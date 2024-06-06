from flask import Blueprint, request, jsonify, current_app, session
import os
from api.app.models import User, Book
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
    query = text("SELECT b.logo, b.title, a.name FROM [user] u JOIN library l ON u.user_id = l.user_id JOIN book b ON l.book_id = b.book_id JOIN author_book ab ON b.book_id = ab.book_id JOIN author a ON ab.author_id = a.author_id WHERE u.user_id = :user")
    result = db.session.execute(query, {'user': user_id})

    books = [{'logo': row[0], 'title': row[1], 'author': row[2]} for row in result]
    return jsonify(books)


def register_routes(app):
    app.register_blueprint(bp, url_prefix='/api')
