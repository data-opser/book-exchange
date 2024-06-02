from datetime import datetime
from api.app import db
from werkzeug.security import generate_password_hash, check_password_hash


class User(db.Model):
    user_id = db.Column(db.Integer, primary_key=True)
    login = db.Column(db.String(50), index=True, unique=True, nullable=False)
    email = db.Column(db.String(50), index=True, unique=True, nullable=False)
    password_hash = db.Column(db.String(256), nullable=False)
    name = db.Column(db.String(50), nullable=False)
    lastname = db.Column(db.String(50))
    age = db.Column(db.Integer)

    reviews = db.relationship('Review', backref='user', lazy='dynamic', cascade='all, delete-orphan')
    libraries = db.relationship('Library', backref='user', lazy='dynamic', cascade='all, delete-orphan')
    exchanges_offered = db.relationship('Exchange', foreign_keys='Exchange.user_id_offer', backref='user_offer', lazy='dynamic', cascade='all, delete-orphan')
    exchanges_received = db.relationship('Exchange', foreign_keys='Exchange.user_id_reply', backref='user_reply', lazy='dynamic', cascade='all, delete-orphan')
    ratings = db.relationship('Rating', backref='user', lazy='dynamic', cascade='all, delete-orphan')

    def set_password(self, password):
        self.password_hash = generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.password_hash, password)

    def __repr__(self):
        return f'<User {self.login}>'


class Book(db.Model):
    book_id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(50), index=True, nullable=False)
    logo = db.Column(db.String(256), index=True, nullable=False)
    description = db.Column(db.String(500))
    release_date = db.Column(db.Date, nullable=False, default=datetime.utcnow)

    authors = db.relationship('AuthorBook', backref='book', lazy='dynamic', cascade='all, delete-orphan')
    genres = db.relationship('BookGenre', backref='book', lazy='dynamic', cascade='all, delete-orphan')
    reviews = db.relationship('Review', backref='book', lazy='dynamic', cascade='all, delete-orphan')
    libraries = db.relationship('Library', backref='book', lazy='dynamic', cascade='all, delete-orphan')
    exchanges_offered = db.relationship('Exchange', foreign_keys='Exchange.book_id_offer', backref='book_offer', lazy='dynamic', cascade='all, delete-orphan')
    exchanges_received = db.relationship('Exchange', foreign_keys='Exchange.book_id_reply', backref='book_reply', lazy='dynamic', cascade='all, delete-orphan')
    ratings = db.relationship('Rating', backref='book', lazy='dynamic', cascade='all, delete-orphan')

    def __repr__(self):
        return f'<Book {self.title}>'


class Review(db.Model):
    review_id = db.Column(db.Integer, primary_key=True)
    review = db.Column(db.String(500), nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('user.user_id'), nullable=False)
    book_id = db.Column(db.Integer, db.ForeignKey('book.book_id'), nullable=False)

    def __repr__(self):
        return f'<Review {self.review}>'


class Library(db.Model):
    user_id = db.Column(db.Integer, db.ForeignKey('user.user_id'), primary_key=True, nullable=False)
    book_id = db.Column(db.Integer, db.ForeignKey('book.book_id'), primary_key=True, nullable=False)

    def __repr__(self):
        return f'<Library User {self.user_id} Book {self.book_id}>'


class Exchange(db.Model):
    exchange_id = db.Column(db.Integer, primary_key=True)
    book_id_offer = db.Column(db.Integer, db.ForeignKey('book.book_id'), nullable=False)
    user_id_offer = db.Column(db.Integer, db.ForeignKey('user.user_id'), nullable=False)
    comment_offer = db.Column(db.String(150), nullable=False)
    book_id_reply = db.Column(db.Integer, db.ForeignKey('book.book_id'))
    user_id_reply = db.Column(db.Integer, db.ForeignKey('user.user_id'))
    comment_reply = db.Column(db.String(150))
    is_complete = db.Column(db.Boolean, default=False, nullable=False)

    def __repr__(self):
        return f'<Exchange {self.exchange_id}>'


class Author(db.Model):
    author_id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50), nullable=False)

    books = db.relationship('AuthorBook', backref='author', lazy='dynamic', cascade='all, delete-orphan')

    def __repr__(self):
        return f'<Author {self.name}>'


class AuthorBook(db.Model):
    book_id = db.Column(db.Integer, db.ForeignKey('book.book_id'), primary_key=True, nullable=False)
    author_id = db.Column(db.Integer, db.ForeignKey('author.author_id'), primary_key=True, nullable=False)

    def __repr__(self):
        return f'<AuthorBook Book {self.book_id} Author {self.author_id}>'


class Genre(db.Model):
    genre_id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50), nullable=False)

    books = db.relationship('BookGenre', backref='genre', lazy='dynamic', cascade='all, delete-orphan')

    def __repr__(self):
        return f'<Genre {self.name}>'


class BookGenre(db.Model):
    book_id = db.Column(db.Integer, db.ForeignKey('book.book_id'), primary_key=True, nullable=False)
    genre_id = db.Column(db.Integer, db.ForeignKey('genre.genre_id'), primary_key=True, nullable=False)

    def __repr__(self):
        return f'<BookGenre Book {self.book_id} Genre {self.genre_id}>'


class Rating(db.Model):
    book_id = db.Column(db.Integer, db.ForeignKey('book.book_id'), primary_key=True, nullable=False)
    vote = db.Column(db.Integer, nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('user.user_id'), primary_key=True, nullable=False)

    def __repr__(self):
        return f'<Rating Book {self.book_id} User {self.user_id} Vote {self.vote}>'
