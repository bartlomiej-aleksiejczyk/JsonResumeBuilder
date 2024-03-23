import os
import logging
from celery import Celery

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Celery('message_listener',
             broker=os.environ.get('CELERY_BROKER_URL'))

@app.task(bind=True, queue=os.environ.get('RABBITMQ_QUEUE'))
def listen_and_log(self, message):
    logger.info(f"Received message: {message}")

if __name__ == '__main__':
    app.worker_main()
