ALTER TABLE `sys_ai_config`
    ADD COLUMN `image_base_url` varchar(500) DEFAULT NULL COMMENT '图片生成 API 地址' AFTER `model`,
    ADD COLUMN `image_model` varchar(100) DEFAULT NULL COMMENT '图片生成模型名称' AFTER `image_base_url`,
    ADD COLUMN `image_adapter` varchar(50) DEFAULT NULL COMMENT '图片生成适配器' AFTER `image_model`;
