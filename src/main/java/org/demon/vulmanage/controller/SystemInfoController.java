package org.demon.vulmanage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demon.vulmanage.common.Result;
import org.demon.vulmanage.service.SystemInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 系统信息控制器
 * 提供服务器配置信息API
 */
@Slf4j
@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemInfoController {

    private final SystemInfoService systemInfoService;

    /**
     * 获取系统信息
     * @return 系统配置信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getSystemInfo() {
        try {
            Map<String, Object> systemInfo = systemInfoService.getSystemInfo();
            return Result.success("获取系统信息成功", systemInfo);
        } catch (Exception e) {
            log.error("获取系统信息失败: {}", e.getMessage(), e);
            return Result.error("获取系统信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取CPU信息
     * @return CPU配置信息
     */
    @GetMapping("/cpu")
    public Result<Map<String, Object>> getCpuInfo() {
        try {
            Map<String, Object> cpuInfo = systemInfoService.getCpuInfo();
            return Result.success("获取CPU信息成功", cpuInfo);
        } catch (Exception e) {
            log.error("获取CPU信息失败: {}", e.getMessage(), e);
            return Result.error("获取CPU信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取内存信息
     * @return 内存配置信息
     */
    @GetMapping("/memory")
    public Result<Map<String, Object>> getMemoryInfo() {
        try {
            Map<String, Object> memoryInfo = systemInfoService.getMemoryInfo();
            return Result.success("获取内存信息成功", memoryInfo);
        } catch (Exception e) {
            log.error("获取内存信息失败: {}", e.getMessage(), e);
            return Result.error("获取内存信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取磁盘信息
     * @return 磁盘配置信息
     */
    @GetMapping("/disk")
    public Result<Map<String, Object>> getDiskInfo() {
        try {
            Map<String, Object> diskInfo = systemInfoService.getDiskInfo();
            return Result.success("获取磁盘信息成功", diskInfo);
        } catch (Exception e) {
            log.error("获取磁盘信息失败: {}", e.getMessage(), e);
            return Result.error("获取磁盘信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取网络信息
     * @return 网络配置信息
     */
    @GetMapping("/network")
    public Result<Map<String, Object>> getNetworkInfo() {
        try {
            Map<String, Object> networkInfo = systemInfoService.getNetworkInfo();
            return Result.success("获取网络信息成功", networkInfo);
        } catch (Exception e) {
            log.error("获取网络信息失败: {}", e.getMessage(), e);
            return Result.error("获取网络信息失败: " + e.getMessage());
        }
    }
}