# -*- coding: utf-8 -*-
"""个股综合估值分析（腾讯行情 + 同花顺一致预期）"""
import urllib.request
import json
import sys
import math

def main():
    if len(sys.argv) < 2:
        print(json.dumps({"error": "缺少股票代码参数"}, ensure_ascii=False))
        return

    code = sys.argv[1].strip()
    prefix = "sh" if code.startswith(("6", "9")) else ("bj" if code.startswith("8") else "sz")
    url = f"https://qt.gtimg.cn/q={prefix}{code}"
    req = urllib.request.Request(url)
    req.add_header("User-Agent", "Mozilla/5.0")

    try:
        resp = urllib.request.urlopen(req, timeout=10)
        data = resp.read().decode("gbk")
    except Exception as e:
        print(json.dumps({"error": f"获取行情失败: {e}"}, ensure_ascii=False))
        return

    parts = data.split('"')
    if len(parts) < 2:
        print(json.dumps({"error": "行情数据解析失败"}, ensure_ascii=False))
        return

    vals = parts[1].split("~")
    if len(vals) < 53:
        print(json.dumps({"error": "行情数据字段不足"}, ensure_ascii=False))
        return

    price = float(vals[3]) if vals[3] else 0
    pe_ttm = float(vals[39]) if vals[39] else 0
    pb = float(vals[46]) if vals[46] else 0
    mcap = float(vals[44]) if vals[44] else 0

    result = {
        "name": vals[1],
        "code": code,
        "price": price,
        "pe_ttm": pe_ttm,
        "pb": pb,
        "mcap_yi": mcap,
    }

    # 尝试获取一致预期 EPS
    try:
        import requests
        import pandas as pd
        from io import StringIO

        ths_url = f"https://basic.10jqka.com.cn/new/{code}/worth.html"
        ths_headers = {
            "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
            "Referer": "https://basic.10jqka.com.cn/",
        }
        r = requests.get(ths_url, headers=ths_headers, timeout=10)
        r.encoding = "gbk"
        dfs = pd.read_html(StringIO(r.text))

        for df in dfs:
            cols = [str(c) for c in df.columns]
            if any("每股收益" in c or "均值" in c for c in cols):
                if len(df) >= 2 and len(df.columns) >= 3:
                    eps_cur = float(df.iloc[0, 2]) if pd.notna(df.iloc[0, 2]) else None
                    eps_next = float(df.iloc[1, 2]) if pd.notna(df.iloc[1, 2]) else None
                    analyst_count = int(df.iloc[0, 1]) if pd.notna(df.iloc[0, 1]) else 0

                    result["eps_forecast_cur"] = eps_cur
                    result["eps_forecast_next"] = eps_next
                    result["analyst_count"] = analyst_count

                    if eps_cur and eps_cur > 0:
                        pe_fwd = price / eps_cur
                        result["pe_forward"] = round(pe_fwd, 1)

                        if eps_next and eps_next > 0:
                            cagr = eps_next / eps_cur - 1
                            result["cagr_pct"] = round(cagr * 100, 1)
                            if cagr > 0:
                                peg = pe_fwd / (cagr * 100)
                                result["peg"] = round(peg, 2)
                                if pe_fwd > 30:
                                    digest = math.log(pe_fwd / 30) / math.log(1 + cagr)
                                    result["pe_digest_years"] = round(digest, 1)
                break
    except ImportError:
        result["eps_note"] = "未安装 pandas/requests，无法获取一致预期"
    except Exception:
        result["eps_note"] = "一致预期获取失败（无机构覆盖或网络异常）"

    print(json.dumps(result, ensure_ascii=False))


if __name__ == "__main__":
    main()
