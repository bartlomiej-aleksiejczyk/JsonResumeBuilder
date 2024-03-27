import os
import json
import logging
import requests
from celery import Celery
from jinja2 import Environment, Template
from requests.auth import HTTPBasicAuth


logging.basicConfig(level=logging.INFO)

app = Celery('worker', broker=os.environ.get('CELERY_BROKER_URL'))
app.conf.task_queues = {
    os.environ.get('QUEUE_NAME'): {
        'exchange': os.environ.get('QUEUE_EXCHANGE'),
        'routing_key': os.environ.get('QUEUE_ROUTING_KEY'),
    },
}

@app.task(bind=True, name=os.environ.get('QUEUE_CELERY_TASK_BUILD_CV'))
def process_message_and_compile_latex(self, message):
    logging.info(f"Received message: {message}")

    message_id = message.get('id', '')
    template_content = message.get('template_content', '')
    data = message.get('json_content', {})  # Assuming this is already a dict, if not, use json.loads()

    try:
        template = Template(template_content)
        filled_content = template.render(data)
        output_filename = f'filled_template_{message_id}.tex'
        with open(output_filename, 'w') as file:
            file.write(filled_content)
        logging.info(f"Template rendered and saved to {output_filename}")
    except Exception as e:
        logging.error(f"Error rendering template: {e}")
        return

    username = os.environ.get('SPRING_SINGLE_LOGIN')
    password = os.environ.get('SPRING_SINGLE_PASSWORD')
    url = f"http://springboot-server:8080/{os.environ.get('QUEUE_EXCHANGE')}/api/v1/cv-build-job/{message_id}/status"
    status_update = {'status': 'IN_PROGRESS'}

    try:
        response = requests.post(url, json=status_update, auth=HTTPBasicAuth(username, password))
        logging.info(f"POST request to {url} returned: {response.status_code}")
    except Exception as e:
        logging.error(f"Error sending status update: {e}")

if __name__ == '__main__':
    app.worker_main(argv=[
        'worker',
        '--loglevel=INFO',
        '--concurrency=4',
        '-Q', os.environ.get('QUEUE_NAME', 'resumebuilder'),
    ])
