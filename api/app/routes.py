from flask import Blueprint, request, jsonify, current_app, session
import os
from api.app.models import User, Book, Author, AuthorBook, Exchange
from api.app import db, static_folder_path
from werkzeug.utils import secure_filename
from flask import render_template_string
from sqlalchemy.sql import text

bp = Blueprint('api', __name__)


@bp.route('/')
def serve_home_with_clear_storage():
    with open(os.path.join(static_folder_path, 'homepage', 'main.html')) as file:
        content = file.read()
    # Injecting script to clear local storage
    script = "<script>localStorage.clear();</script>"
    content_with_script = content.replace("</head>", f"{script}</head>")
    return render_template_string(content_with_script)


@bp.route('/register', methods=['POST'])
def register():
    data = request.form.to_dict()
    profile_image = request.files.get('profile_image')
    profile_image_filename = 'profile_placeholder.png'  # default placeholder image

    if User.query.filter_by(email=data['email']).first():
        return jsonify({'error': 'Email already registered'}), 400
    if User.query.filter_by(login=data['login']).first():
        return jsonify({'error': 'Login already taken'}), 400

    user = User(
        login=data['login'],
        email=data['email'],
        name=data['name'],
        lastname=data.get('lastname', ''),
        age=data.get('age', None),
    )
    user.set_password(data['password'])
    db.session.add(user)
    db.session.commit()

    # Save profile image after committing the user to get the user ID
    if profile_image:
        filename = secure_filename(f"{user.login}.png")
        profile_image.save(os.path.join(current_app.config['UPLOAD_FOLDER'], filename))
        user.profile_image = filename
        db.session.commit()

    return jsonify({'message': 'User registered successfully'}), 201


@bp.route('/login', methods=['POST'])
def login():
    data = request.json
    print("Login Data:", data)  # Debugging line
    user = User.query.filter_by(email=data['email']).first()
    if user and user.check_password(data['password']):
        return jsonify({'message': 'Login successful', 'user_id': user.user_id}), 200
    else:
        return jsonify({'error': 'Invalid email or password'}), 400


@bp.route('/logout', methods=['POST'])
def logout():
    session.clear()  # Clear the session data
    return jsonify({'message': 'Logout successful'}), 200


@bp.route('/users/<int:user_id>', methods=['GET'])
def get_user(user_id):
    user = User.query.get(user_id)
    if not user:
        return jsonify({'error': 'User not found'}), 404
    return jsonify({
        'user_id': user.user_id,
        'login': user.login,
        'email': user.email,
        'name': user.name,
        'lastname': user.lastname,
        'age': user.age,
        'profile_image': user.profile_image
    })


@bp.route('/profile', methods=['GET'])
def get_profile():
    user_id = request.args.get('user_id')
    if not user_id:
        return jsonify({'error': 'User ID is required'}), 400

    user = User.query.get(user_id)
    if user:
        profile_image_path = f'/resources/profiles/{user.login}.png'
        print(profile_image_path)
        if not os.path.exists(os.path.join(static_folder_path, profile_image_path)):
            profile_image_path = '/resources/profiles/profile_placeholder.png'
        return jsonify({
            'profile_image': profile_image_path,
            'name': user.name,
            'lastname': user.lastname,
            'age': user.age
        }), 200
    return jsonify({'error': 'User not found'}), 404


@bp.route('/user-profile', methods=['GET'])
def user_profile():
    user_id = session.get('user_id')
    if user_id:
        user = User.query.get(user_id)
        return jsonify({
            'logged_in': True,
            'profile_image': user.profile_image
        })
    return jsonify({'logged_in': False})


@bp.route('/users/<int:user_id>', methods=['PUT'])
def update_user(user_id):
    user = User.query.get(user_id)
    if not user:
        return jsonify({'error': 'User not found'}), 404

    data = request.form
    profile_image = request.files.get('profile_image')

    user.name = data.get('name', user.name)
    user.lastname = data.get('lastname', user.lastname)
    user.age = data.get('age', user.age)

    if profile_image:
        filename = secure_filename(f"{user.login}.png")
        profile_image.save(os.path.join(current_app.config['UPLOAD_FOLDER'], filename))
        user.profile_image = filename

    db.session.commit()
    return jsonify({'message': 'User updated successfully'})


@bp.route('/last-releases', methods=['GET'])
def get_last_releases():
    last_releases = Book.query.order_by(Book.release_date.desc()).limit(3).all()
    return jsonify([{
        'title': book.title,
        'description': book.description,
        'logo': book.logo,
        'release_date': book.release_date.strftime('%Y-%m-%d')
    } for book in last_releases])


@bp.route('/users/<int:user_id>/library', methods=['GET'])
def get_user_library(user_id):
    query = text("SELECT b.book_id, b.logo, b.title, a.name FROM [user] u JOIN library l ON u.user_id = l.user_id JOIN book b ON l.book_id = b.book_id JOIN author_book ab ON b.book_id = ab.book_id JOIN author a ON ab.author_id = a.author_id WHERE u.user_id = :user")
    result = db.session.execute(query, {'user': user_id})

    books = [{'book_id': row[0], 'logo': row[1], 'title': row[2], 'author': row[3]} for row in result]
    return jsonify(books)


@bp.route('/books/<int:book_id>', methods=['GET'])
def get_book(book_id):
    book = Book.query.get(book_id)
    if not book:
        return jsonify({'error': 'Book not found'}), 404
    author = Author.query.join(AuthorBook).join(Book).filter(Book.book_id == book_id).first()
    return jsonify({
        'book_id': book.book_id,
        'title': book.title,
        'author': author.name if author else '',
        'logo': book.logo
    })


