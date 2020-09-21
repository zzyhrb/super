package com.newgrand.superviseanalysis.system.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.superviseanalysis.system.project.entity.PmProject;
import com.newgrand.superviseanalysis.system.project.vo.ProjectOrganUserVO;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */

public interface ProjectMapper extends BaseMapper<PmProject> {
    @Select("SELECT pp.project_id,pp.project_name,pp.create_time,pp.project_descript,su.user_real_name,so.organ_name,pp.is_delete,sot.type_name FROM " +
            " pm_project pp LEFT JOIN  sys_user su ON su.user_id = pp.user_id " +
            " LEFT JOIN sys_organ so ON so.organ_id = pp.organ_id " +
            " LEFT JOIN sys_organ_type sot ON sot.type_id = so.type_id WHERE pp.is_delete <> -1 AND so.organ_id like '${project.organId}%' ")
    List<ProjectOrganUserVO> getProject(@Param("project") ProjectOrganUserVO projectOrganUserVO);

    @Select("SELECT MAX(tree_id) FROM pm_project_tree WHERE parent_id = 0 ")
    String getProjectMaxTreeId();
}
