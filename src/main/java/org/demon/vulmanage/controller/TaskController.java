package org.demon.vulmanage.controller;

import org.demon.vulmanage.common.Result;
import org.demon.vulmanage.param.task.TaskCreateParam;
import org.demon.vulmanage.param.task.TaskQueryParam;
import org.demon.vulmanage.param.task.TaskUpdateParam;
import org.demon.vulmanage.service.TaskService;
import org.demon.vulmanage.util.SecurityUtil;
import org.demon.vulmanage.vo.common.PageVo;
import org.demon.vulmanage.vo.task.TaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务管理控制器
 * 提供任务的CRUD和分页查询API
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    /**
     * 创建任务
     * @param param 任务创建参数
     * @return 创建的任务
     */
    @PostMapping
    public Result<TaskVo> createTask(@RequestBody TaskCreateParam param) {
        try {
            // 使用SecurityUtil获取当前用户ID
            Long currentUserId = SecurityUtil.getCurrentUserId();
            if (currentUserId != null) {
                param.setCreatedUserId(currentUserId);
            }
            
            TaskVo createdTask = taskService.createTask(param);
            return Result.success(createdTask);
        } catch (Exception e) {
            return Result.error("创建任务失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新任务
     * @param id 任务ID
     * @param param 任务更新参数
     * @return 更新的任务
     */
    @PutMapping("/{id}")
    public Result<TaskVo> updateTask(@PathVariable Long id, @RequestBody TaskUpdateParam param) {
        try {
            param.setId(id);
            TaskVo updatedTask = taskService.updateTask(param);
            if (updatedTask != null) {
                return Result.success(updatedTask);
            } else {
                return Result.error("任务不存在");
            }
        } catch (Exception e) {
            return Result.error("更新任务失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除任务
     * @param id 任务ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTask(@PathVariable Long id) {
        try {
            boolean deleted = taskService.deleteTask(id);
            if (deleted) {
                return Result.success(null);
            } else {
                return Result.error("任务不存在或删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除任务失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取任务
     * @param id 任务ID
     * @return 任务信息
     */
    @GetMapping("/{id}")
    public Result<TaskVo> getTaskById(@PathVariable Long id) {
        try {
            TaskVo task = taskService.getTaskById(id);
            if (task != null) {
                return Result.success(task);
            } else {
                return Result.error("任务不存在");
            }
        } catch (Exception e) {
            return Result.error("获取任务失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页查询任务
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping
    public Result<PageVo<TaskVo>> getTasksPage(TaskQueryParam param) {
        try {
            PageVo<TaskVo> tasksPage = taskService.getTasksPage(param);
            return Result.success(tasksPage);
        } catch (Exception e) {
            return Result.error("查询任务失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有任务
     * @return 任务列表
     */
    @GetMapping("/all")
    public Result<List<TaskVo>> getAllTasks() {
        try {
            List<TaskVo> tasks = taskService.getAllTasks();
            return Result.success(tasks);
        } catch (Exception e) {
            return Result.error("获取任务列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据项目ID获取任务列表
     * @param projectId 项目ID
     * @return 任务列表
     */
    @GetMapping("/project/{projectId}")
    public Result<List<TaskVo>> getTasksByProjectId(@PathVariable Long projectId) {
        try {
            List<TaskVo> tasks = taskService.getTasksByProjectId(projectId);
            return Result.success(tasks);
        } catch (Exception e) {
            return Result.error("获取项目任务列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据创建用户ID获取任务列表
     * @param createdUserId 创建用户ID
     * @return 任务列表
     */
    @GetMapping("/user/{createdUserId}")
    public Result<List<TaskVo>> getTasksByCreatedUserId(@PathVariable Long createdUserId) {
        try {
            List<TaskVo> tasks = taskService.getTasksByCreatedUserId(createdUserId);
            return Result.success(tasks);
        } catch (Exception e) {
            return Result.error("获取用户任务列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新任务状态
     * @param id 任务ID
     * @param status 新状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateTaskStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            boolean updated = taskService.updateTaskStatus(id, status);
            if (updated) {
                return Result.success(null);
            } else {
                return Result.error("任务不存在或状态更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新任务状态失败: " + e.getMessage());
        }
    }
}