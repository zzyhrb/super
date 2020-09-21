package com.newgrand.superviseanalysis.system.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectPurpose;
import com.newgrand.superviseanalysis.system.project.mapper.ProjectPurposeMapper;
import com.newgrand.superviseanalysis.system.project.service.IProjectPurposeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-04-20
 */
@Service
public class ProjectPurposeServiceImpl extends ServiceImpl<ProjectPurposeMapper, PmProjectPurpose> implements IProjectPurposeService {
    private  ProjectPurposeMapper projectPurposeMapper;
    @Autowired
    public ProjectPurposeServiceImpl(ProjectPurposeMapper projectPurposeMapper) {
        this.projectPurposeMapper = projectPurposeMapper;
    }

    @Override
    public List<Map<String, Object>> getlistByDetailId(String detailId) {
        return projectPurposeMapper.getlistByDetailId(detailId);
    }
}
