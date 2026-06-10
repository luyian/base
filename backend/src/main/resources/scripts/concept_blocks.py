# -*- coding: utf-8 -*-
"""个股板块归属查询（东财 slist）"""
import sys
import os
sys.path.insert(0, os.path.dirname(__file__))
from em_helper import *


def main():
    if len(sys.argv) < 2:
        print(json.dumps({"error": "缺少股票代码参数"}, ensure_ascii=False))
        return

    code = sys.argv[1].strip()
    market_code = 1 if code.startswith("6") else 0
    params = {
        "fltt": "2", "invt": "2",
        "secid": f"{market_code}.{code}",
        "spt": "3", "pi": "0", "pz": "200", "po": "1",
        "fields": "f12,f14,f3,f128",
    }
    headers = {"User-Agent": UA, "Referer": "https://quote.eastmoney.com/"}

    try:
        r = em_get("https://push2.eastmoney.com/api/qt/slist/get",
                   params=params, headers=headers, timeout=15)
        d = r.json()
    except Exception as e:
        print(json.dumps({"error": f"请求失败: {e}"}, ensure_ascii=False))
        return

    diff = (d.get("data") or {}).get("diff") or {}
    items = diff.values() if isinstance(diff, dict) else diff
    boards = []
    for it in items:
        boards.append({
            "name": it.get("f14", ""),
            "code": it.get("f12", ""),
            "change_pct": it.get("f3", ""),
            "lead_stock": it.get("f128", ""),
        })

    result = {
        "stock_code": code,
        "total": len(boards),
        "boards": boards,
        "concept_tags": [b["name"] for b in boards],
    }
    print(json.dumps(result, ensure_ascii=False))


if __name__ == "__main__":
    main()
