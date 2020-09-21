package com.newgrand.superviseanalysis.system.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTable;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTableModel;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-29
 */

public interface ProjectTableMapper extends BaseMapper<PmProjectTable> {

    @Update({"CREATE TABLE [dbo].[${tableName}](",
            " [detail_id] varchar(50) COLLATE Chinese_PRC_CI_AS  NOT NULL primary key,",
            " [detail_name] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NULL,",
            " [detail_value] numeric(38,2)  NULL,",
            " [tree_id] varchar(100) COLLATE Chinese_PRC_CI_AS  NULL,",
            " [project_id] int  NULL,",
            " [organ_id] varchar(50) COLLATE Chinese_PRC_CI_AS  NULL,",
            " [user_id] int  NULL,",
            " [create_time] datetime  NULL,",
            " [is_through] int  NULL,",
            " [apply_flag] varchar(50),[end_time] datetime  NULL,[create_time2] datetime  NULL) "})
    boolean create(@Param("tableName") String tableName);

    @Insert({"INSERT INTO ${tableName}(detail_id,",
            "detail_name,",
            "detail_value,",
            "tree_id,",
            "project_id,",
            "organ_id,",
            "user_id,",
            "create_time,",
            "is_through,",
            "apply_flag,",
            "end_time,",
            "create_time2",

            ") VALUES(",
            "#{pmProjectTableModel.detailId},",
            "#{pmProjectTableModel.detailName},",
            "#{pmProjectTableModel.detailValue},",
            "#{pmProjectTableModel.treeId},",
            "#{pmProjectTableModel.projectId},",
            "#{pmProjectTableModel.organId},",
            "#{pmProjectTableModel.userId},",
            "#{pmProjectTableModel.createTime},",
            "#{pmProjectTableModel.isThrough},",
            "#{pmProjectTableModel.applyFlag},",
            "#{pmProjectTableModel.endTime}," ,
            "#{pmProjectTableModel.createTime2}",
            ")"})
    boolean save(@Param("tableName") String tableName, @Param("pmProjectTableModel") PmProjectTableModel pmProjectTableModel);

    @Update({"UPDATE ${tableName} ",
            " set detail_name = #{pmProjectTableModel.detailName},",
            "  detail_value = #{pmProjectTableModel.detailValue},",
            "  tree_id = #{pmProjectTableModel.treeId},",
            "  project_id = #{pmProjectTableModel.projectId},",
            "  organ_id = #{pmProjectTableModel.organId},",
            "  is_through = #{pmProjectTableModel.isThrough},",
            "  create_time = #{pmProjectTableModel.createTime},",
            "  end_time = #{pmProjectTableModel.endTime},",
            "  create_time2 = #{pmProjectTableModel.createTime2}",

            " WHERE detail_id = #{pmProjectTableModel.detailId}"})
    boolean update(@Param("tableName") String tableName, @Param("pmProjectTableModel") PmProjectTableModel pmProjectTableModel);

    @Select({
            "SELECT table_name FROM pm_project pp LEFT JOIN pm_project_table ppt ON pp.project_id = ppt.project_id ",
            "WHERE pp.is_delete = 0 AND pp.organ_id = #{organId}"
    })
    List<String> queryTableListByOrganId(@Param("organId") String organId);

    @Select({
            "SELECT ppt.table_name FROM pm_project pp LEFT JOIN sys_organ oo ON pp.organ_id = oo.organ_id RIGHT JOIN pm_project_table ppt ON ppt.project_id = pp.project_id ",
            " WHERE oo.organ_id LIKE '${organId}%' AND oo.is_delete = 0 AND pp.is_delete = 0"
    })
    List<String> selectTableNamesByOrganId(@Param("organId") String organId);

    @Select({"<script>",
            "SELECT count(1) FROM ${pmProjectTable.tableName}  WHERE CONVERT(varchar(100), pm_project_detail_hljzghyyxm.create_time, 111) = ",
            " <choose>",
                " <when test='createTime!=null and createTime!=\"\"'> CONVERT(varchar(100), CONVERT(datetime,#{createTime}), 111) </when>",
                "<otherwise>  CONVERT(varchar(100), GETDATE(), 111)</otherwise>",
            "</choose> ",
            "</script>"
    })
    Integer count(PmProjectTable pmProjectTable, String createTime);

    @Select({"<script>",
            " SELECT apply.create_time,apply.end_time,apply_flag,project_name,tableName FROM ( ",
            " <foreach collection='tableNames' item='tableName' open='' separator='union' close=''>",
            "SELECT apply_flag,end_time,create_time,project_id,#{tableName} tableName  FROM ${tableName} WHERE is_through = 1 and user_id = #{user.userId} GROUP BY apply_flag,create_time,end_time,project_id",
            " </foreach> ) apply LEFT JOIN pm_project pp ON pp.project_id = apply.project_id WHERE pp.is_delete = 0 ORDER BY create_time desc" ,
            "</script>"})
    List<Map<String, Object>> getApplyList(List<String> tableNames, SysUser user);

    @Update("UPDATE ${tableName} SET is_through=#{i} WHERE apply_flag = #{applyFlag}")
    void setIsThrow(String tableName, String applyFlag, int i);

    @Delete({" DELETE FROM ${pmProjectTable.tableName} WHERE apply_flag = #{applyFlag}"})
    Integer del(PmProjectTable pmProjectTable, String applyFlag);
}
