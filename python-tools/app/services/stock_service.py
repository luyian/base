# -*- coding: utf-8 -*-
"""
股票数据服务 — 将原有 Python 脚本逻辑封装为可调用的服务方法
"""
import json
import logging
import math
import re
import urllib.request
from datetime import date, datetime, timedelta

import requests

from app.services.em_helper import UA, em_get, eastmoney_datacenter

logger = logging.getLogger(__name__)


class StockService:
    """A 股数据查询服务"""

    def get_stock_quote(self, codes: str) -> dict | list:
        """查询股票实时行情（腾讯财经）"""
        code_list = codes.split(",")
        prefixed = []
        for c in code_list:
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
            return {"error": "无有效股票代码"}

        url = "https://qt.gtimg.cn/q=" + ",".join(prefixed)
        req = urllib.request.Request(url)
        req.add_header("User-Agent", "Mozilla/5.0")
        try:
            resp = urllib.request.urlopen(req, timeout=10)
            data = resp.read().decode("gbk")
        except Exception as e:
            return {"error": f"请求失败: {e}"}

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

        return result

    def get_stock_fund_flow(self, code: str) -> dict:
        """查询个股资金流向（东财 push2）"""
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
            return {"error": f"请求失败: {e}"}

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
            return {"error": "暂无资金流数据（非交易时间或代码无效）"}

        return {
            "total_main_net_wan": round(sum(r["main_net"] for r in rows) / 1e4, 2),
            "total_super_net_wan": round(sum(r["super_net"] for r in rows) / 1e4, 2),
            "total_large_net_wan": round(sum(r["large_net"] for r in rows) / 1e4, 2),
            "unit": "万元",
            "minutes": len(rows),
            "latest": rows[-1],
        }

    def get_stock_news(self, code: str) -> dict | list:
        """查询个股新闻（东财 search-api-web）"""
        cb = "jQuery_news"
        url = "https://search-api-web.eastmoney.com/search/jsonp"
        inner_params = json.dumps({
            "uid": "", "keyword": code,
            "type": ["cmsArticleWebOld"],
            "client": "web", "clientType": "web", "clientVersion": "curr",
            "param": {"cmsArticleWebOld": {
                "searchScope": "default", "sort": "default",
                "pageIndex": 1, "pageSize": 10, "preTag": "", "postTag": "",
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
            return {"error": f"请求失败: {e}"}

        rows = []
        articles = d.get("result", {}).get("cmsArticleWebOld", []) or []
        for a in articles[:10]:
            rows.append({
                "title": re.sub(r"<[^>]+>", "", a.get("title", "")),
                "time": a.get("date", ""),
                "source": a.get("mediaName", ""),
            })

        if not rows:
            return {"message": "暂无相关新闻"}
        return rows

    def get_industry_rank(self) -> dict:
        """查询行业板块涨跌排名（东财 push2）"""
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
            return {"error": f"请求失败: {e}"}

        items = d.get("data", {}).get("diff", [])
        if not items:
            return {"error": "暂无行业数据"}

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

        return {"total": len(rows), "top10": rows[:10], "bottom5": rows[-5:]}

    def get_northbound_flow(self) -> dict:
        """查询北向资金实时分钟流向（同花顺）"""
        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/117.0.0.0",
            "Host": "data.hexin.cn",
            "Referer": "https://data.hexin.cn/",
        }
        try:
            r = requests.get(
                "https://data.hexin.cn/market/hsgtApi/method/dayChart/",
                headers=headers, timeout=10,
            )
            d = r.json()
        except Exception as e:
            return {"error": f"请求失败: {e}"}

        times = d.get("time", [])
        hgt = d.get("hgt", [])
        sgt = d.get("sgt", [])

        if not times:
            return {"error": "暂无北向资金数据（非交易时间）"}

        last_hgt = float(hgt[-1]) if hgt else 0
        last_sgt = float(sgt[-1]) if sgt else 0

        return {
            "data_points": len(times),
            "latest_time": times[-1] if times else "",
            "hgt_yi": last_hgt,
            "sgt_yi": last_sgt,
            "total_yi": round(last_hgt + last_sgt, 2),
            "unit": "亿元",
            "direction": "净流入" if (last_hgt + last_sgt) > 0 else "净流出",
        }

    def get_dragon_tiger(self, code: str) -> dict:
        """查询龙虎榜数据（东财 datacenter）"""
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

        return {
            "code": code,
            "period": f"{start} ~ {trade_date}",
            "records_count": len(records),
            "details": records[:10],
        }

    def get_hot_stocks(self) -> dict:
        """查询当日强势股 + 题材归因（同花顺）"""
        today = date.today().strftime("%Y-%m-%d")
        url = f"http://zx.10jqka.com.cn/event/api/getharden/date/{today}/orderby/date/orderway/desc/charset/GBK/"
        headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/117.0.0.0"}

        try:
            r = requests.get(url, headers=headers, timeout=10)
            d = r.json()
        except Exception as e:
            return {"error": f"请求失败: {e}"}

        if d.get("errocode", 0) != 0:
            return {"error": d.get("errormsg", "未知错误")}

        rows = d.get("data", [])
        result = []
        for item in rows[:30]:
            result.append({
                "code": item.get("code", ""),
                "name": item.get("name", ""),
                "reason": item.get("reason", ""),
                "change_pct": item.get("zhangfu", ""),
                "turnover": item.get("huanshou", ""),
            })

        return {"date": today, "total": len(rows), "top30": result}

    def get_concept_blocks(self, code: str) -> dict:
        """查询个股板块归属（东财 slist）"""
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
            return {"error": f"请求失败: {e}"}

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

        return {
            "stock_code": code,
            "total": len(boards),
            "boards": boards,
            "concept_tags": [b["name"] for b in boards],
        }

    def get_margin_trading(self, code: str) -> dict:
        """查询融资融券数据（东财 datacenter）"""
        data = eastmoney_datacenter(
            "RPTA_WEB_RZRQ_GGMX",
            filter_str=f'(SCODE="{code}")',
            page_size=10,
            sort_columns="DATE", sort_types="-1",
        )

        if not data:
            return {"message": "暂无融资融券数据（可能非两融标的）"}

        rows = []
        for row in data:
            rows.append({
                "date": str(row.get("DATE", ""))[:10],
                "rzye_yi": round((row.get("RZYE") or 0) / 1e8, 2),
                "rzmre_wan": round((row.get("RZMRE") or 0) / 1e4, 1),
                "rqye_yi": round((row.get("RQYE") or 0) / 1e8, 2),
                "rzrqye_yi": round((row.get("RZRQYE") or 0) / 1e8, 2),
            })

        return {"code": code, "records": rows, "unit": "亿元/万元"}

    def get_stock_valuation(self, code: str) -> dict:
        """个股综合估值分析（腾讯行情 + 同花顺一致预期）"""
        prefix = "sh" if code.startswith(("6", "9")) else ("bj" if code.startswith("8") else "sz")
        url = f"https://qt.gtimg.cn/q={prefix}{code}"
        req = urllib.request.Request(url)
        req.add_header("User-Agent", "Mozilla/5.0")

        try:
            resp = urllib.request.urlopen(req, timeout=10)
            data = resp.read().decode("gbk")
        except Exception as e:
            return {"error": f"获取行情失败: {e}"}

        parts = data.split('"')
        if len(parts) < 2:
            return {"error": "行情数据解析失败"}

        vals = parts[1].split("~")
        if len(vals) < 53:
            return {"error": "行情数据字段不足"}

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
            from io import StringIO

            import pandas as pd

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
            result["eps_note"] = "未安装 pandas，无法获取一致预期"
        except Exception:
            result["eps_note"] = "一致预期获取失败（无机构覆盖或网络异常）"

        return result


stock_service = StockService()
