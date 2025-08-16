package org.demon.vulmanage.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("system_config")
public class SystemConfig {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String configKey;
    
    private String configValue;
    
    private String description;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}