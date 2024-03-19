FROM python:3.8-slim

WORKDIR /usr/src/app

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Europe/Warsaw

RUN apt-get update && \
    apt-get install -y --no-install-recommends texlive-latex-base texlive-fonts-recommended texlive-fonts-extra texlive-latex-extra && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY . .

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

EXPOSE 5672

CMD ["celery", "-A", "task_processor", "worker", "--loglevel=info"]