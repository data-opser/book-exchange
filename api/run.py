import sys
import os
from flask import Flask, session
from flask.cli import with_appcontext
import click

# Ensure the API directory is in the Python path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), 'api')))

from api.app import create_app

app = create_app()

@app.cli.command("clear-session")
@app.before_request
def clear_session():
    session.clear()

if __name__ == '__main__':
    app.run(debug=True)
