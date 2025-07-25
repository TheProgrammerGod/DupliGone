#consumer/photo_upload_consumer.py

import pika
import json
from config.settings import RabbitMQConfig
from services.image_loader import load_image
from services.image_analyzer import analyze_image
from services.db_service import update_photo_metrics

    
def callback(ch, method, properties, body):
    try:
#        message = json.loads(body)
        print(f"[INFO] Received message: {body}")
        result = load_image(body)
        if  not result:
            return
        
        image, message = result
        metrics = analyze_image(image)
        print(f"[INFO] Analyzed metrics for {message.photo_id}: {metrics}")
        
        update_photo_metrics(message.photo_id, metrics)
        print(f"[INFO] Saved metrics to DB for photo ID: {message.photo_id}")
        ch.basic_ack(delivery_tag=method.delivery_tag)
    except Exception as e:
        print(f"[ERROR] Failed to process message: {e}")
        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=True)

def start_consumer():
    #Load RabbitMQ configuration from environment variables
    rabbitmq_host = RabbitMQConfig.HOST
    rabbitmq_user = RabbitMQConfig.USER
    rabbitmq_pass = RabbitMQConfig.PASSWORD
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
    print(f"[INFO] Listening for messages on queue: {queue_name}. To exit press CTRL+C")
    channel.start_consuming()