@bp.route('/trades', methods=['POST'])
def create_trade():
    data = request.json
    user_id = data.get('user_id')
    book_id = data.get('book_id')
    description = data.get('description')

    if not user_id or not book_id or not description:
        return jsonify({'error': 'Missing data'}), 400

    trade = Exchange(
        user_id_offer=user_id,
        book_id_offer=book_id,
        comment_offer=description,
        is_complete=False
    )

    db.session.add(trade)
    db.session.commit()

    return jsonify({'success': 'Trade created successfully'})



@bp.route('/users/<int:user_id>/active_offers', methods=['GET'])
def get_user_active_offers(user_id):
    query = text("""
        SELECT e.exchange_id, u1.name as user_name, u1.profile_image,   
               b1.title as book_offered_title, b1.logo as book_offered_logo, 
               b2.title as book_reply_title, b2.logo as book_reply_logo,
               a1.name as book_offered_author, a2.name as book_reply_author,
               e.comment_offer, e.comment_reply,
               e.book_id_offer, e.book_id_reply, 
               e.user_id_offer, e.user_id_reply,
               u2.name as user_name_reply, u2.profile_image as profile_image_reply
        FROM exchange e
        JOIN [user] u1 ON e.user_id_offer = u1.user_id
        LEFT JOIN [user] u2 ON e.user_id_reply = u2.user_id
        JOIN book b1 ON e.book_id_offer = b1.book_id
        LEFT JOIN author_book ab1 ON b1.book_id = ab1.book_id
        LEFT JOIN author a1 ON ab1.author_id = a1.author_id
        LEFT JOIN book b2 ON e.book_id_reply = b2.book_id
        LEFT JOIN author_book ab2 ON b2.book_id = ab2.book_id
        LEFT JOIN author a2 ON ab2.author_id = a2.author_id
        WHERE e.user_id_offer = :user_id AND e.is_complete = 0
    """)
    result = db.session.execute(query, {'user_id': user_id})
    offers = [
        {
            'exchange_id': row[0],
            'user_name': row[1],
            'user_profile_image': row[2],
            'book_offered_title': row[3],
            'book_offered_logo': row[4],
            'book_reply_title': row[5],
            'book_reply_logo': row[6],
            'book_offered_author': row[7],
            'book_reply_author': row[8],
            'comment_offer': row[9],
            'comment_reply': row[10],
            'user_id_offer': row[13],
            'user_id_reply': row[14],
            'user_name_reply': row[15],
            'user_profile_image_reply': row[16],
        }
        for row in result
    ]
    return jsonify(offers)


@bp.route('/active-trades', methods=['GET'])
def get_active_trades():
    query = text("""
        SELECT e.exchange_id, u1.name as user_name, u1.profile_image,   
               b1.title as book_offered_title, b1.logo as book_offered_logo, 
               b2.title as book_reply_title, b2.logo as book_reply_logo,
               a1.name as book_offered_author, a2.name as book_reply_author,
               e.comment_offer, e.comment_reply,
               e.book_id_offer, e.book_id_reply, 
               e.user_id_offer, e.user_id_reply,
               u2.name as user_name_reply, u2.profile_image as profile_image_reply
        FROM exchange e
        JOIN [user] u1 ON e.user_id_offer = u1.user_id
        LEFT JOIN [user] u2 ON e.user_id_reply = u2.user_id
        JOIN book b1 ON e.book_id_offer = b1.book_id
        LEFT JOIN author_book ab1 ON b1.book_id = ab1.book_id
        LEFT JOIN author a1 ON ab1.author_id = a1.author_id
        LEFT JOIN book b2 ON e.book_id_reply = b2.book_id
        LEFT JOIN author_book ab2 ON b2.book_id = ab2.book_id
        LEFT JOIN author a2 ON ab2.author_id = a2.author_id
        WHERE e.is_complete = 0
    """)
    result = db.session.execute(query)
    trades = [
        {
            'exchange_id': row[0],
            'user_name': row[1],
            'user_profile_image': row[2],
            'book_offered_title': row[3],
            'book_offered_logo': row[4],
            'book_reply_title': row[5],
            'book_reply_logo': row[6],
            'book_offered_author': row[7],
            'book_reply_author': row[8],
            'comment_offer': row[9],
            'comment_reply': row[10],
            'user_id_offer': row[13],
            'user_id_reply': row[14],
            'user_name_reply': row[15],
            'user_profile_image_reply': row[16],
        }
        for row in result
    ]
    return jsonify(trades)


@bp.route('/trades/<int:trade_id>', methods=['PUT'])
def update_trade(trade_id):
    trade = Exchange.query.get(trade_id)
    if not trade:
        return jsonify({'error': 'Trade not found'}), 404

    data = request.json
    trade.user_id_reply = data.get('user_id_reply')
    trade.book_id_reply = data.get('book_id_reply')
    trade.comment_reply = data.get('comment_reply')
    trade.is_complete = False

    db.session.commit()
    return jsonify({'success': 'Trade updated successfully'})


@bp.route('/trades/<int:trade_id>', methods=['DELETE'])
def delete_trade(trade_id):
    trade = Exchange.query.get(trade_id)
    if not trade:
        return jsonify({'error': 'Trade not found'}), 404

    db.session.delete(trade)
    db.session.commit()
    return jsonify({'success': 'Trade deleted successfully'}), 200


def register_routes(app):
    app.register_blueprint(bp, url_prefix='/api')
