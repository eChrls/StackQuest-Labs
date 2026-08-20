import json, pathlib, re, sys, urllib.request

base=sys.argv[1]; failures=0
def check(name, ok):
    global failures; print(("PASS " if ok else "FAIL ")+name); failures += not ok
try:
    with urllib.request.urlopen(base+"/health", timeout=2) as r: check("E1 health reachable over service network", r.status==200)
except Exception as e: check("E1 health reachable over service network", False)
try:
    data=json.load(urllib.request.urlopen(base+"/config", timeout=2)); check("E2 runtime greeting configured", data.get("greeting")=="hello from lab08")
except Exception: check("E2 runtime greeting configured", False)
tf=pathlib.Path("infra/main.tf").read_text(); check("E3 security group is minimally exposed", "0.0.0.0/0" not in tf.split("egress")[0])
workflow=pathlib.Path(".github/workflows/ci.yml").read_text()
check("I1 CI image uses immutable commit tag", "push: false" in workflow and "${{ github.sha }}" in workflow and ":latest" not in workflow)

terraform=pathlib.Path("infra/ecr_rds.tf").read_text()
i2_network = all(token in terraform for token in (
    'resource "aws_vpc"', 'resource "aws_subnet"',
    'resource "aws_db_subnet_group"', 'resource "aws_security_group" "database"',
    "db_subnet_group_name", "vpc_security_group_ids",
))
check("I2 RDS is private with explicit networking", i2_network and bool(re.search(r"publicly_accessible\s*=\s*false", terraform)))

obs=json.loads(pathlib.Path("observability.json").read_text())
metric=obs.get("metric_filter", {}); alarm=obs.get("alarm", {})
i3_signal = metric.get("name") == alarm.get("metric") and "$.status >= 500" in metric.get("pattern", "") and metric.get("value") == 1
check("I3 operational health, logs and alarm signal are observable", obs.get("health_path")=="/health" and obs.get("log_retention_days",0)>=14 and i3_signal)
sys.exit(failures)
