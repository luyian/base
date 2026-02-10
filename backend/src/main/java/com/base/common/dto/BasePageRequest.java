package com.base.common.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分页查询请求基类
 *
 * @author base
 */
@Data
public class BasePageRequest {

    /**
     * 当前页码
     */
    @ApiModelProperty(value = "当前页码", example = "1")
    @Min(value = 1, message = "页码最小值为1")
    private Long current = 1L;

    /**
     * 每页显示数量
     */
    @ApiModelProperty(value = "每页显示数量", example = "10")
    @Min(value = 1, message = "每页显示数量最小值为1")
    @Max(value = 500, message = "每页显示数量最大值为500")
    private Long size = 10L;

    /**
     * 构建 MyBatis Plus 分页对象
     *
     * @param <T> 实体类型
     * @return 分页对象
     */
    public <T> Page<T> buildPage() {
        return new Page<>(this.current, this.size);
    }
}
