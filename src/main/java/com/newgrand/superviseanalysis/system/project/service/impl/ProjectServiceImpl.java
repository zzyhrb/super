package com.newgrand.superviseanalysis.system.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.newgrand.superviseanalysis.system.project.entity.PmProject;
import com.newgrand.superviseanalysis.system.project.mapper.ProjectMapper;
import com.newgrand.superviseanalysis.system.project.service.IProjectService;
import com.newgrand.superviseanalysis.system.project.vo.ProjectOrganUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, PmProject> implements IProjectService {

    @Autowired
    private ProjectMapper projectMapper;
    @Override
    public List<ProjectOrganUserVO> getProject(ProjectOrganUserVO projectOrganUserVO) {
        return projectMapper.getProject(projectOrganUserVO);
    }

    @Override
    public String getProjectMaxTreeId() {
        return projectMapper.getProjectMaxTreeId();
    }
}
