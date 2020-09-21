package com.newgrand.superviseanalysis.statistics.comprehensive.mapper;

import com.newgrand.superviseanalysis.system.project.entity.PmProjectTable;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */

public interface ComprehensiveMapper {

    @Select({
            "<script>",
            "SELECT ppd.tree_id,parent_id,tree_name,detail_value,column_detail,ppd.create_time,ppd.end_time,detail_id,",
            "CASE when (select count(1) FROM pm_project_detail_hljzghyyxm ppb WHERE ppb.tree_id like ppd.tree_id+'%' AND ppb.tree_id != ppd.tree_id ) &gt; 0 then 0 else 1 end  isHaveChild",

            " FROM ${pmProjectTable.tableName}  ppd LEFT JOIN pm_project_tree ppt ON ppd.tree_id = ppt.tree_id WHERE apply_flag" ,
            " <choose>" ,
                " <when test='map.applyFlag!=null and map.applyFlag!=\"\"'> = #{map.applyFlag} </when>",
                "<otherwise> in (SELECT apply_flag FROM ${pmProjectTable.tableName} where is_through = 0 <when test='map.universalQuerySql!=null and map.universalQuerySql!=\"\"'> ${map.universalQuerySql} </when>" ,
                    " <when test='map.startTime!=null and map.startTime !=\"\"'> AND create_time &gt;=  #{map.startTime} </when>",
                    " <when test='map.endTime!=null and map.endTime !=\"\"'> AND create_time &lt;=  #{map.endTime} </when>",
                    " GROUP BY apply_flag)</otherwise>",
            "</choose> ",
            " ORDER BY ppd.create_time DESC,apply_flag,ppd.tree_id,tree_order DESC ",
            "</script>"
    })
    List<Map<String, Object>> list(@Param("pmProjectTable") PmProjectTable pmProjectTable, Map<String, Object> map);
}
