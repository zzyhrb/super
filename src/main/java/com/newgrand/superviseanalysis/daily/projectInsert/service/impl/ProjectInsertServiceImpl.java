package com.newgrand.superviseanalysis.daily.projectInsert.service.impl;

import com.newgrand.superviseanalysis.daily.projectInsert.mapper.ProjectInsertMapper;
import com.newgrand.superviseanalysis.daily.projectInsert.service.IProjectInsertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */
@Service
public class ProjectInsertServiceImpl implements IProjectInsertService {
    private ProjectInsertMapper projectInsertMapper;

    @Autowired
    public ProjectInsertServiceImpl(ProjectInsertMapper projectInsertMapper) {
        this.projectInsertMapper = projectInsertMapper;
    }

    @Override
    public List<Map<String, Object>> list(String tableName, Integer userId, Map<String, Object> map) {
        return projectInsertMapper.list(tableName, userId, map);
    }
}
