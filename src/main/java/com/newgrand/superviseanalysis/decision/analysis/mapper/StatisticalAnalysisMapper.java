package com.newgrand.superviseanalysis.decision.analysis.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-29
 */
public interface StatisticalAnalysisMapper {
    @Select({"<script>",
            " SELECT CASE when ROUND(sum(detail_value),2) is null then 0 else ROUND(sum(detail_value),2) end totalMoney FROM ( ",
            " <foreach collection='tableNames' item='tableName' open='' separator='UNION ALL' close=''>",
            " SELECT detail_value FROM ${tableName} ppd LEFT JOIN pm_project_tree  ppt ON ppt.tree_id = ppd.tree_id WHERE ppt.is_delete = 0 AND is_through = 0 AND type_id = #{typeId} and len(ppt.tree_id) = 4 ",
            // 天
            " <when test='map.economicsType==\"day\" or map.economicsType == null'> AND ppd.create_time &gt;= #{map.day} AND ppd.create_time &lt;= #{map.day} </when>",
            // 月
            " <when test='map.economicsType==\"month\"'> AND MONTH(ppd.create_time) = LEFT ( #{map.day} , len(#{map.day})-1  ) ",
            " <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
            " <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
            "</when>",

            // 季度
            " <when test='map.economicsType==\"season\"'>" +
                    " <when test='map.day!=null and map.day !=\"\"'> AND month(ppd.create_time) in ${map.day} </when>",
            " <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
            " <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
            "</when>",

            // 年
            " <when test='map.economicsType==\"year\"'>",
            "  AND year(ppd.create_time) = ${map.day} ",
            " <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
            " <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
            "</when>",

            " </foreach> ) totalMoney ",
            "</script>"})
    Double totalMoneyByType(@Param("typeId") Integer typeId, @Param("tableNames") List<String> tableNames, @Param("map") Map<String, Object> map);

    @Select({"<script>",
            " SELECT DISTINCT(convert(varchar, create_time, 111)) FROM ( ",
            " <foreach collection='tableNames' item='tableName' open='' separator='UNION ALL' close=''>",
            " SELECT ppd.create_time FROM ${tableName} ppd LEFT JOIN pm_project_tree  ppt ON ppt.tree_id = ppd.tree_id WHERE ppt.is_delete = 0 AND is_through = 0 ",
            " <choose>",
            "<when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
            "<otherwise> AND ppd.create_time &gt;= DATEADD(DAY,-7,convert(varchar, getdate(), 111)) </otherwise>",
            "</choose> ",
            " <choose>",
            " <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
            "<otherwise> AND ppd.create_time &lt;= getdate()</otherwise>",
            "</choose> ",
            " </foreach> ) totalMoney ",
            "</script>"})
    List<String> XAxisByDay(@Param("tableNames") List<String> tableNames, @Param("map") Map<String, Object> map);


    @Select({"<script>",
            " SELECT month FROM ( ",
            " <foreach collection='tableNames' item='tableName' open='' separator='UNION' close=''>",
            " SELECT cast(month(ppd.create_time ) as VARCHAR )+'月' month FROM ${tableName} ppd LEFT JOIN pm_project_tree  ppt ON ppt.tree_id = ppd.tree_id WHERE ppt.is_delete = 0 AND is_through = 0",
            " <choose>",
            "<when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
            "<otherwise> AND ppd.create_time &gt;= cast(year(getdate())as VARCHAR) + '/01/01 00:00:01'  </otherwise>",
            "</choose> ",
            " <choose>",
            " <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
            "<otherwise> AND ppd.create_time &lt;= getdate()</otherwise>",
            "</choose> ",
            " </foreach> GROUP BY month(ppd.create_time)) totalMoney order by MONTH asc ",

            "</script>"})
    List<String> XAxisByMonth(@Param("tableNames") List<String> tableNames, @Param("map") Map<String, Object> map);

    @Select({"<script>",
            " SELECT season FROM ( ",
            " <foreach collection='tableNames' item='tableName' open='' separator='UNION' close=''>",
            " SELECT ",
            "CASE when MONTH ( ppd.create_time ) between 1 and 3 then '第一季度'",
            "when MONTH ( ppd.create_time ) between 4 and 6 then '第二季度'",
            "when MONTH ( ppd.create_time ) between 7 and 9 then '第三季度'",
            "else '第四季度' end season",
            " FROM ${tableName} ppd LEFT JOIN pm_project_tree  ppt ON ppt.tree_id = ppd.tree_id WHERE ppt.is_delete = 0 AND is_through = 0 ",
            " <choose>",
            "<when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
            "<otherwise> AND ppd.create_time &gt;= cast(year(getdate())as VARCHAR) + '/01/01 00:00:01'  </otherwise>",
            "</choose> ",
            " <choose>",
            " <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
            "<otherwise> AND ppd.create_time &lt;= getdate()</otherwise>",
            "</choose> ",
            " </foreach> ) totalMoney GROUP BY season ORDER BY (CASE season when '第一季度' then 1 when '第二季度' then 2 when '第三季度' then 3 ELSE 4 END)",
            "</script>"})
    List<String> XAxisBySeason(@Param("tableNames") List<String> tableNames, @Param("map") Map<String, Object> map);


