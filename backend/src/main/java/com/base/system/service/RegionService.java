package com.base.system.service;

import com.base.system.dto.RegionCascadeNode;
import com.base.system.dto.RegionQueryRequest;
import com.base.system.dto.RegionTreeNode;
import com.base.system.entity.Region;

import java.util.List;

/**
 * 行政区划服务接口
 */
public interface RegionService {

    /**
     * 获取行政区划树
     *
     * @param request 查询请求
     * @return 树形结构列表
     */
    List<RegionTreeNode> getRegionTree(RegionQueryRequest request);

    /**
     * 根据父级ID查询子区划
     *
     * @param parentId 父级ID
     * @return 子区划列表
     */
    List<Region> getChildrenByParentId(Long parentId);

    /**
     * 根据层级查询区划
     *
     * @param level 层级
     * @return 区划列表
     */
    List<Region> getRegionsByLevel(Integer level);

    /**
     * 获取级联选择器数据（懒加载）
     *
     * @param parentId 父级ID（null表示查询顶级）
     * @return 级联节点列表
     */
    List<RegionCascadeNode> getCascadeNodes(Long parentId);

    /**
     * 根据区划代码获取完整路径
     *
     * @param regionCode 区划代码
     * @return 完整路径（如：广东省/广州市/天河区）
     */
    String getFullPath(String regionCode);

    /**
     * 根据区划代码查询
     *
     * @param regionCode 区划代码
     * @return 区划信息
     */
    Region getByRegionCode(String regionCode);

    /**
     * 根据ID查询区划详情
     *
     * @param id 区划ID
     * @return 区划信息
     */
    Region getById(Long id);

    /**
     * 创建区划
     *
     * @param region 区划信息
     */
    void createRegion(Region region);

    /**
     * 更新区划
     *
     * @param region 区划信息
     */
    void updateRegion(Region region);

    /**
     * 删除区划（逻辑删除）
     *
     * @param id 区划ID
     */
    void deleteRegion(Long id);

    /**
     * 批量导入区划数据
     *
     * @param regions 区划列表
     * @return 导入成功数量
     */
    int batchImport(List<Region> regions);

    /**
     * 搜索区划（支持名称、拼音）
     *
     * @param keyword 关键词
     * @return 区划列表
     */
    List<Region> searchRegions(String keyword);
}
