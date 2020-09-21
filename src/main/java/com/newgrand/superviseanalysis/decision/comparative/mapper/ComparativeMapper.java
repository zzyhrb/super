package com.newgrand.superviseanalysis.decision.comparative.mapper;

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
public interface ComparativeMapper {

    @Select({
            "<script> ",
            "<choose>" ,
                // 根据日进行查询
                " <when test='map.time==\"day\"'>" ,
                    "SELECT convert(varchar, create_time, 111) FROM ${map.tableName} WHERE is_through = 0 " ,
                    "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND create_time &gt;=  #{map.startTime} </when>",
                    "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND create_time &lt;=  #{map.endTime} </when>",
                            " GROUP BY convert(varchar, create_time, 111)",
                "</when>",
            // 根据月进行查询
                " <when test='map.time==\"month\"'>" ,
                    "SELECT cast(month(create_time ) as VARCHAR )+'月' FROM ${map.tableName} WHERE is_through = 0 " ,
                    "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND create_time &gt;=  #{map.startTime} </when>",
                    "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND create_time &lt;=  #{map.endTime} </when>",
                        " GROUP BY month(create_time)",
                "</when>",
            // 根据季度进行查询
            " <when test='map.time==\"season\"'>" ,
                "SELECT create_time FROM (select ",
            "               CASE when MONTH ( create_time ) between 1 and 3 then '第一季度' " ,
            "                when MONTH ( create_time ) between 4 and 6 then '第二季度' " ,
            "                when MONTH ( create_time ) between 7 and 9 then '第三季度' " ,
            "                else '第四季度' end create_time",
                " FROM ${map.tableName} WHERE is_through = 0 " ,
                "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND create_time &gt;=  #{map.startTime} </when>",
                "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND create_time &lt;=  #{map.endTime} </when>",
                " ) sea GROUP BY create_time ORDER BY (CASE create_time when '第一季度' then 1 when '第二季度' then 2 when '第三季度' then 3 ELSE 4 END)",
            "</when>",
            // 根据年进行查询
                " <when test='map.time==\"year\"'>" ,
                    "SELECT cast(year(create_time ) as VARCHAR ) FROM ${map.tableName} WHERE is_through = 0 " ,
                    "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND create_time &gt;=  #{map.startTime} </when>",
                    "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND create_time &lt;=  #{map.endTime} </when>",
                        " GROUP BY year(create_time)",
                "</when>",
            "</choose>",

            "</script> ",

    })
    List<String> comparativeLegend(@Param("map") Map<String, Object> map);

    @Select({
            "<script>",
           "SELECT ppt.tree_name FROM pm_project_tree ppt  LEFT JOIN ${map.tableName} ppd ON ppd.tree_id = ppt.tree_id WHERE is_through = 0 " ,
                "<choose>" ,
                    " <when test='map.parentId!=null and map.parentId !=\"\"'> AND parent_id = #{map.parentId} </when>",
                    "<otherwise> AND  1 = 2 </otherwise>",
                "</choose>",
                   "  GROUP BY ppt.tree_id,ppt.tree_name ORDER BY ppt.tree_id ${order} ",
            "</script>"
    })
    List<String> comparativeXAxis(@Param("map") Map<String, Object> map, @Param("order")  String order);

