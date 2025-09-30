FROM python:3.11-slim

# Aqui utilizei o Dockerfile semelhante ao em aula com a diferença que coloquei o COPY tudo em uma linha só
WORKDIR /app

RUN apt-get update && apt-get install -y build-essential libpq-dev --no-install-recommends && rm -rf /var/lib/apt/lists/*

COPY etl_postgres.py raw_movies.csv requirements.txt ./

RUN pip install --no-cache-dir -r requirements.txt

CMD ["python", "etl_postgres.py"]
