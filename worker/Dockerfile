FROM python:3.12.2-alpine3.19

WORKDIR /usr/src/app

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Europe/Warsaw

RUN apt-get update && \
    apt-get install -y --no-install-recommends texlive-latex-base texlive-fonts-recommended texlive-fonts-extra texlive-latex-extra && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

RUN useradd -m myuser
USER myuser

ENTRYPOINT ["celery", "-A", "task_processor", "worker"]
CMD ["--loglevel=info"]
