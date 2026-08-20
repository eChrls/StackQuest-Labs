import json, pathlib, sys, urllib.request
base=sys.argv[1]; failures=0
def check(name, ok):
    global failures; print(("PASS " if ok else "FAIL ")+name); failures += not ok
try:
    with urllib.request.urlopen(base+"/health", timeout=2) as r: check("E1 health reachable over service network", r.status==200)
except Exception as e: check("E1 health reachable over service network", False)
try:
    data=json.load(urllib.request.urlopen(base+"/config", timeout=2)); check("E2 runtime greeting configured", data.get("greeting")=="hello from lab08")
except Exception: check("E2 runtime greeting configured", False)
tf=pathlib.Path("infra/main.tf").read_text(); check("E3 security group is minimally exposed", "0.0.0.0/0" not in tf)
sys.exit(failures)