    @Select({
            "<script> ",
            "<choose>" ,
                // 根据日进行查询
                " <when test='map.time==\"day\"'>" ,
                    "SELECT case when sum(detail_value) is null then 0 else round(sum(detail_value),2) end " ,
                            " FROM pm_project_tree ppt  LEFT JOIN ${map.tableName} ppd ON ppd.tree_id = ppt.tree_id WHERE is_through = 0 " ,
                            "<choose>" ,
                                " <when test='map.parentId!=null and map.parentId !=\"\"'> AND parent_id = #{map.parentId} </when>",
                                "<otherwise> AND  1 = 2 </otherwise>",
                            "</choose>",
                            " and convert(varchar, ppd.create_time, 111) = #{map.legendStr} " ,
                            "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
                            "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                                " GROUP BY ppt.tree_id,ppt.tree_name ORDER BY ppt.tree_id asc ",
                "</when>",
                // 根据月进行查询
                " <when test='map.time==\"month\"'>" ,
                    "SELECT case when sum(detail_value) is null then 0 else round(sum(detail_value),2) end " ,
                    " FROM pm_project_tree ppt  LEFT JOIN ${map.tableName} ppd ON ppd.tree_id = ppt.tree_id WHERE is_through = 0 " ,
                    "<choose>" ,
                        " <when test='map.parentId!=null and map.parentId !=\"\"'> AND parent_id = #{map.parentId} </when>",
                        "<otherwise> AND  1 = 2 </otherwise>",
                    "</choose>",
                    " and cast(month(ppd.create_time ) as VARCHAR )+'月' = #{map.legendStr} " ,
                    "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
                    "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                        " GROUP BY ppt.tree_id,ppt.tree_name ORDER BY ppt.tree_id asc ",
                "</when>",
                // 根据季度进行查询
                " <when test='map.time==\"season\"'>" ,
                    "SELECT case when sum(detail_value) is null then 0 else round(sum(detail_value),2) end " ,
                    " FROM pm_project_tree ppt  LEFT JOIN ${map.tableName} ppd ON ppd.tree_id = ppt.tree_id WHERE is_through = 0 " ,
                    "<choose>" ,
                        "  <when test='map.parentId!=null and map.parentId !=\"\"'> AND parent_id = #{map.parentId} </when>",
                        " <otherwise> AND  1 = 2 </otherwise>",
                    "</choose>",
                    " and month(ppd.create_time ) in ${map.day} " ,
                    "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
                    "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                        " GROUP BY ppt.tree_id,ppt.tree_name ORDER BY ppt.tree_id asc ",
                "</when>",

                // 根据年进行查询
                " <when test='map.time==\"year\"'>" ,
                    "SELECT case when sum(detail_value) is null then 0 else round(sum(detail_value),2) end " ,
                    " FROM pm_project_tree ppt  LEFT JOIN ${map.tableName} ppd ON ppd.tree_id = ppt.tree_id WHERE is_through = 0 " ,
                    "<choose>" ,
                        " <when test='map.parentId!=null and map.parentId !=\"\"'> AND parent_id = #{map.parentId} </when>",
                        " <otherwise> AND  1 = 2 </otherwise>",
                    "</choose>",
                    " and cast(year(ppd.create_time ) as VARCHAR ) = #{map.legendStr} " ,
                    " <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
                    " <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                        " GROUP BY ppt.tree_id,ppt.tree_name ORDER BY ppt.tree_id asc ",
                "</when>",

            "</choose>",

            "</script> ",

    })
    List<Double> comparativeSeries(@Param("map") Map<String, Object> map);

    @Select({"<script> ",
                "SELECT ppt.tree_name name,sum(ppd.detail_value) value FROM pm_project_tree ppt  LEFT JOIN ${map.tableName} ppd ON ppd.tree_id = ppt.tree_id WHERE is_through = 0 " ,
                "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
                "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                "<choose>" ,
                    " <when test='map.parentId!=null and map.parentId !=\"\"'> AND parent_id = #{map.parentId} </when>",
                    " <otherwise> AND  1 = 2 </otherwise>",
                "</choose>",
                " GROUP BY ppt.tree_id,ppt.tree_name ORDER BY ppt.tree_id ${order}",
            "</script> ",
    })
    List<Map<String, Object>> proportionSeries(@Param("map") Map<String, Object> map, @Param("order")  String order);


    @Select({"<script> ",
            "<choose>" ,
            // 根据日进行查询
            " <when test='map.time==\"day\"'>" ,
                "SELECT CONVERT ( VARCHAR, ppd.create_time, 111 ) " ,
                " FROM pm_project_tree ppt  LEFT JOIN ${map.tableName} ppd ON ppd.tree_id = ppt.tree_id  WHERE is_through = 0 " ,
                "<choose>" ,
                " <when test='map.parentId!=null and map.parentId !=\"\"'> AND ppd.tree_id = #{map.parentId} </when>",
                "<otherwise> AND ppd.tree_id = '01' </otherwise>",
                "</choose>",
                "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
                "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                " GROUP BY CONVERT ( VARCHAR, ppd.create_time, 111 ) ",
                "</when>",
            // 根据月进行查询
            " <when test='map.time==\"month\"'>" ,
                "SELECT cast(month(ppd.create_time ) as VARCHAR )+'月'  " ,
                "FROM pm_project_tree ppt  LEFT JOIN ${map.tableName} ppd ON ppd.tree_id = ppt.tree_id  WHERE is_through = 0 " ,
                "<choose>" ,
                " <when test='map.parentId!=null and map.parentId !=\"\"'> AND ppd.tree_id = #{map.parentId} </when>",
                "<otherwise> AND ppd.tree_id = '01' </otherwise>",
                "</choose>",
                "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
                "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                " GROUP BY month(ppd.create_time) ",
            "</when>",
            // 根据季度进行查询
            " <when test='map.time==\"season\"'>" ,
                "select season from ( " +
                        " SELECT" +
                        " CASE when MONTH ( ppd.create_time ) between 1 and 3 then '第一季度'" +
                        "                when MONTH ( ppd.create_time ) between 4 and 6 then '第二季度' " ,
                        "                when MONTH ( ppd.create_time ) between 7 and 9 then '第三季度'" ,
                        "                else '第四季度' end season FROM pm_project_tree ppt  LEFT JOIN ${map.tableName} ppd ON ppd.tree_id = ppt.tree_id  WHERE is_through = 0",
                "<choose>" ,
                " <when test='map.parentId!=null and map.parentId !=\"\"'> AND ppd.tree_id = #{map.parentId} </when>",
                "<otherwise> AND ppd.tree_id = '01' </otherwise>",
                "</choose>",
                "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
                "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                " ) sea GROUP BY season ORDER BY (CASE season when '第一季度' then 1 " ,
                        "                when '第二季度' then 2 " ,
                        "                when '第三季度' then 3 " ,
                        " ELSE 4 " ,
                        "                END)",
            "</when>",

            // 根据年进行查询
                " <when test='map.time==\"year\"'>" ,
                "SELECT year(ppd.create_time) " ,
                " FROM pm_project_tree ppt  LEFT JOIN ${map.tableName} ppd ON ppd.tree_id = ppt.tree_id   WHERE is_through = 0 " ,
                "<choose>" ,
                " <when test='map.parentId!=null and map.parentId !=\"\"'> AND ppd.tree_id = #{map.parentId} </when>",
                "<otherwise> AND ppd.tree_id = '01' </otherwise>",
                "</choose>",
                "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
                "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                " GROUP BY year(ppd.create_time) ",
            "</when>",

            "</choose>",

            "</script> ",
    })
    List<String> sameProportionXAxis(@Param("map") Map<String, Object> map);


