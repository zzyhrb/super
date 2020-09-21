package com.newgrand.superviseanalysis.daily.workFlow.service.impl;

import com.newgrand.superviseanalysis.daily.workFlow.mapper.ActivitiMapper;
import com.newgrand.superviseanalysis.daily.workFlow.service.IActivitiService;
import com.newgrand.superviseanalysis.system.project.entity.PmProject;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import org.activiti.engine.task.Task;
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
public class ActivitiServiceImpl implements IActivitiService {
    @Autowired
    private ActivitiMapper activitiMapper;

    @Override
    public List<Map<String, Object>> pendingApproval(Task task, SysUser user) {
        return activitiMapper.pendingApproval(task, user);
    }

    @Override
    public PmProject getProjectId(String tableName, String applyFlag) {
        return activitiMapper.getProjectId(tableName, applyFlag);
    }

    @Override
    public List<Map<String, Object>> getDetail(String tableName, String applyFlag, Integer projectId) {
        return activitiMapper.getDetail(tableName, applyFlag, projectId);
    }

    @Override
    public Map<String, Object> getOrganProjectDate(Map<String, Object> map) {
        return activitiMapper.getOrganProjectDate(map);
    }

    @Override
    public List<String> getProcId(String applyFlag) {
        return activitiMapper.getProcId(applyFlag);
    }

    @Override
    public void removeProcHi(List<String> procId) {
        if (procId != null && procId.size() > 0) {
            activitiMapper.removeProcHi(procId);
        }
    }
}
