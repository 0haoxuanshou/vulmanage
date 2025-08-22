package org.demon.vulmanage.service;

import org.demon.vulmanage.param.project.ProjectCreateParam;
import org.demon.vulmanage.param.project.ProjectQueryParam;
import org.demon.vulmanage.param.project.ProjectUpdateParam;
import org.demon.vulmanage.vo.common.PageVo;
import org.demon.vulmanage.vo.project.ProjectVo;

import java.util.List;

/**
 * 项目服务接口
 * 定义项目管理的业务方法
 */
public interface ProjectService {
    
    /**
     * 创建项目
     * @param param 项目创建参数
     * @return 创建的项目
     */
    ProjectVo createProject(ProjectCreateParam param);
    
    /**
     * 更新项目
     * @param param 项目更新参数
     * @return 更新的项目
     */
    ProjectVo updateProject(ProjectUpdateParam param);
    
    /**
     * 根据ID删除项目
     * @param id 项目ID
     * @return 是否删除成功
     */
    boolean deleteProject(Long id);
    
    /**
     * 根据ID获取项目
     * @param id 项目ID
     * @return 项目信息
     */
    ProjectVo getProjectById(Long id);
    
    /**
     * 分页查询项目
     * @param param 查询参数
     * @return 分页结果
     */
    PageVo<ProjectVo> getProjectsPage(ProjectQueryParam param);
    
    /**
     * 获取所有项目
     * @return 项目列表
     */
    List<ProjectVo> getAllProjects();
    
    /**
     * 根据创建用户ID获取项目列表
     * @param createdUserId 创建用户ID
     * @return 项目列表
     */
    List<ProjectVo> getProjectsByCreatedUserId(Long createdUserId);
}