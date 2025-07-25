import json
import cv2
from pathlib import Path
from pydantic import ValidationError
from dto.photo_uploaded_message import PhotoUploadedMessage

def load_image(body: bytes):
    BASE_DIR = Path("../dev_photos") #Fix this path when deploying using Docker
    try:
        data = json.loads(body)
        message = PhotoUploadedMessage(**data)

    except (json.JSONDecodeError, ValidationError)  as e:
        print(f"[ERROR] Invalid message format: {e}")
        return
    
    image_path = BASE_DIR / message.photo_path
    if not image_path.exists():
        print(f"[ERROR] File does not exist: {image_path}")
        return

    image = cv2.imread(str(image_path))
    if image is None:
        print(f"[ERROR] Unable to read image file: {image_path}")
        return

    print(f"[INFO] Successfully loaded image: {image_path}")
    
    return image, message