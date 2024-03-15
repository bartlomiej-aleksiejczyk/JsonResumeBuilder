# Use an official Python runtime as a parent image
FROM python:3.8-slim

# Set the working directory in the container
WORKDIR /usr/src/app

# Set environment variables
ENV CELERY_BROKER_URL=amqp://guest@localhost//
ENV SCHEDULER_API_URL=http://localhost:5000/status
# Set default environment variables for LaTeX installation, adjust as necessary
ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Europe/Warsaw

# Install any needed packages for LaTeX and your application
RUN apt-get update && \
    apt-get install -y --no-install-recommends texlive-latex-base texlive-fonts-recommended texlive-fonts-extra texlive-latex-extra && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy the current directory contents into the container at /usr/src/app
COPY . .

# Install any needed packages specified in requirements.txt
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

# Make port 5672 available to the world outside this container
EXPOSE 5672

# Run app.py when the container launches
CMD ["celery", "-A", "your_script_name", "worker", "--loglevel=info"]
