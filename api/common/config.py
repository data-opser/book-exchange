import os
basedir = os.path.abspath(os.path.dirname(__file__))

class Config(object):
    SECRET_KEY = 'nikiviki'
    SQLALCHEMY_DATABASE_URI = 'mssql+pyodbc://admin:nikiviki@book-exchange-db.c90eek8m2zl2.eu-central-1.rds.amazonaws.com,1433/book-exchange?driver=ODBC+Driver+17+for+SQL+Server'
    SQLALCHEMY_TRACK_MODIFICATIONS = False