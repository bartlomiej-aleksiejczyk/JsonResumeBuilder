import os
from celery import Celery
import json
import logging
import requests
from requests.auth import HTTPBasicAuth

logging.basicConfig(level=logging.INFO)

app = Celery('worker', broker=os.environ.get('CELERY_BROKER_URL'))

app.conf.task_queues = {
    os.environ.get('QUEUE_NAME'): {
        'exchange': os.environ.get('QUEUE_EXCHANGE'),
        'routing_key': os.environ.get('QUEUE_ROUTING_KEY'),
    },
}

def get_csrf_token(server_url, auth):
    """Get CSRF token from the server."""
    # Assuming the CSRF token is available at /csrf endpoint
    csrf_url = f"{server_url}/csrf"
    response = requests.get(csrf_url, auth=auth)
    if response.status_code == 200:
        return response.json().get('token')
    else:
        logging.error(f"Failed to obtain CSRF token, status code: {response.status_code}")
        return None

@app.task(bind=True, name=os.environ.get('QUEUE_CELERY_TASK_BUILD_CV'))
def log_message(self, message):
    logging.info(f"Received message: {message}")
    message_id = message.get('id', '')

    username = os.environ.get('SPRING_SINGLE_LOGIN')
    password = os.environ.get('SPRING_SINGLE_PASSWORD')
    auth = HTTPBasicAuth(username, password)

    server_url = f"http://springboot-server:8080/{os.environ.get('IMAGE_NAME')}/"
    url = f"{server_url}api/v1/cv-build-job/{message_id}/status"

    csrf_token = get_csrf_token(server_url, auth)
    if not csrf_token:
        logging.error("CSRF token could not be obtained. Exiting task.")
        return

    headers = {
        "Content-Type": "application/json",
        "X-CSRF-TOKEN": csrf_token,
    }

    data = {'status': 'IN_PROGRESS'}
    response = requests.patch(url, json=data, headers=headers, auth=auth)
    
    logging.info(f"PATCH request to {url} returned: {response.status_code}, {response.text}")

if __name__ == '__main__':
    app.worker_main(argv=[
        'worker',
        '--loglevel=INFO', 
        '--concurrency=4',
        '-Q', 'resumebuilder',
    ])
