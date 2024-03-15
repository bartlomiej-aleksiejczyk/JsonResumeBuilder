import os
import json
import subprocess
import requests
from celery import Celery
from jinja2 import Environment, FileSystemLoader

# Initialize Celery
app = Celery('latex_compiler',
             broker=os.environ.get('CELERY_BROKER_URL', 'pyamqp://guest@localhost//'))

@app.task(bind=True)
def compile_latex(self, template_name, data):
    # Set up the Jinja2 environment with custom delimiters for LaTeX
    env = Environment(
        loader=FileSystemLoader('/path/to/templates'),
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

    # Load the LaTeX template using the custom environment
    template = env.get_template(template_name)

    # Render the template with data
    filled_content = template.render(data)

    # Write the filled-in LaTeX code to a new file
    output_filename = 'filled_template.tex'
    with open(output_filename, 'w') as file:
        file.write(filled_content)

    # Compile the LaTeX file into a PDF
    try:
        subprocess.run(['pdflatex', '-interaction=nonstopmode', output_filename], check=True)
        notify_scheduler_api(self.request.id, 'SUCCESS')
    except subprocess.CalledProcessError as error:
        notify_scheduler_api(self.request.id, 'FAILURE', str(error))
        raise error  # Raising error to acknowledge task failure

def notify_scheduler_api(task_id, status, error_message=None):
    """Notify the scheduler API about the task status."""
    payload = {'task_id': task_id, 'status': status}
    if error_message:
        payload['error_message'] = error_message
    requests.post(os.environ.get('SCHEDULER_API_URL', 'http://localhost:5000/status'), json=payload)

@app.task(bind=True)
def cancel_task(self, task_id):
    """Cancel a running task."""
    app.control.revoke(task_id, terminate=True)
