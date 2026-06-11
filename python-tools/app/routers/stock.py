# -*- coding: utf-8 -*-
"""
股票数据工具路由
"""
from fastapi import APIRouter, Query

from app.schema import Result
from app.services.stock_service import stock_service

router = APIRouter()


@router.get("/quote", summary="股票实时行情")
async def stock_quote(
    codes: str = Query(..., description="股票代码，多只用逗号分隔，如 600519,000858"),
) -> Result:
    """查询股票实时行情（腾讯财经）"""
    data = stock_service.get_stock_quote(codes)
    if isinstance(data, dict) and "error" in data:
        return Result.fail(message=data["error"])
    return Result.ok(data=data)


@router.get("/fund-flow", summary="个股资金流向")
async def stock_fund_flow(
    code: str = Query(..., description="6位股票代码，如 600519"),
) -> Result:
    """查询个股当日资金流向（分钟级）"""
    data = stock_service.get_stock_fund_flow(code)
    if "error" in data:
        return Result.fail(message=data["error"])
    return Result.ok(data=data)


@router.get("/news", summary="个股新闻")
async def stock_news(
    code: str = Query(..., description="6位股票代码，如 600519"),
) -> Result:
    """查询个股相关新闻资讯"""
    data = stock_service.get_stock_news(code)
    if isinstance(data, dict) and "error" in data:
        return Result.fail(message=data["error"])
    return Result.ok(data=data)


@router.get("/industry-rank", summary="行业板块排名")
async def industry_rank() -> Result:
    """查询全市场行业板块涨跌幅排名"""
    data = stock_service.get_industry_rank()
    if "error" in data:
        return Result.fail(message=data["error"])
    return Result.ok(data=data)


@router.get("/northbound-flow", summary="北向资金流向")
async def northbound_flow() -> Result:
    """查询北向资金（沪股通/深股通）当日实时分钟流向"""
    data = stock_service.get_northbound_flow()
    if "error" in data:
        return Result.fail(message=data["error"])
    return Result.ok(data=data)


@router.get("/dragon-tiger", summary="龙虎榜数据")
async def dragon_tiger(
    code: str = Query(..., description="6位股票代码，如 002475"),
) -> Result:
    """查询龙虎榜数据"""
    data = stock_service.get_dragon_tiger(code)
    return Result.ok(data=data)


@router.get("/hot-stocks", summary="当日强势股")
async def hot_stocks() -> Result:
    """查询当日涨停/强势股及题材归因"""
    data = stock_service.get_hot_stocks()
    if "error" in data:
        return Result.fail(message=data["error"])
    return Result.ok(data=data)


@router.get("/concept-blocks", summary="板块归属")
async def concept_blocks(
    code: str = Query(..., description="6位股票代码，如 600519"),
) -> Result:
    """查询个股所属概念板块"""
    data = stock_service.get_concept_blocks(code)
    if "error" in data:
        return Result.fail(message=data["error"])
    return Result.ok(data=data)


@router.get("/margin-trading", summary="融资融券")
async def margin_trading(
    code: str = Query(..., description="6位股票代码，如 600519"),
) -> Result:
    """查询个股融资融券数据"""
    data = stock_service.get_margin_trading(code)
    return Result.ok(data=data)


@router.get("/valuation", summary="综合估值")
async def stock_valuation(
    code: str = Query(..., description="6位股票代码，如 688017"),
) -> Result:
    """个股综合估值分析"""
    data = stock_service.get_stock_valuation(code)
    if "error" in data:
        return Result.fail(message=data["error"])
    return Result.ok(data=data)