    @Select({"<script> ",
            "<choose>" ,
            // 根据日进行查询
            " <when test='map.time==\"day\"'>" ,
                "SELECT CASE WHEN SUM(detail_value) IS NULL then 0 else ROUND(SUM(detail_value) ,2) END " ,
                " FROM pm_project_tree ppt  LEFT JOIN ${map.tableName} ppd ON ppd.tree_id = ppt.tree_id   WHERE is_through = 0 " ,
                "<choose>" ,
                " <when test='map.parentId!=null and map.parentId !=\"\"'> AND ppd.tree_id = #{map.parentId} </when>",
                "<otherwise> AND ppd.tree_id = '01' </otherwise>",
                "</choose>",
                "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
                "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                " GROUP BY CONVERT ( VARCHAR, ppd.create_time, 111 ) ",
            "</when>",
            // 根据月进行查询
            " <when test='map.time==\"month\"'>" ,
                "SELECT CASE WHEN SUM(detail_value) IS NULL then 0 else ROUND(SUM(detail_value) ,2) END " ,
                " FROM pm_project_tree ppt  LEFT JOIN ${map.tableName} ppd ON ppd.tree_id = ppt.tree_id   WHERE is_through = 0 " ,
                "<choose>" ,
                " <when test='map.parentId!=null and map.parentId !=\"\"'> AND ppd.tree_id = #{map.parentId} </when>",
                "<otherwise> AND ppd.tree_id = '01' </otherwise>",
                "</choose>",
                "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
                "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                " GROUP BY month(ppd.create_time) ",
            "</when>",
            // 根据季度进行查询
            " <when test='map.time==\"season\"'>" ,
                "select CASE WHEN SUM(detail_value) IS NULL then 0 else ROUND(SUM(detail_value) ,2) END from ( " +
                        " SELECT" +
                        " CASE when MONTH ( ppd.create_time ) between 1 and 3 then '第一季度'" +
                        "                when MONTH ( ppd.create_time ) between 4 and 6 then '第二季度' " ,
                "                when MONTH ( ppd.create_time ) between 7 and 9 then '第三季度'" ,
                "                else '第四季度' end season,detail_value FROM pm_project_tree ppt  LEFT JOIN ${map.tableName} ppd ON ppd.tree_id = ppt.tree_id  WHERE is_through = 0",
                "<choose>" ,
                " <when test='map.parentId!=null and map.parentId !=\"\"'> AND ppd.tree_id = #{map.parentId} </when>",
                "<otherwise> AND ppd.tree_id = '01' </otherwise>",
                "</choose>",
                "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
                "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                " ) sea GROUP BY season ORDER BY (CASE season when '第一季度' then 1 " ,
                "                when '第二季度' then 2 " ,
                "                when '第三季度' then 3 " ,
                " ELSE 4 " ,
                "                END)",
            "</when>",

            // 根据年进行查询
            " <when test='map.time==\"year\"'>" ,
                "SELECT CASE WHEN SUM(detail_value) IS NULL then 0 else ROUND(SUM(detail_value) ,2) END " ,
                " FROM pm_project_tree ppt  LEFT JOIN ${map.tableName} ppd ON ppd.tree_id = ppt.tree_id  WHERE is_through = 0 " ,
                "<choose>" ,
                " <when test='map.parentId!=null and map.parentId !=\"\"'> AND ppd.tree_id = #{map.parentId} </when>",
                "<otherwise> AND ppd.tree_id = '01' </otherwise>",
                "</choose>",
                "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
                "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                " GROUP BY year(ppd.create_time) ",
            "</when>",

            "</choose>",

            "</script> ",
    })
    List<Double> sameProportionSeries(@Param("map") Map<String, Object> map);
}
