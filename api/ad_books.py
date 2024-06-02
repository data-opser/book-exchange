import sys
import os
from datetime import datetime

# Ensure the API directory is in the Python path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__))))

from app import create_app, db
from app.models import Book



app = create_app()
app.app_context().push()

books = [
    {
        'title': 'The Great Gatsby',
        'description': 'A novel set in the 1920s about Jay Gatsby\'s love for Daisy Buchanan.',
        'logo': 'the_great_gatsby.png',
        'release_date': '1925-04-10'
    },
    {
        'title': 'To Kill a Mockingbird',
        'description': 'A story about racial injustice in the Deep South, seen through the eyes of young Scout Finch.',
        'logo': 'to_kill_a_mockingbird.png',
        'release_date': '1960-07-11'
    },
    {
        'title': '1984',
        'description': 'A dystopian novel by George Orwell about totalitarianism and surveillance.',
        'logo': '1984.png',
        'release_date': '1949-06-08'
    },
    {
        'title': 'Pride and Prejudice',
        'description': 'A romantic novel by Jane Austen about the manners and matrimonial machinations among the British gentry of the early 19th century.',
        'logo': 'pride_and_prejudice.png',
        'release_date': '1813-01-28'
    },
    {
        'title': 'The Catcher in the Rye',
        'description': 'A novel by J.D. Salinger about the experiences of a young man named Holden Caulfield.',
        'logo': 'the_catcher_in_the_rye.png',
        'release_date': '1951-07-16'
    }
]

# Convert the string dates to datetime.date objects and add to the database
for book in books:
    release_date = datetime.strptime(book['release_date'], '%Y-%m-%d').date()
    new_book = Book(
        title=book['title'],
        description=book['description'],
        logo=book['logo'],
        release_date=release_date
    )
    db.session.add(new_book)

db.session.commit()
