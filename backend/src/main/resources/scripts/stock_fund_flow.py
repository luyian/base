# -*- coding: utf-8 -*-
"""个股资金流向 — 分钟级（东财 push2）"""
import sys
import os
sys.path.insert(0, os.path.dirname(__file__))
from em_helper import *


def main():
    if len(sys.argv) < 2:
        print(json.dumps({"error": "缺少股票代码参数"}, ensure_ascii=False))
        return

    code = sys.argv[1].strip()
    secid = f"1.{code}" if code.startswith("6") else f"0.{code}"
    url = "https://push2.eastmoney.com/api/qt/stock/fflow/kline/get"
    params = {
        "secid": secid, "klt": 1,
        "fields1": "f1,f2,f3,f7",
        "fields2": "f51,f52,f53,f54,f55,f56,f57",
    }
    headers = {"User-Agent": UA, "Referer": "https://quote.eastmoney.com/"}
    try:
        r = em_get(url, params=params, headers=headers, timeout=10)
        d = r.json()
    except Exception as e:
        print(json.dumps({"error": f"请求失败: {e}"}, ensure_ascii=False))
        return

    rows = []
    for line in d.get("data", {}).get("klines", []):
        parts = line.split(",")
        if len(parts) >= 6:
            rows.append({
                "time": parts[0],
                "main_net": float(parts[1]),
                "small_net": float(parts[2]),
                "mid_net": float(parts[3]),
                "large_net": float(parts[4]),
                "super_net": float(parts[5]),
            })

    if not rows:
        print(json.dumps({"error": "暂无资金流数据（非交易时间或代码无效）"}, ensure_ascii=False))
        return

    summary = {
        "total_main_net_wan": round(sum(r["main_net"] for r in rows) / 1e4, 2),
        "total_super_net_wan": round(sum(r["super_net"] for r in rows) / 1e4, 2),
        "total_large_net_wan": round(sum(r["large_net"] for r in rows) / 1e4, 2),
        "unit": "万元",
        "minutes": len(rows),
        "latest": rows[-1],
    }
    print(json.dumps(summary, ensure_ascii=False))


if __name__ == "__main__":
    main()
