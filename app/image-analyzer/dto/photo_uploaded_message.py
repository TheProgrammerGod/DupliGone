from pydantic import BaseModel, Field, FilePath
from typing import Optional
from pathlib import Path

class PhotoUploadedMessage(BaseModel):
    photo_id: str = Field(..., description="Unique ID of the uploaded photo")
    photo_path: Path = Field(..., description="Absolute or Relative path to the uploaded photo file")
    metadata: Optional[dict] = Field(default_factory=dict)