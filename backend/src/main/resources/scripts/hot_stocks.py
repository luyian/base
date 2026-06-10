# -*- coding: utf-8 -*-
"""当日强势股 + 题材归因（同花顺）"""
import requests
import json
import sys
from datetime import date


def main():
    today = date.today().strftime("%Y-%m-%d")
    url = f"http://zx.10jqka.com.cn/event/api/getharden/date/{today}/orderby/date/orderway/desc/charset/GBK/"
    headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/117.0.0.0"}

    try:
        r = requests.get(url, headers=headers, timeout=10)
        d = r.json()
    except Exception as e:
        print(json.dumps({"error": f"请求失败: {e}"}, ensure_ascii=False))
        return

    if d.get("errocode", 0) != 0:
        print(json.dumps({"error": d.get("errormsg", "未知错误")}, ensure_ascii=False))
        return

    rows = d.get("data", [])
    result = []
    for r in rows[:30]:
        result.append({
            "code": r.get("code", ""),
            "name": r.get("name", ""),
            "reason": r.get("reason", ""),
            "change_pct": r.get("zhangfu", ""),
            "turnover": r.get("huanshou", ""),
        })

    print(json.dumps({"date": today, "total": len(rows), "top30": result}, ensure_ascii=False))


if __name__ == "__main__":
    main()
