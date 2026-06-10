# -*- coding: utf-8 -*-
"""实时行情查询（腾讯财经，不封IP）"""
import urllib.request
import json
import sys

def main():
    if len(sys.argv) < 2:
        print(json.dumps({"error": "缺少股票代码参数"}, ensure_ascii=False))
        return

    codes = sys.argv[1].split(",")
    prefixed = []
    for c in codes:
        # 归一化：去除 .SH/.SZ/.BJ 后缀，只保留6位数字
        c = c.strip().upper()
        if "." in c:
            c = c.split(".")[0]
        c = c.replace("SH", "").replace("SZ", "").replace("BJ", "")
        if not c or len(c) != 6 or not c.isdigit():
            continue
        if c.startswith(("6", "9")):
            prefixed.append(f"sh{c}")
        elif c.startswith("8"):
            prefixed.append(f"bj{c}")
        else:
            prefixed.append(f"sz{c}")

    if not prefixed:
        print(json.dumps({"error": "无有效股票代码"}, ensure_ascii=False))
        return

    url = "https://qt.gtimg.cn/q=" + ",".join(prefixed)
    req = urllib.request.Request(url)
    req.add_header("User-Agent", "Mozilla/5.0")
    try:
        resp = urllib.request.urlopen(req, timeout=10)
        data = resp.read().decode("gbk")
    except Exception as e:
        print(json.dumps({"error": f"请求失败: {e}"}, ensure_ascii=False))
        return

    result = []
    for line in data.strip().split(";"):
        if not line.strip() or "=" not in line or '"' not in line:
            continue
        vals = line.split('"')[1].split("~")
        if len(vals) < 53:
            continue
        result.append({
            "name": vals[1],
            "code": vals[2],
            "price": float(vals[3]) if vals[3] else 0,
            "change_pct": float(vals[32]) if vals[32] else 0,
            "change_amt": float(vals[31]) if vals[31] else 0,
            "open": float(vals[5]) if vals[5] else 0,
            "high": float(vals[33]) if vals[33] else 0,
            "low": float(vals[34]) if vals[34] else 0,
            "last_close": float(vals[4]) if vals[4] else 0,
            "amount_wan": float(vals[37]) if vals[37] else 0,
            "turnover_pct": float(vals[38]) if vals[38] else 0,
            "pe_ttm": float(vals[39]) if vals[39] else 0,
            "mcap_yi": float(vals[44]) if vals[44] else 0,
            "float_mcap_yi": float(vals[45]) if vals[45] else 0,
            "pb": float(vals[46]) if vals[46] else 0,
            "limit_up": float(vals[47]) if vals[47] else 0,
            "limit_down": float(vals[48]) if vals[48] else 0,
        })

    print(json.dumps(result, ensure_ascii=False))


if __name__ == "__main__":
    main()
