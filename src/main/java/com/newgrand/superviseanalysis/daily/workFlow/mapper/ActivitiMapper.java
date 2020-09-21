package com.newgrand.superviseanalysis.daily.workFlow.mapper;

import com.newgrand.superviseanalysis.system.project.entity.PmProject;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import org.activiti.engine.task.Task;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */

public interface ActivitiMapper {

    @Select({"SELECT ROLE_ID_,VALUE_ FROM (",
            "SELECT TEXT_ VALUE_,(select USER_ID_ from ACT_RU_IDENTITYLINK WHERE TASK_ID_ = ${task.id} and TYPE_ = 'candidate' ) ROLE_ID_ FROM ACT_HI_VARINST WHERE ",
            " PROC_INST_ID_ = #{task.processInstanceId} AND NAME_ in('organ','applyFlag', 'tableName') )apply WHERE ROLE_ID_ = #{user.roleId}"})
    List<Map<String, Object>> pendingApproval(Task task, SysUser user);

    @Select({"SELECT top(1) project_id,create_time from ${tableName} WHERE apply_flag  = #{applyFlag}"})
    PmProject getProjectId(String tableName, String applyFlag);

    @Select({"SELECT ppd.detail_id,ppt.tree_id id,ppt.tree_name title,ppd.detail_value ,ppt.column_detail,ppt.parent_id pid,ppt.dict_id,ppd.create_time,ppd.end_time,is_through,type_id from pm_project_tree ppt LEFT join ${tableName} ppd ON ppt.tree_id = ppd.tree_id " ,
            "WHERE  is_delete = 0 AND (ppd.apply_flag = #{applyFlag})" ,
            "AND ppt.project_id = #{projectId} order by ppt.tree_id"})
    List<Map<String, Object>> getDetail(String tableName, String applyFlag, Integer projectId);

    @Select({
            "SELECT project_name,so.organ_name,(select convert(varchar, create_time, 111)+' ~ '+convert(varchar, end_time, 111)   from ${map.tableName} where apply_flag = #{map.applyFlag} GROUP BY apply_flag,create_time,end_time) date FROM pm_project_table ppt " +
                    " LEFT JOIN pm_project pp on ppt.project_id = pp.project_id " +
                    " LEFT JOIN sys_organ so ON so.organ_id = pp.organ_id" +
                    " where table_name = #{map.tableName}"
    })
    Map<String, Object> getOrganProjectDate(@Param("map") Map<String, Object> map);

    @Select("SELECT PROC_INST_ID_ FROM ACT_HI_VARINST WHERE NAME_ = 'applyFlag' and TEXT_ = #{applyFlag}")
    List<String> getProcId(@Param("applyFlag") String applyFlag);

    @Delete({
            "<script>DELETE FROM ACT_HI_PROCINST WHERE PROC_INST_ID_ in (",
            "<foreach collection='procIdList' item='procId' open='' separator=',' close=''> #{procId}",
            "</foreach>)",
            "</script>"
    })
    void removeProcHi(@Param("procIdList") List<String> procId);
}
