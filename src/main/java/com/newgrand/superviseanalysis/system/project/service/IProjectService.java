package com.newgrand.superviseanalysis.system.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.newgrand.superviseanalysis.system.project.entity.PmProject;
import com.newgrand.superviseanalysis.system.project.vo.ProjectOrganUserVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */
public interface IProjectService extends IService<PmProject> {
    List<ProjectOrganUserVO> getProject(ProjectOrganUserVO projectOrganUserVO);

    String getProjectMaxTreeId();
}
