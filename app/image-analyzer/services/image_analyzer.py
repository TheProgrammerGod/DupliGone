from PIL import Image
import imagehash
import cv2
import numpy as np
from pathlib import Path

#Load Haar Cascades
_face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
_smile_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_smile.xml')

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
    laplacian = cv2.Laplacian(image, cv2.CV_64F)
    variance = laplacian.var()
    
    return float(variance)

def calculate_brightness(image : np.ndarray) -> float:
    """Calculates the brightness of an image using the mean intensity of grayscale pixels."""
    brightness = image.mean() # Mean intensity value
    return float(brightness)

def calculate_contrast(image : np.ndarray) -> float:
    """Calculates the contrast of an image using the standard deviation of pixel intensities."""
    contrast = image.std() # Standard deviation of pixel intensities
    return float(contrast)

def calculate_face_metrics(image : np.ndarray) -> dict:
    """Detect faces and smiles in the image."""
    faces = _face_cascade.detectMultiScale(image, scaleFactor=1.1, minNeighbors=5)
    
    smile_count = 0
    for (x, y, w, h) in faces:
        roi_gray = image[y:y+h, x:x+w]
        smiles = _smile_cascade.detectMultiScale(roi_gray, scaleFactor=1.7, minNeighbors=22)
        if(len(smiles) > 0):
            smile_count += 1
    
    face_count = len(faces)
    smile_score = smile_count / face_count if face_count > 0 else 0.0
    
    return {
        "face_count": face_count,
        "smile_score": round(smile_score, 2)  # Round to 2 decimal places
    }
    
def calculate_exposure_flatness(image : np.ndarray) -> float:
    """Calculate exposure flatness as the variance of normalized grayscale histogram."""
    hist = cv2.calcHist([image], [0], None, [256], [0, 256])
    hist = hist / hist.sum() # Normalize histogram
    
    # Compute variance of histogram - captures distribution "sharpness"
    flatness_score = np.var(hist)
    
    return float(flatness_score)

def analyze_image(image : np.ndarray) -> dict:
    """"Analyze the image and return various metrics."""
    
    hashes = compute_image_hashes(image)
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY) # Convert to grayscale
    sharpness = calculate_sharpness(gray)
    brightness = calculate_brightness(gray)
    contrast = calculate_contrast(gray)
    face_metrics = calculate_face_metrics(gray)
    exposure_flatness = calculate_exposure_flatness(gray)
    
    return {
        **hashes,
        "sharpness" : sharpness,
        "brightness" : brightness,
        "contrast" : contrast,
        "exposure_flatness" : exposure_flatness,
        **face_metrics
    }
    
    
    