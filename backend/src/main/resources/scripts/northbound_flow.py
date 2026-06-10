# -*- coding: utf-8 -*-
"""北向资金实时分钟流向（同花顺 hsgtApi）"""
import requests
import json
import sys


def main():
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/117.0.0.0",
        "Host": "data.hexin.cn",
        "Referer": "https://data.hexin.cn/",
    }
    try:
        r = requests.get(
            "https://data.hexin.cn/market/hsgtApi/method/dayChart/",
            headers=headers, timeout=10
        )
        d = r.json()
    except Exception as e:
        print(json.dumps({"error": f"请求失败: {e}"}, ensure_ascii=False))
        return

    times = d.get("time", [])
    hgt = d.get("hgt", [])
    sgt = d.get("sgt", [])

    if not times:
        print(json.dumps({"error": "暂无北向资金数据（非交易时间）"}, ensure_ascii=False))
        return

    last_hgt = float(hgt[-1]) if hgt else 0
    last_sgt = float(sgt[-1]) if sgt else 0

    result = {
        "data_points": len(times),
        "latest_time": times[-1] if times else "",
        "hgt_yi": last_hgt,
        "sgt_yi": last_sgt,
        "total_yi": round(last_hgt + last_sgt, 2),
        "unit": "亿元",
        "direction": "净流入" if (last_hgt + last_sgt) > 0 else "净流出",
    }
    print(json.dumps(result, ensure_ascii=False))


if __name__ == "__main__":
    main()
