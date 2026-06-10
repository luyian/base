# -*- coding: utf-8 -*-
"""个股新闻查询（东财 search-api-web）"""
import sys
import os
import re
sys.path.insert(0, os.path.dirname(__file__))
from em_helper import *


def main():
    if len(sys.argv) < 2:
        print(json.dumps({"error": "缺少股票代码参数"}, ensure_ascii=False))
        return

    code = sys.argv[1].strip()
    cb = "jQuery_news"
    url = "https://search-api-web.eastmoney.com/search/jsonp"
    inner_params = json.dumps({
        "uid": "", "keyword": code,
        "type": ["cmsArticleWebOld"],
        "client": "web", "clientType": "web", "clientVersion": "curr",
        "param": {"cmsArticleWebOld": {
            "searchScope": "default", "sort": "default",
            "pageIndex": 1, "pageSize": 10, "preTag": "", "postTag": ""
        }},
    }, separators=(",", ":"))
    params = {"cb": cb, "param": inner_params}
    headers = {"User-Agent": UA, "Referer": "https://so.eastmoney.com/"}

    try:
        r = em_get(url, params=params, headers=headers, timeout=15)
        text = r.text
        json_str = text[text.index("(") + 1:text.rindex(")")]
        d = json.loads(json_str)
    except Exception as e:
        print(json.dumps({"error": f"请求失败: {e}"}, ensure_ascii=False))
        return

    rows = []
    articles = d.get("result", {}).get("cmsArticleWebOld", []) or []
    for a in articles[:10]:
        rows.append({
            "title": re.sub(r"<[^>]+>", "", a.get("title", "")),
            "time": a.get("date", ""),
            "source": a.get("mediaName", ""),
        })

    if not rows:
        print(json.dumps({"message": "暂无相关新闻"}, ensure_ascii=False))
        return

    print(json.dumps(rows, ensure_ascii=False))


if __name__ == "__main__":
    main()
