package com.newgrand.superviseanalysis.daily.workFlow.service;

import com.newgrand.superviseanalysis.system.project.entity.PmProject;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */
public interface IActivitiService {

    List<Map<String, Object>> pendingApproval(Task task, SysUser user);

    PmProject getProjectId(String tableName, String applyFlag);

    List<Map<String, Object>> getDetail(String tableName, String applyFlag, Integer projectId);

    Map<String, Object> getOrganProjectDate(Map<String, Object> map);

    List<String> getProcId(String applyFlag);

    void removeProcHi(List<String> procId);
}
