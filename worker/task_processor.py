import os
import logging
import requests
import json
import subprocess
from celery import Celery
from jinja2 import Environment, FileSystemLoader
from requests.auth import HTTPBasicAuth

logging.basicConfig(level=logging.INFO)

app = Celery('worker', broker=os.environ.get('CELERY_BROKER_URL'))
app.conf.task_queues = {
    os.environ.get('QUEUE_NAME'): {
        'exchange': os.environ.get('QUEUE_EXCHANGE'),
        'routing_key': os.environ.get('QUEUE_ROUTING_KEY'),
    },
}

#TODO: Improve error handling i want everything worng to fail task
@app.task(bind=True, name=os.environ.get('QUEUE_CELERY_TASK_BUILD_CV'))
def process_message_and_compile_latex(self, message):
    try:

        logging.info(f"Received message: {message}")

        message_id = message.get('id', '')
        template_content = message.get('template_content', '')
        data = message.get('json_content', {})

        output_tex_filename = f'filled_template_{message_id}.tex'
        output_pdf_filename = f'filled_template_{message_id}.pdf'

        username = os.environ.get('SPRING_SINGLE_LOGIN')
        password = os.environ.get('SPRING_SINGLE_PASSWORD')
        url = f"http://springboot-server:8080/{os.environ.get('QUEUE_EXCHANGE')}/api/v1/cv-build-job/{message_id}/status"
    
        env = Environment(
            loader=FileSystemLoader('.'),
            block_start_string='\BLOCK{',
            block_end_string='}',
            variable_start_string='\VAR{',
            variable_end_string='}',
            comment_start_string='\#{',
            comment_end_string='}',
            line_statement_prefix='%%',
            line_comment_prefix='%#',
            trim_blocks=True,
            autoescape=False,
        )

        template = env.from_string(template_content)
        filled_content = template.render(data)
        
        with open(output_tex_filename, 'w') as file:
            file.write(filled_content)
        logging.info(f"Template rendered and saved to {output_tex_filename}")

        compile_cmd = ['pdflatex', '-interaction=nonstopmode', output_tex_filename]
        subprocess.run(compile_cmd, check=True)
        logging.info(f"Compiled LaTeX document to PDF: {output_pdf_filename}")

    except Exception as e:
        logging.error(f"Error processing template: {e}")
        return report_task_failure(url, username, password)

  
    status_update = json.dumps({'status': 'IN_PROGRESS'})

    files = {
        'statusUpdate': ('', status_update, 'application/json'),
        'file': (output_pdf_filename, open(output_pdf_filename, 'rb'), 'application/pdf')
    }

    try:
        response = requests.patch(url, files=files, auth=HTTPBasicAuth(username, password))
        logging.info(f"PATCH request to {url} returned: {response.status_code}")
    except Exception as e:
        logging.error(f"Error sending status update and file: {e}")
    finally:
        os.remove(output_tex_filename)
        os.remove(output_pdf_filename)

def report_task_failure(url, username, password):
    status_update_failed = json.dumps({'status': 'FAILED'})
        
    files_failed = {
        'statusUpdate': ('', status_update_failed, 'application/json'),
        'file': ('', '', 'application/octet-stream')
    }
    try:
            response = requests.patch(url, files=files_failed, auth=HTTPBasicAuth(username, password))
            logging.info(f"PATCH request with failure status to {url} returned: {response.status_code}")
    except Exception as e:
            logging.error(f"Error sending failed status update: {e}")

if __name__ == '__main__':
    app.worker_main(argv=[
        'worker',
        '--loglevel=INFO',
        '--concurrency=4',
        '-Q', os.environ.get('QUEUE_NAME', 'resumebuilder'),
    ])
