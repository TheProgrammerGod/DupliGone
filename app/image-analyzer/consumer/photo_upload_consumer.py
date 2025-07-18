#consumer/photo_upload_consumer.py

import os
import pika
import json
from config.settings import RabbitMQConfig
from services.image_loader import load_image

def handle_photo_upload(message: dict):
    # Placeholder for processing the uploaded photo
    print("Processing photo upload:", message)
    
def callback(ch, method, properties, body):
    try:
        message = json.loads(body)
        print(f"[INFO] Received message: {message}")
        result = load_image(message)
        if result:
            image, message = result
            #TODO : Do image processing here
    except Exception as e:
        print(f"Failed to process message: {e}")
        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=True)

def start_consumer():
    #Load RabbitMQ configuration from environment variables
    rabbitmq_host = RabbitMQConfig.RABBITMQ_HOST
    rabbitmq_user = RabbitMQConfig.RABBITMQ_USER
    rabbitmq_pass = RabbitMQConfig.RABBITMQ_PASS
    queue_name = RabbitMQConfig.QUEUE_NAME
    
    #Setup credentials and connection parameters
    credentials = pika.PlainCredentials(rabbitmq_user, rabbitmq_pass)
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host=rabbitmq_host, credentials=credentials)
    )
    channel = connection.channel()
    
    #Ensure that the queue exists(idempotent)
    channel.queue_declare(queue=queue_name, durable=True)
    
    #Start consuming messages from the queue
    channel.basic_consume(queue=queue_name, on_message_callback=callback)
    print(f"Listening for messages on queue: {queue_name}. To exit press CTRL+C")
    channel.start_consuming()