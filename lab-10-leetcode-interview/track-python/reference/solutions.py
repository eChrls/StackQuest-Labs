"""REFERENCE / SPOILER: Python solutions used after an attempt."""
from collections import deque
from decimal import Decimal, InvalidOperation

def pair_transactions(values, target):
    seen = {}
    for index, value in enumerate(values):
        if target - value in seen: return seen[target-value], index
        seen.setdefault(value, index)
    return None

def transaction_summary(lines):
    totals = {}
    for line in lines:
        parts = line.split("|")
        if len(parts) != 4 or parts[3] != "APPROVED": continue
        try: totals[parts[1]] = totals.get(parts[1], Decimal(0)) + Decimal(parts[2])
        except InvalidOperation: pass
    return totals

def balanced_events(events):
    opening, pairs, stack = "([{<", {')':'(',']':'[','}':'{','>':'<'}, []
    for char in events:
        if char in opening: stack.append(char)
        elif char in pairs and (not stack or stack.pop() != pairs[char]): return False
    return not stack

def growth_streak(values):
    if not values: return 0
    dp=[1]*len(values)
    for i in range(len(values)):
        for j in range(i):
            if values[j] < values[i]: dp[i]=max(dp[i],dp[j]+1)
    return max(dp)

def fraud_clusters(grid):
    seen=set(); count=0
    for row in range(len(grid)):
        for col in range(len(grid[row])):
            if grid[row][col] != '1' or (row,col) in seen: continue
            count += 1; queue=deque([(row,col)]); seen.add((row,col))
            while queue:
                r,c=queue.popleft()
                for nr,nc in ((r+1,c),(r-1,c),(r,c+1),(r,c-1)):
                    if 0<=nr<len(grid) and 0<=nc<len(grid[nr]) and grid[nr][nc]=='1' and (nr,nc) not in seen: seen.add((nr,nc)); queue.append((nr,nc))
    return count

def scheduling(windows):
    end=None; count=0
    for start,finish in sorted(windows,key=lambda item:item[1]):
        if end is None or start >= end: count += 1; end=finish
    return count
