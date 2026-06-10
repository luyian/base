# -*- coding: utf-8 -*-
"""龙虎榜数据查询（东财 datacenter）"""
import sys
import os
from datetime import datetime, timedelta
sys.path.insert(0, os.path.dirname(__file__))
from em_helper import *


def main():
    if len(sys.argv) < 2:
        print(json.dumps({"error": "缺少股票代码参数"}, ensure_ascii=False))
        return

    code = sys.argv[1].strip()
    trade_date = datetime.now().strftime("%Y-%m-%d")
    start = (datetime.now() - timedelta(days=30)).strftime("%Y-%m-%d")

    data = eastmoney_datacenter(
        "RPT_DAILYBILLBOARD_DETAILSNEW",
        filter_str=f"(TRADE_DATE>='{start}')(TRADE_DATE<='{trade_date}')(SECURITY_CODE=\"{code}\")",
        page_size=50,
        sort_columns="TRADE_DATE", sort_types="-1",
    )

    records = []
    for row in data:
        records.append({
            "date": str(row.get("TRADE_DATE", ""))[:10],
            "reason": row.get("EXPLANATION", ""),
            "net_buy_wan": round((row.get("BILLBOARD_NET_AMT") or 0) / 10000, 1),
            "change_pct": round(float(row.get("CHANGE_RATE") or 0), 2),
        })

    result = {
        "code": code,
        "period": f"{start} ~ {trade_date}",
        "records_count": len(records),
        "details": records[:10],
    }
    print(json.dumps(result, ensure_ascii=False))


if __name__ == "__main__":
    main()
