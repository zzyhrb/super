package com.newgrand.superviseanalysis.decision.yeareconomics.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-29
 */
public interface YearEconomicsMapper {

    @Select({"<script>",
            " SELECT year FROM ( ",
            " <foreach collection='projectTables' item='tableName' open='' separator='union' close=''>",
            "SELECT year(create_time) year FROM  ${tableName} WHERE 1 = 1",
            " <when test='map.startTime!=null and map.startTime !=\"\"'> AND create_time &gt;  #{map.startTime} </when>",
            " <when test='map.endTime!=null and map.endTime!=\"\"'> AND create_time &lt;  #{map.endTime} </when>",
                    " GROUP BY year(create_time)",
            " </foreach> ) yearTable ORDER BY year" ,
            "</script>"})
    List<String> allYear(@Param("projectTables") List<String> projectTables,Map<String,Object> map);

    @Select({"<script>",
            " SELECT CASE when SUM(money) is null then 0 else  Round(SUM(money),2) end FROM ( ",
            " <foreach collection='tableNames' item='tableName' open='' separator='UNION ALL' close=''>",
            "SELECT sum(detail_value) money FROM ${tableName} pdh LEFT JOIN pm_project_tree  pp ON pdh.tree_id = pp.tree_id WHERE pp.is_delete = 0 AND pp.type_id = #{typeId} AND len(pp.tree_id) = 4 AND year(pdh.create_time) = #{year} ",
            " </foreach> ) totalMoney ",
            "</script>"})
    Float allMoney(@Param("typeId") Integer typeId,@Param("tableNames") List<String> tableList,@Param("year")String year);
}
