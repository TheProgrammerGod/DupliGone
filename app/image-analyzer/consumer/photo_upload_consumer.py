#consumer/photo_upload_consumer.py

import pika
import json
from config.settings import RabbitMQConfig
from services.image_loader import load_image
from services.image_analyzer import analyze_image
from services.db_service import update_photo_metrics

# Constants
MAX_RETRIES = 3
BASE_DELAY_MS = 2000 # 2 seconds base

    
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
        # Extract retry count from headers or message properties
        retry_count = body.get('retry_count', 0)
        
        if retry_count < MAX_RETRIES:
            # Exponential backoff delay
            delay_ms = BASE_DELAY_MS * (2 ** retry_count)
            
            print(f"[WARN] Processing failed, retrying (attempt {retry_count + 1}) after {delay_ms}ms : {e}")
            
            # Re-publish the message with increased retry count and delay
            retry_message = {
                **body,
                'retry_count': retry_count + 1
            }
            
            prop = pika.BasicProperties(
                expiration=str(delay_ms),  # Set message TTL (in milliseconds)
                content_type='application/json'
            )
            
            ch.basic_publish(
                exchange='',
                routing_key=RabbitMQConfig.QUEUE_NAME,
                body=json.dumps(retry_message),
                properties=prop
            )
        else:
            print(f"[ERROR] Max retries reached for photoId {body.get('photo_id')}. Discarding message: {e}")
        ch.basic_ack(delivery_tag=method.delivery_tag)
        

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