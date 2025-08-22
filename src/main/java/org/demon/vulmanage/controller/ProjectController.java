package org.demon.vulmanage.controller;

import org.demon.vulmanage.common.Result;
import org.demon.vulmanage.param.project.ProjectCreateParam;
import org.demon.vulmanage.param.project.ProjectQueryParam;
import org.demon.vulmanage.param.project.ProjectUpdateParam;
import org.demon.vulmanage.service.ProjectService;
import org.demon.vulmanage.util.SecurityUtil;
import org.demon.vulmanage.vo.common.PageVo;
import org.demon.vulmanage.vo.project.ProjectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目管理控制器
 * 提供项目的CRUD和分页查询API
 */
@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    /**
     * 创建项目
     * @param param 项目创建参数
     * @return 创建的项目
     */
    @PostMapping
    public Result<ProjectVo> createProject(@RequestBody ProjectCreateParam param) {
        try {
            // 使用SecurityUtil获取当前用户ID
            Long currentUserId = SecurityUtil.getCurrentUserId();
            if (currentUserId != null) {
                param.setCreatedUserId(currentUserId);
            }
            
            ProjectVo createdProject = projectService.createProject(param);
            return Result.success(createdProject);
        } catch (Exception e) {
            return Result.error("创建项目失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新项目
     * @param id 项目ID
     * @param param 项目更新参数
     * @return 更新的项目
     */
    @PutMapping("/{id}")
    public Result<ProjectVo> updateProject(@PathVariable Long id, @RequestBody ProjectUpdateParam param) {
        try {
            param.setId(id);
            ProjectVo updatedProject = projectService.updateProject(param);
            if (updatedProject != null) {
                return Result.success(updatedProject);
            } else {
                return Result.error("项目不存在");
            }
        } catch (Exception e) {
            return Result.error("更新项目失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除项目
     * @param id 项目ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteProject(@PathVariable Long id) {
        try {
            boolean deleted = projectService.deleteProject(id);
            if (deleted) {
                return Result.success();
            } else {
                return Result.error("项目不存在");
            }
        } catch (Exception e) {
            return Result.error("删除项目失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取项目
     * @param id 项目ID
     * @return 项目信息
     */
    @GetMapping("/{id}")
    public Result<ProjectVo> getProjectById(@PathVariable Long id) {
        try {
            ProjectVo project = projectService.getProjectById(id);
            if (project != null) {
                return Result.success(project);
            } else {
                return Result.error("项目不存在");
            }
        } catch (Exception e) {
            return Result.error("获取项目失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页查询项目
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping
    public Result<PageVo<ProjectVo>> getProjectsPage(ProjectQueryParam param) {
        try {
            PageVo<ProjectVo> result = projectService.getProjectsPage(param);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询项目失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有项目
     * @return 项目列表
     */
    @GetMapping("/all")
    public Result<List<ProjectVo>> getAllProjects() {
        try {
            List<ProjectVo> projects = projectService.getAllProjects();
            return Result.success(projects);
        } catch (Exception e) {
            return Result.error("获取项目列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据创建用户ID获取项目列表
     * @param createdUserId 创建用户ID
     * @return 项目列表
     */
    @GetMapping("/user/{createdUserId}")
    public Result<List<ProjectVo>> getProjectsByCreatedUserId(@PathVariable Long createdUserId) {
        try {
            List<ProjectVo> projects = projectService.getProjectsByCreatedUserId(createdUserId);
            return Result.success(projects);
        } catch (Exception e) {
            return Result.error("获取用户项目列表失败: " + e.getMessage());
        }
    }
}