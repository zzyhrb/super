package com.newgrand.superviseanalysis.system.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTable;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTableModel;
import com.newgrand.superviseanalysis.system.project.mapper.ProjectTableMapper;
import com.newgrand.superviseanalysis.system.project.service.IProjectTableService;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
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
 * @since 2020-03-29
 */
@Service
public class ProjectTableServiceImpl extends ServiceImpl<ProjectTableMapper, PmProjectTable> implements IProjectTableService {
    @Autowired
    private ProjectTableMapper projectTableMapper;

    @Override
    public boolean create(String tableName) {
        return projectTableMapper.create(tableName);
    }

    @Override
    public boolean save(String tableName,PmProjectTableModel pmProjectTableModel) {
        return projectTableMapper.save(tableName,pmProjectTableModel);
    }

    @Override
    public boolean update(String tableName, PmProjectTableModel pmProjectTableModel) {
        return projectTableMapper.update(tableName,pmProjectTableModel);
    }

    @Override
    public List<String> selectTableNamesByOrganId(String organId) {
        return projectTableMapper.selectTableNamesByOrganId(organId);
    }

    @Override
    public Integer count(PmProjectTable pmProjectTable, String createTime) {
        return projectTableMapper.count(pmProjectTable,createTime);
    }

    @Override
    public Integer del(PmProjectTable pmProjectTable, String applyFlag) {
        return projectTableMapper.del(pmProjectTable,applyFlag);
    }

    @Override
    public List<Map<String, Object>> getApplyList(List<String> tableNames, SysUser user) {
        return projectTableMapper.getApplyList(tableNames, user);
    }

    @Override
    public void setIsThrow(String tableName, String applyFlag, int i) {
        projectTableMapper.setIsThrow(tableName, applyFlag, i);

    }

}
