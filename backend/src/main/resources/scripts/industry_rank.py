# -*- coding: utf-8 -*-
"""行业板块涨跌排名（东财 push2）"""
import sys
import os
sys.path.insert(0, os.path.dirname(__file__))
from em_helper import *


def main():
    url = "https://push2.eastmoney.com/api/qt/clist/get"
    params = {
        "pn": "1", "pz": "100", "po": "1", "np": "1",
        "fltt": "2", "invt": "2",
        "fs": "m:90+t:2",
        "fields": "f2,f3,f4,f12,f13,f14,f104,f105,f128,f136,f140,f141,f207",
    }
    headers = {"User-Agent": UA}
    try:
        r = em_get(url, params=params, headers=headers, timeout=15)
        d = r.json()
    except Exception as e:
        print(json.dumps({"error": f"请求失败: {e}"}, ensure_ascii=False))
        return

    items = d.get("data", {}).get("diff", [])
    if not items:
        print(json.dumps({"error": "暂无行业数据"}, ensure_ascii=False))
        return

    rows = []
    for i, item in enumerate(items):
        rows.append({
            "rank": i + 1,
            "name": item.get("f14", ""),
            "change_pct": item.get("f3", 0),
            "up_count": item.get("f104", 0),
            "down_count": item.get("f105", 0),
            "leader": item.get("f140", ""),
            "leader_change": item.get("f136", 0),
        })

    result = {"total": len(rows), "top10": rows[:10], "bottom5": rows[-5:]}
    print(json.dumps(result, ensure_ascii=False))


if __name__ == "__main__":
    main()
