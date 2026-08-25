package com.base.system.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 扫描文档整理服务接口
 * <p>
 * 提供「多张文档扫描图片 → 压缩合并成 PDF」的工作区式流程：
 * 开始整理(create) → 分批复存图(images) → 调整顺序(order) → 完成归档(finalize)。
 * 图片仅暂存服务器临时目录，完成/放弃后即时删除，不落 COS。
 * </p>
 *
 * @author base
 * @since 2026-08-25
 */
public interface ScanDocService {

    /**
     * 开始整理：创建文档工作区并分配唯一编号
     *
     * @param docName 文档命名
     * @return 包含 docId、docNo、docName 的 Map
     */
    Map<String, Object> create(String docName);

    /**
     * 追加图片到工作区
     *
     * @param docId 工作区唯一编号
     * @param files 本批次新增图片
     * @return 当前工作区图片列表 Map
     */
    Map<String, Object> appendImages(String docId, MultipartFile[] files);

    /**
     * 调整图片顺序
     *
     * @param docId 工作区唯一编号
     * @param ids   图片 id 有序数组（按新的页序）
     * @return 当前工作区图片列表 Map
     */
    Map<String, Object> reorderImages(String docId, java.util.List<String> ids);

    /**
     * 删除单张图片
     *
     * @param docId   工作区唯一编号
     * @param imageId 图片 id
     * @return 当前工作区图片列表 Map
     */
    Map<String, Object> deleteImage(String docId, String imageId);

    /**
     * 放弃整个工作区并清理临时文件
     *
     * @param docId 工作区唯一编号
     */
    void deleteWorkArea(String docId);

    /**
     * 完成归档：压缩合并成 PDF，上传 COS + 写 sys_file，删除源图
     *
     * @param docId   工作区唯一编号
     * @param maxSide 降采样后最长边像素
     * @param quality JPEG 压缩质量
     * @return 包含 targetFile 的 Map
     */
    Map<String, Object> finalize(String docId, int maxSide, int quality);
}