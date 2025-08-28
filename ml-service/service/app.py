from fastapi import FastAPI

app = FastAPI(title="Credit Risk API (skeleton)")

@app.get('/health')
def health():
    return {"status": "ok"}
