import os
import subprocess
import requests
from celery import Celery
from jinja2 import Environment, FileSystemLoader

app = Celery('latex_compiler',
             broker=os.environ.get('CELERY_BROKER_URL'))

templates_directory = os.environ.get('LATEX_TEMPLATE_DIR', '/path /to/templates')

@app.task(bind=True, queue=os.environ.get('RABBITMQ_QUEUE'))
def compile_latex(self, template_name, data):
    env = Environment(
        loader=FileSystemLoader(templates_directory),
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
    template = env.get_template(template_name)
    filled_content = template.render(data)

    output_filename = 'filled_template.tex'
    with open(output_filename, 'w') as file:
        file.write(filled_content)

    try:
        subprocess.run(['pdflatex', '-interaction=nonstopmode', output_filename], check=True)
        notify_scheduler_api(self.request.id, 'SUCCESS')
    except subprocess.CalledProcessError as error:
        notify_scheduler_api(self.request.id, 'FAILURE', str(error))
        raise error

def notify_scheduler_api(task_id, status, error_message=None):
    payload = {'task_id': task_id, 'status': status}
    if error_message:
        payload['error_message'] = error_message
    requests.post(os.environ.get('SCHEDULER_API_URL'), json=payload)

@app.task(bind=True)
def cancel_task(self, task_id):
    app.control.revoke(task_id, terminate=True)
