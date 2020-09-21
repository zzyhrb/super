package com.newgrand.superviseanalysis.system.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTree;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */

public interface ProjectTreeMapper extends BaseMapper<PmProjectTree> {

    @Select("SELECT max(tree_id) FROM pm_project_tree WHERE parent_id = #{parentId}")
    String getMaxTreeId(@Param("parentId") String parentId);

    @Insert("INSERT INTO pm_project_tree \n" +
            "SELECT #{projectTree.treeId}+SUBSTRING(tree_id,3,len(tree_id)) tree_id,tree_name,type_id,tree_order,getdate(),${projectTree.userId} user_id,\n" +
            "#{projectTree.treeId}+SUBSTRING(parent_id,3,len(parent_id)) parent_id\n" +
            ",${projectTree.projectId} project_id,is_delete,column_descript,column_detail,dict_id FROM pm_project_tree WHERE is_delete = 0 AND project_id = ${copyProjectId} AND parent_id !=0")
    void copyProjectTree(@Param("projectTree") PmProjectTree projectTree,@Param("copyProjectId") String copyProjectId);
}
