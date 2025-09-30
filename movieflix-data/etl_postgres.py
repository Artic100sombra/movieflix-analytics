import pandas as pd
from sqlalchemy import create_engine, text
import os
import unicodedata
import random

# Informações do banco de dados e o nome do arquivo que vamos tratar os dados
PG_USER = os.getenv("PG_USER", "movieuser")
PG_PASS = os.getenv("PG_PASS", "moviepass")
PG_DB   = os.getenv("PG_DB", "moviesdb")
PG_HOST = os.getenv("PG_HOST", "db")
PG_PORT = os.getenv("PG_PORT", "5432")

CSV_FILE = "raw_movies.csv"

conn_str = f"postgresql+psycopg2://{PG_USER}:{PG_PASS}@{PG_HOST}:{PG_PORT}/{PG_DB}"

print("Conexão:", conn_str)

engine = create_engine(conn_str, echo=False)

print("Lendo CSV:", CSV_FILE)
df = pd.read_csv(CSV_FILE)

# Deixei o nome das colunas semelhante ao 
# feito em aula então temos que remover espaços e acentos 
# e transformar no nome que vou dar no banco

def normalize_col(col):
    col = col.strip()
    col = unicodedata.normalize('NFKD', col).encode('ASCII', 'ignore').decode('ASCII')
    col = col.replace(' ', '_').replace('-', '_')
    return col.lower()

df.columns = [normalize_col(c) for c in df.columns]

df = df.rename(columns={
    "name": "title",
    "release_year": "year",
    "genre": "genre",
    "imdb_rating": "rating"
})

# Garantir colunas esperadas (se não existir, cria com NaN)
for col in ["title", "year", "genre", "rating"]:
    if col not in df.columns:
        df[col] = None

# Remover colunas extras que não são esperadas pelo banco
df = df[["title", "year", "genre", "rating"]]


# Tratamento dos dados - remover espaçoes extras em título
df["title"] = df["title"].astype(str).str.strip()
# remover espaços extras em genero
df["genre"] = df["genre"].astype(str).str.strip()
# converter ano e nota com valores default quando faltam
df["year"] = pd.to_numeric(df["year"], errors="coerce").fillna(0).astype(int)
df["rating"] = pd.to_numeric(df["rating"], errors="coerce").fillna(0.0).astype(float)

print("Preview após transformação:")
print(df.head())


# ------------------------------
# Criando o schema
# ------------------------------
with engine.begin() as conn:
    # Tabela movie
    conn.execute(text("""
    CREATE TABLE IF NOT EXISTS movie (
        id SERIAL PRIMARY KEY,
        title TEXT,
        year INT,
        genre TEXT
    );
    """))

    # Tabela rating
    conn.execute(text("""
    CREATE TABLE IF NOT EXISTS rating (
        id SERIAL PRIMARY KEY,
        score REAL,
        age INT,
        country TEXT,
        movie_id INT REFERENCES movie(id) ON DELETE CASCADE
    );
    """))

    # Limpa a tabela pra garantir que não tem alguma informação anterior (lixo)
    conn.execute(text("TRUNCATE TABLE rating CASCADE;"))
    conn.execute(text("TRUNCATE TABLE movie CASCADE;"))

# ------------------------------
# Inserindo os filmes
# ------------------------------
movie_ids = {}
with engine.begin() as conn:
    for _, row in df.iterrows():
        result = conn.execute(
            text("INSERT INTO movie (title, year, genre) VALUES (:title, :year, :genre) RETURNING id"),
            {"title": row["title"], "year": row["year"], "genre": row["genre"]}
        )
        movie_id = result.scalar()
        movie_ids[row["title"]] = movie_id

# ------------------------------
# Inserindo as notas (adicionei um random pra países e idade já que tinha esquecido de colocar no CSV)
# ------------------------------

countries = ["USA", "Brazil", "UK", "France", "Japan", "Germany"]
ages = [15, 18, 21, 25, 30, 35, 40, 50]

with engine.begin() as conn:
    for _, row in df.iterrows():
        if row["rating"] and row["rating"] > 0:
            conn.execute(
                text("""
                    INSERT INTO rating (score, age, country, movie_id)
                    VALUES (:score, :age, :country, :movie_id)
                """),
                {
                    "score": float(row["rating"]),
                    "age": random.choice(ages),
                    "country": random.choice(countries),
                    "movie_id": movie_ids[row["title"]]
                }
            )

print("✅ Dados carregados na tabela 'movie' (Data Warehouse).")

# Aqui temos a tabela de movies do genero sci_fi para formar um data mart
with engine.begin() as conn:
    conn.execute(text("DROP TABLE IF EXISTS scifi_movie;"))
    conn.execute(text("""
        CREATE TABLE scifi_movie AS
        SELECT m.title, m.year, r.score
        FROM movie m
        JOIN rating r ON r.movie_id = m.id
        WHERE m.genre ILIKE 'Sci%%';
    """))

print("✅ Data Mart 'scifi_movie' criado.")

# Por fim uma checagem para ver se os registros foram inseridos no banco de dados
with engine.connect() as conn:
    total = conn.execute(text("SELECT COUNT(*) FROM movie;")).scalar()
    total_ratings = conn.execute(text("SELECT COUNT(*) FROM rating;")).scalar()
    scifi_total = conn.execute(text("SELECT COUNT(*) FROM scifi_movie;")).scalar()
    print(f"Registros no Warehouse (movie): {total}")
    print(f"Registros no Warehouse (rating): {total_ratings}")
    print(f"Registros no Data Mart (scifi_movie): {scifi_total}")

print("Fim do ETL.")