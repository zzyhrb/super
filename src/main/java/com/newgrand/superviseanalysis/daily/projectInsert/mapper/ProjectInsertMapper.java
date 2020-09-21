package com.newgrand.superviseanalysis.daily.projectInsert.mapper;

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

public interface ProjectInsertMapper {

    @Select({
            "<script>",
            "SELECT apply_flag,create_time,end_time,is_through,ppd.user_id,su.user_real_name,#{tableName} table_name FROM ${tableName} ",
            "ppd LEFT JOIN sys_user su ON su.user_id = ppd.user_id ",
            "WHERE 1 = 1 <when test='userId!=null and userId !=\"\"'> AND ppd.user_id = #{userId} </when>  ",
            " <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
            " <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.end_time &lt;=  #{map.endTime} </when>",
            "GROUP BY apply_flag,create_time,end_time,is_through,ppd.user_id,su.user_real_name ORDER BY create_time desc",
            "</script>"
    })
    List<Map<String, Object>> list(String tableName, Integer userId, @Param("map") Map<String, Object> map);
}
