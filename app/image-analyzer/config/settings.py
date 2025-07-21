#config/settings.py

import os
from dotenv import load_dotenv

#Load environment variables from .env file
load_dotenv()

class RabbitMQConfig:
    HOST = os.environ.get('RABBITMQ_HOST', 'localhost')
    USER = os.environ.get('RABBITMQ_USER')
    PASSWORD = os.environ.get('RABBITMQ_PASS')
    QUEUE_NAME = os.environ.get('PHOTO_UPLOADED_QUEUE', 'photo.uploaded.queue')
    
class DBConfig:
    HOST = os.environ.get('POSTGRES_HOST', 'localhost')
    PORT = os.environ.get('POSTGRES_PORT', '5432')
    USER = os.environ.get('POSTGRES_USER')
    PASSWORD = os.environ.get('POSTGRES_PASSWORD')
    DATABASE = os.environ.get('POSTGRES_DB')