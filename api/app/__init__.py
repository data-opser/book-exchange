import sys
import os

# Ensure the API directory is in the Python path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__))))

from flask import Flask, send_from_directory, session
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_cors import CORS
from api.common.config import Config


db = SQLAlchemy()
migrate = Migrate()

static_folder_path = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..', 'Frontend'))

def create_app():
    app = Flask(__name__, static_folder=static_folder_path)

    app.config.from_object(Config)
    CORS(app)

    db.init_app(app)
    migrate.init_app(app, db)

    from api.app import models
    from api.app.routes import register_routes
    register_routes(app)

    @app.route('/')
    def serve_home():
        home_path = os.path.join(static_folder_path, 'homepage', 'main.html')
        print(f"Serving home from: {home_path}")
        return send_from_directory(os.path.join(static_folder_path, 'homepage'), 'main.html')

    @app.route('/<path:path>')
    def serve_static(path):
        print(f"Requested path: {path}")
        return send_from_directory(static_folder_path, path)

    # Register a before_first_request handler to clear session
    @app.before_request
    def clear_session():
        session.clear()

    return app
