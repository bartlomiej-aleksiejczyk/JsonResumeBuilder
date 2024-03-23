FROM python:3.12.2-slim-bookworm

WORKDIR /usr/src/app

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Europe/Warsaw

RUN apt-get update && \
    apt-get install -y --no-install-recommends && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

RUN useradd -m myuser
USER myuser

ENTRYPOINT ["python", "task_processor.py"]