# -*- coding: utf-8 -*-
"""
东财公共模块 — 限流 + 会话复用 + 断连重试
"""
import json
import random
import time

import requests
from requests.exceptions import ConnectionError

UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
EM_SESSION = requests.Session()
EM_SESSION.headers.update({"User-Agent": UA})
EM_MIN_INTERVAL = 1.0
_em_last_call = [0.0]


def em_get(url: str, params: dict | None = None, headers: dict | None = None,
           timeout: int = 15, **kwargs) -> requests.Response:
    """东财统一请求入口：自动节流 + 复用 session + 断连自动重建连接重试"""
    global EM_SESSION
    wait = EM_MIN_INTERVAL - (time.time() - _em_last_call[0])
    if wait > 0:
        time.sleep(wait + random.uniform(0.1, 0.5))
    try:
        resp = EM_SESSION.get(url, params=params, headers=headers, timeout=timeout, **kwargs)
        return resp
    except ConnectionError:
        # 服务器主动关闭了旧连接，关闭 session 后新建重试
        EM_SESSION.close()
        EM_SESSION = requests.Session()
        EM_SESSION.headers.update({"User-Agent": UA})
        time.sleep(1)
        return EM_SESSION.get(url, params=params, headers=headers, timeout=timeout, **kwargs)
    finally:
        _em_last_call[0] = time.time()


DATACENTER_URL = "https://datacenter-web.eastmoney.com/api/data/v1/get"


def eastmoney_datacenter(report_name: str, filter_str: str = "",
                         page_size: int = 50, sort_columns: str = "",
                         sort_types: str = "-1") -> list[dict]:
    """东财数据中心通用查询"""
    params = {
        "reportName": report_name,
        "columns": "ALL",
        "filter": filter_str,
        "pageNumber": "1",
        "pageSize": str(page_size),
        "sortColumns": sort_columns,
        "sortTypes": sort_types,
        "source": "WEB",
        "client": "WEB",
    }
    headers = {"User-Agent": UA, "Referer": "https://data.eastmoney.com/"}
    try:
        r = em_get(DATACENTER_URL, params=params, headers=headers, timeout=15)
        d = r.json()
        result = d.get("result", {})
        if result is None:
            return []
        return result.get("data", []) or []
    except Exception:
        return []
