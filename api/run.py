import sys
import os

# Ensure the API directory is in the Python path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), 'api')))

from api.app import create_app

app = create_app()

if __name__ == '__main__':
    app.run(debug=True)
