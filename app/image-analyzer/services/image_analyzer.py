from PIL import Image
import imagehash
import cv2
import numpy as np
from pathlib import Path

#Load Haar Cascades
_face_cascade = cv2.CascadeClassifier(cv.data.haarcascades + 'haarcascade_frontalface_default.xml')
_smile_cascade = cv2.CascadeClassifier(cv.data.haarcascades + 'haarcascade_smile.xml')

def cv2_to_pil(cv_image: np.ndarray) -> Image.Image:
    """Convert a cv2 image to a PIL Image."""
    color_converted = cv2.cvtColor(cv_image, cv2.COLOR_BGR2RGB)
    return Image.fromarray(color_converted)

def compute_image_hashes(image : np.ndarray) -> dict:
    """Compute multiple image hashes for comparison."""
    pil_image = cv2_to_pil(image)
    
    return {
        "ahash": str(imagehash.average_hash(pil_image)),
        "phash": str(imagehash.phash(pil_image)),
        "dhash": str(imagehash.dhash(pil_image)),
        "whash": str(imagehash.whash(pil_image)),
    }

def calculate_sharpness(image : np.ndarray) -> float:
    """Calculate the sharpness of an image using the Laplacian variance method."""
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY) # Convert to grayscale
    laplacian = cv2.Laplacian(gray, cv2.CV_64F)
    variance = laplacian.var()
    
    return variance

def calculate_brightness(image : np.ndarray) -> float:
    """Calculates the brightness of an image using the mean intensity of grayscale pixels."""
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY) # Convert to grayscale
    brightness = gray.mean() # Mean intensity value
    return brightness

def calculate_contrast(image : np.ndarray) -> float:
    """Calculates the contrast of an image using the standard deviation of pixel intensities."""
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY) # Convert to grayscale
    contrast = gray.std() # Standard deviation of pixel intensities
    return contrast

def calculate_face_metrics(image : np.ndarray) -> dict:
    """Detect faces and smiles in the image."""
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY) # Convert to grayscale
    faces = _face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5)
    
    smile_count = 0
    for (x, y, w, h) in faces:
        roi_gray = gray[y:y+h, x:x+w]
        smiles = _smile_cascade.detectMultiScale(roi_gray, scaleFactor=1.7, minNeighbors=22)
        if(smiles > 0):
            smile_count += 1
    
    face_count = len(faces)
    smile_score = smile_count / face_count if face_count > 0 else 0.0
    
    return {
        "face_count": face_count,
        "smile_score": round(smile_score, 2)  # Round to 2 decimal places
    }
    