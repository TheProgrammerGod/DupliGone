from consumer.photo_upload_consumer import start_consumer
import cv2
from services.image_analyzer import analyze_image

if __name__ == "__main__":
    print("Starting photo upload consumer...")
    start_consumer()
    # image = cv2.imread("../dev_photos/60f6c12e-e48c-4376-b2c1-4fa82b6e0b84/b5cea6ce-8d3a-44fd-b71f-a103575740cd.jpg")
    # print(analyze_image(image))