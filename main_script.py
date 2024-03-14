import json
import subprocess
from jinja2 import Environment, FileSystemLoader

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
template = env.get_template('your_template.tex')

# Load data from JSON
with open('data.json', 'r') as file:
    data = json.load(file)

# Render the template with data
filled_content = template.render(data)

# Write the filled-in LaTeX code to a new file
with open('filled_template.tex', 'w') as file:
    file.write(filled_content)

# Compile the LaTeX file into a PDF
subprocess.run(['pdflatex', '-interaction=nonstopmode', 'filled_template.tex'], check=True)
