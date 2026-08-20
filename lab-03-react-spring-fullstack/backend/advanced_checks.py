"""Offline Advanced evidence checks; no AWS/external service required."""
import sqlite3, threading

REPORTING_QUERY = "SELECT amount FROM payment WHERE merchant_id = ?"
IDEMPOTENCY_GUARD = False

def a1_reporting():
    db=sqlite3.connect(":memory:"); db.execute("create table payment(merchant_id text, amount numeric, status text)"); db.executemany("insert into payment values('M1',?, 'CAPTURED')",[(1,)]*1000)
    plan=list(db.execute("EXPLAIN QUERY PLAN "+REPORTING_QUERY,("M1",)))
    return "GROUP BY" in REPORTING_QUERY and "SUM" in REPORTING_QUERY and plan

def a2_atomic_idempotent():
    state={"status":"PENDING","audits":0}; lock=threading.Lock()
    def callback():
        if IDEMPOTENCY_GUARD:
            with lock:
                if state["status"] != "PENDING": return
                state["status"]="CAPTURED"; state["audits"]+=1
        else:
            state["status"]="CAPTURED"; state["audits"]+=1
    ts=[threading.Thread(target=callback) for _ in range(2)]
    [t.start() for t in ts]; [t.join() for t in ts]
    return state["status"]=="CAPTURED" and state["audits"]==1

if __name__ == "__main__":
    results=[("A1 aggregate reporting plan",a1_reporting()),("A2 atomic idempotent callback",a2_atomic_idempotent())]
    for name,ok in results: print(("PASS " if ok else "FAIL ")+name)
    raise SystemExit(sum(not ok for _,ok in results))
