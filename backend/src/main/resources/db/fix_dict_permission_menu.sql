-- 修复枚举管理迁移为字典管理后的历史菜单与权限编码
UPDATE `sys_permission`
SET `permission_name` = '字典管理',
    `permission_code` = 'system:dict:list',
    `path` = '/system/dict',
    `component` = 'system/Dict',
    `update_time` = NOW(),
    `update_by` = 'system'
WHERE (`id` = 105 OR `permission_code` = 'system:enum:list')
  AND `deleted` = 0;

UPDATE `sys_permission`
SET `permission_name` = '字典查询',
    `permission_code` = 'system:dict:query',
    `update_time` = NOW(),
    `update_by` = 'system'
WHERE (`id` = 10501 OR `permission_code` = 'system:enum:query')
  AND `deleted` = 0;

UPDATE `sys_permission`
SET `permission_name` = '字典新增',
    `permission_code` = 'system:dict:add',
    `update_time` = NOW(),
    `update_by` = 'system'
WHERE (`id` = 10502 OR `permission_code` = 'system:enum:add')
  AND `deleted` = 0;

UPDATE `sys_permission`
SET `permission_name` = '字典编辑',
    `permission_code` = 'system:dict:edit',
    `update_time` = NOW(),
    `update_by` = 'system'
WHERE (`id` = 10503 OR `permission_code` = 'system:enum:edit')
  AND `deleted` = 0;

UPDATE `sys_permission`
SET `permission_name` = '字典删除',
    `permission_code` = 'system:dict:delete',
    `update_time` = NOW(),
    `update_by` = 'system'
WHERE (`id` = 10504 OR `permission_code` = 'system:enum:delete')
  AND `deleted` = 0;
