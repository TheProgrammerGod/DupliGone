# Photo Library Cleaner - Architecture Overview

## 🎯 Objective

The Photo Library Cleaner system helps users clean up large, unorganized photo libraries by automatically:

* Scanning a folder of photos
* Grouping visually similar photos
* Scoring photos for quality (sharpness, brightness, face detection)
* Suggesting the best photo in each group and flagging others for deletion

---

## 🧱 High-Level Architecture

```
              +-----------------+      
              |     Frontend    | (React + Tailwind)
              |-----------------|
              | Upload UI       |
              | Progress page   |
              | Cluster review  |
              +--------+--------+
                       |
                       v
              +--------+--------+
              |      API Gateway | (Spring Boot REST)
              +--------+--------+
                       |
          +------------+-------------+
          |                          |
          v                          v
+------------------+     +------------------------+
| Job & Photo Mgmt |     |  Task Queue (RabbitMQ) |
| (Spring Service) |     +------------------------+
| - Save metadata  |             |
| - Track status   |             v
+------------------+     +------------------------+
                          | Worker Service         |
                          | (Rabbit Listener)      |
                          | - Download photo       |
                          | - Send to analyzer     |
                          | - Save results         |
                          +-----------+------------+
                                      |
                                      v
                        +-------------+------------+
                        |   Python Image Analyzer  |
                        |   (OpenCV, imagehash)    |
                        +--------------------------+

---

## 🧪 Clustering Phase (Batch Job)

Once all photos are processed:
- A batch job (Spring Batch or manual trigger) collects all pHashes
- Clusters similar images (via Python microservice)
- Stores cluster groups and best photo per group in DB

---

## 🗃 Data Flow

1. User uploads photos via UI
2. Backend creates a new `Job` entry and stores metadata
3. Each photo is submitted as a message to `photo.process` queue
4. Worker consumes message, analyzes photo, and saves results to DB
5. When all photos in a job are processed, clustering is triggered
6. UI polls `/status?jobId=...` until results are ready
7. User sees cluster groups and selects/deletes unwanted images

---

## 🛠 Technology Stack

### Frontend:
- React
- Tailwind CSS
- Axios / React Query

### Backend:
- Java + Spring Boot
- Spring Web (REST API)
- Spring Data JPA (PostgreSQL)
- Spring AMQP (RabbitMQ integration)
- Flyway (DB migrations)

### Async Processing:
- RabbitMQ (task queue)
- Spring AMQP listeners (photo processor)

### Batch Clustering:
- Spring Batch or manual job trigger
- Python service for DBSCAN or Faiss-based clustering

### Image Analysis:
- Python microservice
  - OpenCV (sharpness, brightness)
  - imagehash (pHash/dHash)
  - face_recognition (optional)

### Infra:
- PostgreSQL (DB)
- Docker + Docker Compose
- Optional: NGINX, GitHub Actions, S3/Cloudinary

---

## 🔄 Resilience & Reliability
- Each photo task is idempotent and retry-safe
- Every stage writes progress to DB to allow crash recovery
- Queue-based design allows horizontal scaling of workers
- Polling-based status API simplifies frontend logic

---

## 📁 Project Structure (Top Level)

```

photo-library-cleaner/
├── backend/           # Spring Boot app
├── frontend/          # React app
├── image-analyzer/    # Python microservice
├── infrastructure/    # Docker Compose, NGINX, etc.
├── scripts/           # Seeders, setup scripts
├── docs/              # Architecture, contracts, etc.
├── .env               # Shared config values
└── README.md

```

---

## 🚀 Next Steps
- Implement DB schema (Job, Photo, Cluster)
- Set up RabbitMQ config + listener
- Build the photo processing pipeline
- Create the clustering trigger logic (manual or automatic)
- Design UI screens for upload, progress, and results

```
