package org.demon.vulmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demon.vulmanage.mapper.ProjectMapper;
import org.demon.vulmanage.mapper.UserMapper;
import org.demon.vulmanage.model.Project;
import org.demon.vulmanage.model.User;
import org.demon.vulmanage.param.project.ProjectCreateParam;
import org.demon.vulmanage.param.project.ProjectQueryParam;
import org.demon.vulmanage.param.project.ProjectUpdateParam;
import org.demon.vulmanage.service.ProjectService;
import org.demon.vulmanage.vo.common.PageVo;
import org.demon.vulmanage.vo.project.ProjectVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目服务实现类
 * 实现项目管理的具体业务逻辑
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    
    @Autowired
    private ProjectMapper projectMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public ProjectVo createProject(ProjectCreateParam param) {
        Project project = new Project();
        BeanUtils.copyProperties(param, project);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        projectMapper.insert(project);
        return getProjectById(project.getId());
    }
    
    @Override
    public ProjectVo updateProject(ProjectUpdateParam param) {
        Project project = new Project();
        BeanUtils.copyProperties(param, project);
        project.setUpdatedAt(LocalDateTime.now());
        projectMapper.updateById(project);
        return getProjectById(project.getId());
    }
    
    @Override
    public boolean deleteProject(Long id) {
        return projectMapper.deleteById(id) > 0;
    }
    
    @Override
    public ProjectVo getProjectById(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            return null;
        }
        return convertToVo(project);
    }
    
    /**
     * 将Project实体转换为ProjectVo
     */
    private ProjectVo convertToVo(Project project) {
        ProjectVo vo = new ProjectVo();
        BeanUtils.copyProperties(project, vo);
        
        // 填充创建用户名称
        User user = userMapper.selectById(project.getCreatedUserId());
        if (user != null) {
            vo.setCreatedUserName(user.getUsername());
        }
        
        return vo;
    }
    
    @Override
    public PageVo<ProjectVo> getProjectsPage(ProjectQueryParam param) {
        Page<Project> page = new Page<>(param.getPage(), param.getSize());
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        
        // 项目名称模糊查询
        if (StringUtils.hasText(param.getName())) {
            queryWrapper.like(Project::getName, param.getName());
        }
        
        // 创建用户ID精确查询
        if (param.getCreatedUserId() != null) {
            queryWrapper.eq(Project::getCreatedUserId, param.getCreatedUserId());
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(Project::getCreatedAt);
        
        IPage<Project> result = projectMapper.selectPage(page, queryWrapper);
        
        // 转换为VO
        List<ProjectVo> records = result.getRecords().stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
        
        return new PageVo<>(records, result.getTotal(), param.getPage(), param.getSize());
    }
    
    @Override
    public List<ProjectVo> getAllProjects() {
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Project::getCreatedAt);
        
        List<Project> projects = projectMapper.selectList(queryWrapper);
        
        return projects.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProjectVo> getProjectsByCreatedUserId(Long createdUserId) {
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getCreatedUserId, createdUserId)
                   .orderByDesc(Project::getCreatedAt);
        
        List<Project> projects = projectMapper.selectList(queryWrapper);
        
        return projects.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }
}