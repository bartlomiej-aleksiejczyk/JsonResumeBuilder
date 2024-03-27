import os
from celery import Celery
import json
import logging
import requests

logging.basicConfig(level=logging.INFO)

app = Celery('worker', broker=os.environ.get('CELERY_BROKER_URL'))

app.conf.task_queues = {
    os.environ.get('QUEUE_NAME'): {
        'exchange': os.environ.get('QUEUE_EXCHANGE'),
        'routing_key': os.environ.get('QUEUE_ROUTING_KEY'),
    },
}

@app.task(bind=True, name=os.environ.get('QUEUE_CELERY_TASK_BUILD_CV'))
def log_message(self, message):
    logging.info(f"Received message: {message}")
    message_id = message.get('id', '')

    url = f"http://springboot-server:8080/{os.environ.get('QUEUE_EXCHANGE')}/api/v1/cv-build-job/{message_id}/status"
    data = {'status': 'IN_PROGRESS'}
    response = requests.post(url, json=data)
    logging.info(f"POST request to {url} returned: {response.status_code}")

if __name__ == '__main__':
    app.worker_main(argv=[
        'worker',
        '--loglevel=INFO', 
        '--concurrency=4',
        '-Q', 'resumebuilder',
    ])
