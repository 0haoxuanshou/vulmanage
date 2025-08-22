package org.demon.vulmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demon.vulmanage.mapper.ProjectMapper;
import org.demon.vulmanage.mapper.TaskMapper;
import org.demon.vulmanage.mapper.UserMapper;
import org.demon.vulmanage.model.Project;
import org.demon.vulmanage.model.Task;
import org.demon.vulmanage.model.User;
import org.demon.vulmanage.param.task.TaskCreateParam;
import org.demon.vulmanage.param.task.TaskQueryParam;
import org.demon.vulmanage.param.task.TaskUpdateParam;
import org.demon.vulmanage.service.TaskService;
import org.demon.vulmanage.vo.common.PageVo;
import org.demon.vulmanage.vo.task.TaskVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务服务实现类
 * 实现任务管理的具体业务逻辑
 */
@Service
public class TaskServiceImpl implements TaskService {
    
    @Autowired
    private TaskMapper taskMapper;
    
    @Autowired
    private ProjectMapper projectMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public TaskVo createTask(TaskCreateParam param) {
        Task task = new Task();
        BeanUtils.copyProperties(param, task);
        task.setStatus(Task.TaskStatus.PENDING.name());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.insert(task);
        return getTaskById(task.getId());
    }
    
    @Override
    public TaskVo updateTask(TaskUpdateParam param) {
        Task task = new Task();
        BeanUtils.copyProperties(param, task);
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);
        return getTaskById(param.getId());
    }
    
    @Override
    public boolean deleteTask(Long id) {
        return taskMapper.deleteById(id) > 0;
    }
    
    @Override
    public TaskVo getTaskById(Long id) {
        Task task = taskMapper.selectById(id);
        if (task != null) {
            return convertToVo(task);
        }
        return null;
    }
    
    /**
     * 将Task实体转换为TaskVo
     */
    private TaskVo convertToVo(Task task) {
        TaskVo vo = new TaskVo();
        BeanUtils.copyProperties(task, vo);
        
        // 设置类型描述
        try {
            Task.TaskType taskType = Task.TaskType.valueOf(task.getType());
            vo.setTypeDescription(taskType.getDescription());
        } catch (Exception e) {
            vo.setTypeDescription(task.getType());
        }
        
        // 设置状态描述
        try {
            Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(task.getStatus());
            vo.setStatusDescription(taskStatus.getDescription());
        } catch (Exception e) {
            vo.setStatusDescription(task.getStatus());
        }
        
        // 设置项目名称
        if (task.getProjectId() != null) {
            Project project = projectMapper.selectById(task.getProjectId());
            if (project != null) {
                vo.setProjectName(project.getName());
            }
        }
        
        // 设置创建用户名称
        if (task.getCreatedUserId() != null) {
            User user = userMapper.selectById(task.getCreatedUserId());
            if (user != null) {
                vo.setCreatedUserName(user.getUsername());
            }
        }
        
        return vo;
    }
    
    @Override
    public PageVo<TaskVo> getTasksPage(TaskQueryParam param) {
        Page<Task> page = new Page<>(param.getPage(), param.getSize());
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(param.getName())) {
            wrapper.like(Task::getName, param.getName());
        }
        if (StringUtils.hasText(param.getType())) {
            wrapper.eq(Task::getType, param.getType());
        }
        if (StringUtils.hasText(param.getStatus())) {
            wrapper.eq(Task::getStatus, param.getStatus());
        }
        if (param.getProjectId() != null) {
            wrapper.eq(Task::getProjectId, param.getProjectId());
        }
        if (param.getCreatedUserId() != null) {
            wrapper.eq(Task::getCreatedUserId, param.getCreatedUserId());
        }
        
        wrapper.orderByDesc(Task::getCreatedAt);
        
        IPage<Task> taskPage = taskMapper.selectPage(page, wrapper);
        List<TaskVo> taskVos = taskPage.getRecords().stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
        
        return new PageVo<>(taskVos, taskPage.getTotal(), (int) taskPage.getCurrent(), (int) taskPage.getSize());
    }
    
    @Override
    public List<TaskVo> getAllTasks() {
        List<Task> tasks = taskMapper.selectList(new LambdaQueryWrapper<Task>()
                .orderByDesc(Task::getCreatedAt));
        return tasks.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskVo> getTasksByProjectId(Long projectId) {
        List<Task> tasks = taskMapper.selectList(new LambdaQueryWrapper<Task>()
                .eq(Task::getProjectId, projectId)
                .orderByDesc(Task::getCreatedAt));
        return tasks.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskVo> getTasksByCreatedUserId(Long createdUserId) {
        List<Task> tasks = taskMapper.selectList(new LambdaQueryWrapper<Task>()
                .eq(Task::getCreatedUserId, createdUserId)
                .orderByDesc(Task::getCreatedAt));
        return tasks.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean updateTaskStatus(Long id, String status) {
        Task task = new Task();
        task.setId(id);
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        
        // 如果状态是RUNNING，设置开始时间
        if (Task.TaskStatus.RUNNING.name().equals(status)) {
            task.setStartedAt(LocalDateTime.now());
        }
        // 如果状态是COMPLETED或FAILED，设置完成时间
        else if (Task.TaskStatus.COMPLETED.name().equals(status) || Task.TaskStatus.FAILED.name().equals(status)) {
            task.setCompletedAt(LocalDateTime.now());
        }
        
        return taskMapper.updateById(task) > 0;
    }
}