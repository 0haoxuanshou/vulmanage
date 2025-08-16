package org.demon.vulmanage.param.common;

import lombok.Data;

/**
 * 通用分页查询参数
 */
@Data
public class PageParam {
    
    /**
     * 当前页码，默认为1
     */
    private Integer page = 1;
    
    /**
     * 每页大小，默认为10
     */
    private Integer size = 10;
    
    /**
     * 排序字段
     */
    private String sortBy;
    
    /**
     * 排序方向：asc(升序) 或 desc(降序)，默认为desc
     */
    private String sortDirection = "desc";
    
    /**
     * 获取偏移量
     */
    public Integer getOffset() {
        return (page - 1) * size;
    }
    
    /**
     * 验证并修正分页参数
     */
    public void validate() {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        if (size > 100) {
            size = 100; // 限制最大每页数量
        }
        if (sortDirection == null || (!"asc".equalsIgnoreCase(sortDirection) && !"desc".equalsIgnoreCase(sortDirection))) {
            sortDirection = "desc";
        }
    }
}