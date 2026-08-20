import json, os, time
from http.server import BaseHTTPRequestHandler, HTTPServer

class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/health":
            self.send_response(200); self.send_header("Content-Type","application/json"); self.end_headers(); self.wfile.write(json.dumps({"status":"ok"}).encode()); return
        if self.path == "/config":
            self.send_response(200); self.send_header("Content-Type","application/json"); self.end_headers(); self.wfile.write(json.dumps({"greeting":os.getenv("APP_GREETING","")}).encode()); return
        self.send_response(404); self.end_headers()
    def log_request(self, code="-", size="-"):
        print(json.dumps({
            "event": "http_request",
            "method": self.command,
            "path": self.path,
            "status": int(code),
            "client": self.client_address[0],
            "timestamp": int(time.time()),
        }), flush=True)

    def log_message(self, fmt, *args):
        pass

port=int(os.getenv("APP_PORT","8080"))
# Deliberate E1: loopback binding makes the container unreachable through its service network.
HTTPServer((os.getenv("APP_BIND","127.0.0.1"),port),Handler).serve_forever()
