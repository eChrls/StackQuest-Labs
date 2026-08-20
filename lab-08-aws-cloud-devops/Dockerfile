FROM python:3.12-slim
WORKDIR /workspace
COPY app app
COPY tests tests
COPY infra infra
COPY . .
CMD ["python","app/main.py"]
