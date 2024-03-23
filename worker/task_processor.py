import os
from celery import Celery
import logging

logging.basicConfig(level=logging.INFO)

app = Celery('worker', broker=os.environ.get('CELERY_BROKER_URL'))

app.conf.task_queues = {
    os.environ.get('CELERY_QUEUE', 'default'): {
        'exchange': os.environ.get('CELERY_QUEUE', 'default'),
        'routing_key': os.environ.get('CELERY_QUEUE', 'default'),
    },
}

@app.task(bind=True)
def log_message(self, message):
    logging.info(f"Received message: {message}")

if __name__ == '__main__':
    app.worker_main(argv=[
        'worker',
        '--loglevel=INFO',
        '--concurrency=4',
        '-Q', os.environ.get('CELERY_QUEUE', 'default'),
    ])
