from PIL import Image
import imagehash
import cv2
import numpy as np
from pathlib import Path

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