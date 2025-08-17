package org.demon.vulmanage.service;

import java.util.Map;

/**
 * 系统信息服务接口
 */
public interface SystemInfoService {

    /**
     * 获取完整的系统信息
     * @return 系统信息Map
     */
    Map<String, Object> getSystemInfo();

    /**
     * 获取CPU信息
     * @return CPU信息Map
     */
    Map<String, Object> getCpuInfo();

    /**
     * 获取内存信息
     * @return 内存信息Map
     */
    Map<String, Object> getMemoryInfo();

    /**
     * 获取磁盘信息
     * @return 磁盘信息Map
     */
    Map<String, Object> getDiskInfo();

    /**
     * 获取网络信息
     * @return 网络信息Map
     */
    Map<String, Object> getNetworkInfo();
}