FROM python:3.12.11-slim

ENV PYTHONDONTWRITEBYTECODE=1 PYTHONUNBUFFERED=1
WORKDIR /workspace

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
RUN mkdir -p /workspace/data

COPY . .
