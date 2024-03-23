import os
import json
import logging
import pika
from jinja2 import Environment, FileSystemLoader
from jinja2.exceptions import TemplateError

logging.basicConfig(level=logging.INFO)

# Configuration
RABBITMQ_URL = os.environ.get('CELERY_BROKER_URL')
QUEUE_NAME = os.environ.get('RABBITMQ_QUEUE', 'default')
TEMPLATES_DIRECTORY = os.environ.get('LATEX_TEMPLATE_DIR', '/path/to/templates')

def compile_latex(template_name, data):
    try:
        env = Environment(
            loader=FileSystemLoader(TEMPLATES_DIRECTORY),
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
        logging.info(f"Template {template_name} compiled with data.")
        return True
    except TemplateError as te:
        logging.error(f"Template error: {te}")
        return False
    except Exception as e:
        logging.error(f"Error compiling LaTeX: {e}")
        return False

def on_message(channel, method_frame, header_frame, body):
    message = json.loads(body)
    template_name = message.get('template_name')
    data = message.get('data')
    success = compile_latex(template_name, data)

    if success:
        channel.basic_ack(delivery_tag=method_frame.delivery_tag)
    else:
        logging.error(f"Failing and not requeuing message: {message}")
        channel.basic_ack(delivery_tag=method_frame.delivery_tag)

def main():
    params = pika.URLParameters(RABBITMQ_URL)
    connection = pika.BlockingConnection(params)
    channel = connection.channel()

    channel.queue_declare(queue=QUEUE_NAME, durable=True)

    channel.basic_consume(queue=QUEUE_NAME, on_message_callback=on_message)

    try:
        logging.info('Starting to consume messages...')
        channel.start_consuming()
    except KeyboardInterrupt:
        channel.stop_consuming()
    except pika.exceptions.AMQPConnectionError:
        logging.error("Connection to broker lost")
    finally:
        if connection.is_open:
            connection.close()

if __name__ == '__main__':
    main()
