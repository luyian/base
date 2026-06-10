# -*- coding: utf-8 -*-
"""融资融券数据查询（东财 datacenter）"""
import sys
import os
sys.path.insert(0, os.path.dirname(__file__))
from em_helper import *


def main():
    if len(sys.argv) < 2:
        print(json.dumps({"error": "缺少股票代码参数"}, ensure_ascii=False))
        return

    code = sys.argv[1].strip()
    data = eastmoney_datacenter(
        "RPTA_WEB_RZRQ_GGMX",
        filter_str=f'(SCODE="{code}")',
        page_size=10,
        sort_columns="DATE", sort_types="-1",
    )

    if not data:
        print(json.dumps({"message": "暂无融资融券数据（可能非两融标的）"}, ensure_ascii=False))
        return

    rows = []
    for row in data:
        rows.append({
            "date": str(row.get("DATE", ""))[:10],
            "rzye_yi": round((row.get("RZYE") or 0) / 1e8, 2),
            "rzmre_wan": round((row.get("RZMRE") or 0) / 1e4, 1),
            "rqye_yi": round((row.get("RQYE") or 0) / 1e8, 2),
            "rzrqye_yi": round((row.get("RZRQYE") or 0) / 1e8, 2),
        })

    print(json.dumps({"code": code, "records": rows, "unit": "亿元/万元"}, ensure_ascii=False))


if __name__ == "__main__":
    main()
