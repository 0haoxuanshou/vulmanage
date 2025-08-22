package org.demon.vulmanage.service;

import org.demon.vulmanage.param.task.TaskCreateParam;
import org.demon.vulmanage.param.task.TaskQueryParam;
import org.demon.vulmanage.param.task.TaskUpdateParam;
import org.demon.vulmanage.vo.common.PageVo;
import org.demon.vulmanage.vo.task.TaskVo;

import java.util.List;

/**
 * 任务服务接口
 * 定义任务管理的业务方法
 */
public interface TaskService {
    
    /**
     * 创建任务
     * @param param 任务创建参数
     * @return 创建的任务
     */
    TaskVo createTask(TaskCreateParam param);
    
    /**
     * 更新任务
     * @param param 任务更新参数
     * @return 更新的任务
     */
    TaskVo updateTask(TaskUpdateParam param);
    
    /**
     * 删除任务
     * @param id 任务ID
     * @return 是否删除成功
     */
    boolean deleteTask(Long id);
    
    /**
     * 根据ID获取任务
     * @param id 任务ID
     * @return 任务信息
     */
    TaskVo getTaskById(Long id);
    
    /**
     * 分页查询任务
     * @param param 查询参数
     * @return 分页结果
     */
    PageVo<TaskVo> getTasksPage(TaskQueryParam param);
    
    /**
     * 获取所有任务
     * @return 任务列表
     */
    List<TaskVo> getAllTasks();
    
    /**
     * 根据项目ID获取任务列表
     * @param projectId 项目ID
     * @return 任务列表
     */
    List<TaskVo> getTasksByProjectId(Long projectId);
    
    /**
     * 根据创建用户ID获取任务列表
     * @param createdUserId 创建用户ID
     * @return 任务列表
     */
    List<TaskVo> getTasksByCreatedUserId(Long createdUserId);
    
    /**
     * 更新任务状态
     * @param id 任务ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateTaskStatus(Long id, String status);
}