    @Select({"<script>",
            " SELECT name,val FROM (",
            " SELECT pp.project_name name,case when  val is null then 0 ELSE round(val,2)  end val,row_number() over( order by val desc) ROW FROM pm_project pp LEFT JOIN  ( ",
            " <foreach collection='tableNames' item='tableName' open='' separator='UNION ALL' close=''>",
            " SELECT sum(ppd.detail_value) val,max(pp.project_name) name,max( pp.project_id ) id FROM ${tableName} ppd LEFT JOIN pm_project_tree ppt on ppt.tree_id = ppd.tree_id ",
            " LEFT JOIN pm_project pp ON pp.project_id = ppt.project_id",
            " WHERE len(ppt.tree_id) = 4 AND is_through = 0 AND ppt.is_delete = 0 AND ppt.type_id= 3 and pp.is_delete = 0",
            " <choose>",
            "<when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
            "<otherwise> AND ppd.create_time &gt;= DATEADD(DAY,-7,getdate()) </otherwise>",
            "</choose> ",
            " <choose>",
            " <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
            "<otherwise> AND ppd.create_time &lt;= getdate()</otherwise>",
            "</choose> ",
            " </foreach> GROUP BY ppd.project_id) total on total.id = pp.project_id WHERE  pp.is_delete = 0 AND total.id = pp.project_id ) project WHERE ROW &lt;=5 ",
            "</script>"})
    List<Map<String, Object>> queryTop5Project(@Param("tableNames") List<String> tableNames, @Param("map") Map<String, Object> map);

    @Select({"<script>",
            " SELECT name,val FROM (",
            " SELECT case WHEN sum(val) IS NULL then 0 else round(sum(val),2) end val,max(organ_name) name,row_number() over( order by sum(val) desc) ROW FROM (SELECT so.organ_name,so.organ_id,val FROM sys_organ so ",
            " LEFT JOIN pm_project pp ON pp.organ_id = so.organ_id",
            " LEFT JOIN (",
            " <foreach collection='tableNames' item='tableName' open='' separator='UNION ALL' close=''>",
            " SELECT sum(ppd.detail_value) val ,max( ppd.project_id ) id FROM ${tableName} ppd LEFT JOIN",
            " pm_project_tree ppt on ppt.tree_id = ppd.tree_id ",
            " WHERE len(ppt.tree_id) = 4 AND is_through = 0 AND ppt.is_delete = 0 AND ppt.type_id= 3 ",
            " <choose>",
            "<when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
            "<otherwise> AND ppd.create_time &gt;= DATEADD(DAY,-7,getdate()) </otherwise>",
            "</choose> ",
            " <choose>",
            " <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
            "<otherwise> AND ppd.create_time &lt;= getdate()</otherwise>",
            "</choose> ",
            " </foreach> ) total ON total.id = pp.project_id WHERE so.is_delete = 0 AND so.organ_id like '${map.organId}%' and len(so.organ_id) > 4) total   GROUP BY organ_id )organ WHERE ROW &lt;=5 ",
            "</script>"})
    List<Map<String, Object>> queryTop5Organ(@Param("tableNames") List<String> tableNames, @Param("map") Map<String, Object> map);

    @Select({"<script>",
            "SELECT case WHEN sum(detail_value) IS NULL then 0 else round(sum(detail_value),2) end val from (",
            " <foreach collection='map.tableNames' item='tableName' open='' separator='UNION ALL' close=''>",
            " SELECT sum(detail_value) detail_value FROM ${tableName} ppd LEFT JOIN pm_project_tree ppt ON ppd.tree_id = ppt.tree_id  WHERE type_id = #{map.typeId}" +
                    " AND is_through = 0 AND len(ppd.tree_id) = 4 AND is_delete = 0 AND ppd.create_time &gt;=  #{map.startTime} AND ppd.end_time &lt;=  #{map.endTime}" +
                    " GROUP BY ppd.tree_id",
            "</foreach>) money",
            "</script>"

    })
    Double childrenOrganMoney(@Param("map") Map<String, Object> map);

    @Select({
            "SELECT table_name FROM pm_project_table ppt LEFT JOIN pm_project pp on pp.project_id = ppt.project_id\n" +
                    "LEFT JOIN sys_organ so ON so.organ_id = pp.organ_id " +
                    "WHERE so.organ_name=#{xAxisStr}"
    })
    List<String> tableNameList(@Param("xAxisStr") String xAxisStr);

    @Select({
            "<script>",
            "SELECT \n" +
                    "case WHEN \n" +
                    "sum(money) is null then 0 ELSE Round(sum(money),2) END money  FROM (\n" +
                    " <foreach collection='tableNames' item='tableName' open='' separator='UNION ALL' close=''>",

                    "SELECT sum(detail_value) money FROM ${tableName} ppd LEFT JOIN pm_project_tree ppt \n" +
                    "ON ppd.tree_id = ppt.tree_id\n" +
                    "\n" +
                    "WHERE ppt.type_id = (select type_id FROM pm_project_type  WHERE is_delete = 0 AND type_name = #{map.typeName})\n" +
                    "AND len(ppt.tree_id) = 4 AND is_through = 0 \n" +
                    " <choose>",
                        "<when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
                        "<otherwise> AND ppd.create_time &gt;= DATEADD(DAY,-7,getdate()) </otherwise>",
                    "</choose> ",
                    " <choose>",
                        " <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                        "<otherwise> AND ppd.create_time &lt;= getdate()</otherwise>",
                    "</choose> ",
                    "GROUP BY type_id\n" +
                    "</foreach>) MONEY",
            "</script>"
    })
    Double economicTrendPieMoney(@Param("map") Map<String, Object> map, @Param("tableNames") List<String> tableNames);
}
