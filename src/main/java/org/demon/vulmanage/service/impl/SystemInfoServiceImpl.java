package org.demon.vulmanage.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demon.vulmanage.service.SystemInfoService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 系统信息服务实现类
 */
@Slf4j
@Service
public class SystemInfoServiceImpl implements SystemInfoService {

    private final DecimalFormat df = new DecimalFormat("#.##");

    @Override
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        
        try {
            systemInfo.put("cpu", getCpuInfo());
            systemInfo.put("memory", getMemoryInfo());
            systemInfo.put("disk", getDiskInfo());
            systemInfo.put("network", getNetworkInfo());
            systemInfo.put("os", getOsInfo());
            systemInfo.put("jvm", getJvmInfo());
            systemInfo.put("timestamp", System.currentTimeMillis());
        } catch (Exception e) {
            log.error("获取系统信息失败: {}", e.getMessage(), e);
            systemInfo.put("error", "获取系统信息失败: " + e.getMessage());
        }
        
        return systemInfo;
    }

    @Override
    public Map<String, Object> getCpuInfo() {
        Map<String, Object> cpuInfo = new HashMap<>();
        
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            
            cpuInfo.put("processors", osBean.getAvailableProcessors());
            cpuInfo.put("architecture", osBean.getArch());
            
            // 尝试获取CPU使用率（可能需要特定的JVM实现）
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOsBean = 
                    (com.sun.management.OperatingSystemMXBean) osBean;
                double cpuUsage = sunOsBean.getProcessCpuLoad() * 100;
                double systemCpuUsage = sunOsBean.getSystemCpuLoad() * 100;
                
                cpuInfo.put("processCpuUsage", cpuUsage >= 0 ? df.format(cpuUsage) + "%" : "N/A");
                cpuInfo.put("systemCpuUsage", systemCpuUsage >= 0 ? df.format(systemCpuUsage) + "%" : "N/A");
            }
            
            // 获取系统负载平均值
            double loadAverage = osBean.getSystemLoadAverage();
            cpuInfo.put("loadAverage", loadAverage >= 0 ? df.format(loadAverage) : "N/A");
            
        } catch (Exception e) {
            log.error("获取CPU信息失败: {}", e.getMessage());
            cpuInfo.put("error", "获取CPU信息失败: " + e.getMessage());
        }
        
        return cpuInfo;
    }

    @Override
    public Map<String, Object> getMemoryInfo() {
        Map<String, Object> memoryInfo = new HashMap<>();
        
        try {
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            Runtime runtime = Runtime.getRuntime();
            
            // JVM内存信息
            long jvmTotal = runtime.totalMemory();
            long jvmFree = runtime.freeMemory();
            long jvmUsed = jvmTotal - jvmFree;
            long jvmMax = runtime.maxMemory();
            
            Map<String, Object> jvmMemory = new HashMap<>();
            jvmMemory.put("total", formatBytes(jvmTotal));
            jvmMemory.put("used", formatBytes(jvmUsed));
            jvmMemory.put("free", formatBytes(jvmFree));
            jvmMemory.put("max", formatBytes(jvmMax));
            jvmMemory.put("usagePercent", df.format((double) jvmUsed / jvmTotal * 100) + "%");
            
            memoryInfo.put("jvm", jvmMemory);
            
            // 尝试获取系统内存信息
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOsBean = 
                    (com.sun.management.OperatingSystemMXBean) osBean;
                
                long totalPhysical = sunOsBean.getTotalPhysicalMemorySize();
                long freePhysical = sunOsBean.getFreePhysicalMemorySize();
                long usedPhysical = totalPhysical - freePhysical;
                
                Map<String, Object> systemMemory = new HashMap<>();
                systemMemory.put("total", formatBytes(totalPhysical));
                systemMemory.put("used", formatBytes(usedPhysical));
                systemMemory.put("free", formatBytes(freePhysical));
                systemMemory.put("usagePercent", df.format((double) usedPhysical / totalPhysical * 100) + "%");
                
                memoryInfo.put("system", systemMemory);
            }
            
        } catch (Exception e) {
            log.error("获取内存信息失败: {}", e.getMessage());
            memoryInfo.put("error", "获取内存信息失败: " + e.getMessage());
        }
        
        return memoryInfo;
    }

    @Override
    public Map<String, Object> getDiskInfo() {
        Map<String, Object> diskInfo = new HashMap<>();
        
        try {
            File[] roots = File.listRoots();
            List<Map<String, Object>> disks = new ArrayList<>();
            
            long totalSpace = 0;
            long totalUsed = 0;
            long totalFree = 0;
            
            for (File root : roots) {
                Map<String, Object> disk = new HashMap<>();
                
                long total = root.getTotalSpace();
                long free = root.getFreeSpace();
                long used = total - free;
                
                totalSpace += total;
                totalUsed += used;
                totalFree += free;
                
                disk.put("path", root.getAbsolutePath());
                disk.put("total", formatBytes(total));
                disk.put("used", formatBytes(used));
                disk.put("free", formatBytes(free));
                disk.put("usagePercent", total > 0 ? df.format((double) used / total * 100) + "%" : "0%");
                
                disks.add(disk);
            }
            
            diskInfo.put("disks", disks);
            
            // 总计信息
            Map<String, Object> summary = new HashMap<>();
            summary.put("total", formatBytes(totalSpace));
            summary.put("used", formatBytes(totalUsed));
            summary.put("free", formatBytes(totalFree));
            summary.put("usagePercent", totalSpace > 0 ? df.format((double) totalUsed / totalSpace * 100) + "%" : "0%");
            
            diskInfo.put("summary", summary);
            
        } catch (Exception e) {
            log.error("获取磁盘信息失败: {}", e.getMessage());
            diskInfo.put("error", "获取磁盘信息失败: " + e.getMessage());
        }
        
        return diskInfo;
    }

    @Override
    public Map<String, Object> getNetworkInfo() {
        Map<String, Object> networkInfo = new HashMap<>();
        
        try {
            List<Map<String, Object>> interfaces = new ArrayList<>();
            
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                
                Map<String, Object> interfaceInfo = new HashMap<>();
                interfaceInfo.put("name", networkInterface.getName());
                interfaceInfo.put("displayName", networkInterface.getDisplayName());
                interfaceInfo.put("isUp", networkInterface.isUp());
                interfaceInfo.put("isLoopback", networkInterface.isLoopback());
                interfaceInfo.put("supportsMulticast", networkInterface.supportsMulticast());
                
                // 获取IP地址
                List<String> addresses = new ArrayList<>();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress address = inetAddresses.nextElement();
                    addresses.add(address.getHostAddress());
                }
                interfaceInfo.put("addresses", addresses);
                
                // 获取MAC地址
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    StringBuilder macAddress = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        macAddress.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
                    }
                    interfaceInfo.put("macAddress", macAddress.toString());
                }
                
                interfaces.add(interfaceInfo);
            }
            
            networkInfo.put("interfaces", interfaces);
            
            // 获取主机名
            try {
                networkInfo.put("hostname", InetAddress.getLocalHost().getHostName());
            } catch (Exception e) {
                networkInfo.put("hostname", "Unknown");
            }
            
        } catch (Exception e) {
            log.error("获取网络信息失败: {}", e.getMessage());
            networkInfo.put("error", "获取网络信息失败: " + e.getMessage());
        }
        
        return networkInfo;
    }

    /**
     * 获取操作系统信息
     */
    private Map<String, Object> getOsInfo() {
        Map<String, Object> osInfo = new HashMap<>();
        
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            
            osInfo.put("name", osBean.getName());
            osInfo.put("version", osBean.getVersion());
            osInfo.put("arch", osBean.getArch());
            osInfo.put("availableProcessors", osBean.getAvailableProcessors());
            
            // 系统属性
            osInfo.put("javaVersion", System.getProperty("java.version"));
            osInfo.put("javaVendor", System.getProperty("java.vendor"));
            osInfo.put("javaHome", System.getProperty("java.home"));
            osInfo.put("userDir", System.getProperty("user.dir"));
            osInfo.put("userHome", System.getProperty("user.home"));
            osInfo.put("userName", System.getProperty("user.name"));
            
        } catch (Exception e) {
            log.error("获取操作系统信息失败: {}", e.getMessage());
            osInfo.put("error", "获取操作系统信息失败: " + e.getMessage());
        }
        
        return osInfo;
    }

    /**
     * 获取JVM信息
     */
    private Map<String, Object> getJvmInfo() {
        Map<String, Object> jvmInfo = new HashMap<>();
        
        try {
            Runtime runtime = Runtime.getRuntime();
            
            jvmInfo.put("jvmName", System.getProperty("java.vm.name"));
            jvmInfo.put("jvmVersion", System.getProperty("java.vm.version"));
            jvmInfo.put("jvmVendor", System.getProperty("java.vm.vendor"));
            jvmInfo.put("javaVersion", System.getProperty("java.version"));
            jvmInfo.put("javaHome", System.getProperty("java.home"));
            
            // JVM启动时间
            long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
            long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
            
            jvmInfo.put("startTime", new Date(startTime));
            jvmInfo.put("uptime", formatDuration(uptime));
            
            // 处理器数量
            jvmInfo.put("availableProcessors", runtime.availableProcessors());
            
        } catch (Exception e) {
            log.error("获取JVM信息失败: {}", e.getMessage());
            jvmInfo.put("error", "获取JVM信息失败: " + e.getMessage());
        }
        
        return jvmInfo;
    }

    /**
     * 格式化字节数
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return df.format(size) + " " + units[unitIndex];
    }

    /**
     * 格式化持续时间
     */
    private String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + "天 " + (hours % 24) + "小时 " + (minutes % 60) + "分钟";
        } else if (hours > 0) {
            return hours + "小时 " + (minutes % 60) + "分钟";
        } else if (minutes > 0) {
            return minutes + "分钟 " + (seconds % 60) + "秒";
        } else {
            return seconds + "秒";
        }
    }
}