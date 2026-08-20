import json, os
from http.server import BaseHTTPRequestHandler, HTTPServer

class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/health":
            self.send_response(200); self.send_header("Content-Type","application/json"); self.end_headers(); self.wfile.write(json.dumps({"status":"ok"}).encode()); return
        if self.path == "/config":
            self.send_response(200); self.send_header("Content-Type","application/json"); self.end_headers(); self.wfile.write(json.dumps({"greeting":os.getenv("APP_GREETING","")}).encode()); return
        self.send_response(404); self.end_headers()
    def log_message(self, fmt, *args): print("%s - %s" % (self.address_string(), fmt % args), flush=True)

port=int(os.getenv("APP_PORT","8080"))
# Deliberate E1: loopback binding makes the container unreachable through its service network.
HTTPServer((os.getenv("APP_BIND","127.0.0.1"),port),Handler).serve_forever()
