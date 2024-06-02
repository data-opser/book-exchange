from flask import Blueprint, request, jsonify
from api.app.models import User, Book
from api.app import db

bp = Blueprint('api', __name__)


@bp.route('/register', methods=['POST'])
def register():
    data = request.json
    print("Register Data:", data)  # Debugging line
    if User.query.filter_by(email=data['email']).first():
        return jsonify({'error': 'Email already registered'}), 400
    if User.query.filter_by(login=data['login']).first():
        return jsonify({'error': 'Login already taken'}), 400

    user = User(
        login=data['login'],
        email=data['email'],
        name=data['name'],
        lastname=data.get('lastname', ''),
        age=data.get('age', None)
    )
    user.set_password(data['password'])
    db.session.add(user)
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


@bp.route('/last-releases', methods=['GET'])
def get_last_releases():
    last_releases = Book.query.order_by(Book.release_date.desc()).limit(3).all()
    return jsonify([{
        'title': book.title,
        'description': book.description,
        'logo': book.logo,
        'release_date': book.release_date.strftime('%Y-%m-%d')
    } for book in last_releases])


def register_routes(app):
    app.register_blueprint(bp, url_prefix='/api')
