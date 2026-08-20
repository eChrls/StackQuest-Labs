FROM python:3.12-slim
WORKDIR /workspace
COPY pyproject.toml .
RUN pip install --no-cache-dir '.[test]'
COPY app app
COPY tests tests
COPY data data
