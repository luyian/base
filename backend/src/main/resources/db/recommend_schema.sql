-- 股票推荐打分系统 - 数据库表结构
-- 创建时间：2026-02-06

-- ============================================
-- 1. 打分规则配置表
-- ============================================
DROP TABLE IF EXISTS `stk_score_rule`;
CREATE TABLE `stk_score_rule` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `rule_code` varchar(50) NOT NULL COMMENT '规则编码（唯一标识，对应策略Bean名称）',
    `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
    `rule_desc` varchar(500) DEFAULT NULL COMMENT '规则描述',
    `category` varchar(20) NOT NULL DEFAULT 'TECHNICAL' COMMENT '规则分类（TECHNICAL-技术面, FUNDAMENTAL-基本面）',
    `score_type` varchar(20) NOT NULL DEFAULT 'FIXED' COMMENT '打分类型（FIXED-固定分, DYNAMIC-动态分）',
    `base_score` int NOT NULL DEFAULT 10 COMMENT '基础分数',
    `max_score` int NOT NULL DEFAULT 10 COMMENT '最高分数（动态打分时使用）',
    `weight` decimal(5,2) NOT NULL DEFAULT 1.00 COMMENT '权重系数（最终得分 = 规则得分 * 权重）',
    `config_json` text DEFAULT NULL COMMENT '规则参数配置（JSON格式，如均线周期、阈值等）',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-禁用, 1-启用）',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号（执行顺序）',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除, 1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_rule_code` (`rule_code`),
    INDEX `idx_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='打分规则配置表';

-- ============================================
-- 2. 打分记录表
-- ============================================
DROP TABLE IF EXISTS `stk_score_record`;
CREATE TABLE `stk_score_record` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `stock_code` varchar(20) NOT NULL COMMENT '股票代码',
    `rule_code` varchar(50) NOT NULL COMMENT '规则编码',
    `score_date` date NOT NULL COMMENT '打分日期',
    `score` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '得分',
    `weighted_score` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '加权得分（得分 * 权重）',
    `hit_flag` tinyint NOT NULL DEFAULT 0 COMMENT '命中标志（0-未命中, 1-命中）',
    `detail_json` text DEFAULT NULL COMMENT '打分详情（JSON格式，记录计算过程）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_stock_rule_date` (`stock_code`, `rule_code`, `score_date`),
    INDEX `idx_score_date` (`score_date`),
    INDEX `idx_stock_date` (`stock_code`, `score_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='打分记录表';

-- ============================================
-- 3. 推荐股票表
-- ============================================
DROP TABLE IF EXISTS `stk_recommend`;
CREATE TABLE `stk_recommend` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `stock_code` varchar(20) NOT NULL COMMENT '股票代码',
    `recommend_date` date NOT NULL COMMENT '推荐日期',
    `total_score` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '总分',
    `hit_rule_count` int NOT NULL DEFAULT 0 COMMENT '命中规则数',
    `total_rule_count` int NOT NULL DEFAULT 0 COMMENT '总规则数',
    `hit_rate` decimal(5,2) NOT NULL DEFAULT 0.00 COMMENT '命中率（%）',
    `rank` int DEFAULT NULL COMMENT '排名',
    `score_detail` text DEFAULT NULL COMMENT '得分明细（JSON格式）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_stock_date` (`stock_code`, `recommend_date`),
    INDEX `idx_recommend_date_score` (`recommend_date`, `total_score` DESC),
    INDEX `idx_recommend_date_rank` (`recommend_date`, `rank`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='推荐股票表';

-- ============================================
-- 4. 初始化规则配置数据
-- ============================================
INSERT INTO `stk_score_rule` (`rule_code`, `rule_name`, `rule_desc`, `category`, `score_type`, `base_score`, `max_score`, `weight`, `config_json`, `status`, `sort_order`, `remark`) VALUES
('MA_ALIGNMENT', '均线多头排列', '短期均线在长期均线上方，形成多头排列（MA5 > MA10 > MA20 > MA60），表明趋势向好', 'TECHNICAL', 'FIXED', 20, 20, 1.50, '{"ma5":5,"ma10":10,"ma20":20,"ma60":60}', 1, 1, '经典趋势判断指标，权重较高'),
('VOLUME_BREAK', '成交量突破', '成交量突破近期平均水平，表明资金关注度提升，可能有主力资金介入', 'TECHNICAL', 'DYNAMIC', 10, 20, 1.20, '{"period":20,"threshold":1.5}', 1, 2, '成交量是价格变动的先行指标'),
('CONTINUOUS_RISE', '连续上涨', '连续N天收盘价上涨，趋势向好，动能强劲', 'TECHNICAL', 'DYNAMIC', 10, 15, 1.00, '{"minDays":3,"maxDays":5}', 1, 3, '短期趋势强度指标'),
('MACD_GOLDEN_CROSS', 'MACD金叉', 'MACD指标DIF上穿DEA，形成金叉买入信号，趋势反转向上', 'TECHNICAL', 'FIXED', 15, 15, 1.30, '{"fastPeriod":12,"slowPeriod":26,"signalPeriod":9}', 1, 4, '经典趋势反转信号'),
('BREAK_HIGH', '突破前高', '突破近期高点，创新高表明上涨动能强劲，可能开启新一轮上涨', 'TECHNICAL', 'DYNAMIC', 10, 20, 1.10, '{"period":20,"breakRatio":1.01}', 1, 5, '突破前高是强势信号');
