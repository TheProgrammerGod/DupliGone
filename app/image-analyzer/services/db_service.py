import psycopg2
from psycopg2.extras import RealDictCursor
from config.settings import DBConfig

db_connection = None

def get_connection():
    global db_connection
    if db_connection is None:
        db_connection = psycopg2.connect(
            dbname=DBConfig.DB_NAME,
            user=DBConfig.DB_USER,
            password=DBConfig.DB_PASSWORD,
            host=DBConfig.DB_HOST,
            port=DBConfig.DB_PORT,
            cursor_factory=RealDictCursor
        )
    return db_connection

def update_photo_metrics(photo_id : str, metrics: dict):
    conn = get_connection()
    try:
        with conn.cursor() as cursor:
            query = """
            UPDATE photos
            SET brightness = %s,
                contrast = %s,
                sharpness = %s,
                face_count = %s,
                smile_score = %s,
                exposure_flatness = %s,
                ahash = %s,
                phash = %s,
                dhash = %s,
                whash = %s
            WHERE id = %s
            """
            cursor.execute(query,(
                metrics['brightness'],
                metrics['contrast'],
                metrics['sharpness'],
                metrics['face_count'],
                metrics['smile_score'],
                metrics['exposure_flatness'],
                metrics['ahash'],
                metrics['phash'],
                metrics['dhash'],
                metrics['whash'],
                photo_id
            ))
    finally:
        conn.commit()
        conn.close()