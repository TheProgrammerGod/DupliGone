import pika
import os
import json
from dotenv import load_dotenv

load_dotenv()

QUEUE_NAME = os.environ.get('PHOTO_UPLOADED_QUEUE')
RABBITMQ_HOST = "localhost"
RABBITMQ_USER = os.environ.get('RABBITMQ_USER')
RABBITMQ_PASS = os.environ.get('RABBITMQ_PASS')
BASE_DIR = os.environ.get('BASE_DIR')

def callback(ch, method, properties, body):
    print("Received message:")
    print(json.loads(body))
    
def main():
    credentials = pika.PlainCredentials(RABBITMQ_USER, RABBITMQ_PASS)
    params = pika.ConnectionParameters(host=RABBITMQ_HOST, credentials=credentials)
    
    connection = pika.BlockingConnection(params)
    channel = connection.channel()
    
    channel.queue_declare(queue=QUEUE_NAME, durable=True)
    channel.basic_consume(queue=QUEUE_NAME, on_message_callback=callback, auto_ack=True)
    
    print(f"Listening for messages on queue: {QUEUE_NAME}")
    channel.start_consuming()

if __name__ == "__main__":
    main()