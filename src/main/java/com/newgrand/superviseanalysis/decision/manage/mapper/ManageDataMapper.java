package com.newgrand.superviseanalysis.decision.manage.mapper;

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
public interface ManageDataMapper {

    @Select({
            "<script> ",
            "<choose>" ,
            // 根据日进行查询
            " <when test='map.time==\"day\"'>" ,
            "SELECT convert(varchar, create_time, 111) dynamicColumn FROM ${map.tableName} WHERE is_through = 0 " ,
            "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND create_time &gt;=  #{map.startTime} </when>",
            "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND create_time &lt;=  #{map.endTime} </when>",
            " GROUP BY convert(varchar, create_time, 111)",
            "</when>",
            // 根据月进行查询
            " <when test='map.time==\"month\"'>" ,
            "SELECT cast(month(create_time ) as VARCHAR )+'月' dynamicColumn FROM ${map.tableName} WHERE is_through = 0 " ,
            "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND create_time &gt;=  #{map.startTime} </when>",
            "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND create_time &lt;=  #{map.endTime} </when>",
            " GROUP BY month(create_time)",
            "</when>",
            // 根据季度进行查询
            " <when test='map.time==\"season\"'>" ,
            "SELECT dynamicColumn FROM (select ",
            "               CASE when MONTH ( create_time ) between 1 and 3 then '第一季度' " ,
            "                when MONTH ( create_time ) between 4 and 6 then '第二季度' " ,
            "                when MONTH ( create_time ) between 7 and 9 then '第三季度' " ,
            "                else '第四季度' end dynamicColumn",
            " FROM ${map.tableName} WHERE is_through = 0 " ,
            "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND create_time &gt;=  #{map.startTime} </when>",
            "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND create_time &lt;=  #{map.endTime} </when>",
            "  ) sea GROUP BY dynamicColumn ORDER BY (CASE dynamicColumn when '第一季度' then 1  when '第二季度' then 2  when '第三季度' then 3 ELSE 4 END)",
            "</when>",
            // 根据年进行查询
            " <when test='map.time==\"year\"'>" ,
            "SELECT cast(year(create_time ) as VARCHAR ) dynamicColumn FROM ${map.tableName} WHERE is_through = 0 " ,
            "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND create_time &gt;=  #{map.startTime} </when>",
            "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND create_time &lt;=  #{map.endTime} </when>",
            " GROUP BY year(create_time)",
            "</when>",
            "</choose>",

            "</script> ",

    })
    List<Map<String, Object>> dynamicColumn(@Param("map") Map<String, Object> map);

    @Select({
            "<script> ",
            "<choose>" ,
            // 根据日进行查询
            " <when test='map.time==\"day\"'>" ,
            "SELECT " ,
                    "ppt.tree_id," ,
                    "case when sum(detail_value)  is null then 0 else round(sum(detail_value),2) end " ,
                    "money " ,
                    "FROM " ,
                    "pm_project_tree ppt " ,
                    "LEFT JOIN ${map.tableName} ppd ON ppt.tree_id = ppd.tree_id" ,
                    "WHERE" ,
                    "ppt.is_delete =0 " ,
                    " <when test='map.parentId!=null and map.parentId !=\"\"'> AND ppt.parent_id = #{map.parentId} </when>  " ,
                    "AND ppt.project_id = ${map.projectId} " ,
                    "AND ( ppt.parent_id != 0 )" ,
                    "AND is_through = 0" ,
                    "AND convert(varchar, ppd.create_time, 111) = #{map.column}" ,
                    "GROUP BY ppt.tree_id",
            "</when>",
            // 根据月进行查询
            " <when test='map.time==\"month\"'>" ,
                "SELECT " ,
                    "ppt.tree_id," ,
                    "case when sum(detail_value)  is null then 0 else round(sum(detail_value),2) end " ,
                    "money " ,
                    "FROM " ,
                    "pm_project_tree ppt " ,
                    "LEFT JOIN ${map.tableName} ppd ON ppt.tree_id = ppd.tree_id" ,
                    "WHERE" ,
                    "ppt.is_delete =0 " ,
            " <when test='map.parentId!=null and map.parentId !=\"\"'> AND ppt.parent_id = #{map.parentId} </when>  " ,
                    "AND ppt.project_id = ${map.projectId} " ,
                    "AND ( ppt.parent_id != 0 )" ,
                    "AND is_through = 0" ,
            "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
            "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                    "AND cast(month(ppd.create_time ) as VARCHAR )+'月' = #{map.column}" ,
                    "GROUP BY ppt.tree_id",
            "</when>",
            // 根据季度进行查询
            " <when test='map.time==\"season\"'>" ,
                "SELECT " ,
                "ppt.tree_id," ,
                "case when sum(detail_value)  is null then 0 else round(sum(detail_value),2) end " ,
                "money " ,
                "FROM " ,
                "pm_project_tree ppt " ,
                "LEFT JOIN ${map.tableName} ppd ON ppt.tree_id = ppd.tree_id" ,
                "WHERE" ,
                "ppt.is_delete =0 " ,
            " <when test='map.parentId!=null and map.parentId !=\"\"'> AND ppt.parent_id = #{map.parentId} </when>  " ,
                "AND ppt.project_id = ${map.projectId} " ,
                "AND ( ppt.parent_id != 0 )" ,
                "AND is_through = 0" ,
                "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
                "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
                "AND month(ppd.create_time ) in ${map.season}" ,
                "GROUP BY ppt.tree_id",
            "</when>",
            // 根据年进行查询
            " <when test='map.time==\"year\"'>" ,
            "SELECT " ,
            "ppt.tree_id," ,
            "case when sum(detail_value)  is null then 0 else round(sum(detail_value),2) end " ,
            "money " ,
            "FROM " ,
            "pm_project_tree ppt " ,
            "LEFT JOIN ${map.tableName} ppd ON ppt.tree_id = ppd.tree_id" ,
            "WHERE" ,
            "ppt.is_delete =0 " ,
            " <when test='map.parentId!=null and map.parentId !=\"\"'> AND ppt.parent_id = #{map.parentId} </when>  " ,
            "AND ppt.project_id = ${map.projectId} " ,
            "AND ( ppt.parent_id != 0 )" ,
            "AND is_through = 0" ,
            "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
            "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
            "AND year(ppd.create_time) = #{map.column}" ,
            "GROUP BY ppt.tree_id",
            "</when>",
            "</choose>",

            "</script> ",
    })
    List<Map<String, Object>> dynamicColumnData(@Param("map")  Map<String, Object> map);

    @Select({
            "<script> ",
            "<choose>" ,
            // 根据日进行查询
            " <when test='map.time==\"day\"'>" ,
//            "SELECT " ,
//            "dict_name name,sum([value]) money " ,
//            "FROM " ,
//            "pm_project_purpose ppp " ,
//            "LEFT JOIN ${map.tableName} ppd ON ppd.detail_id = ppp.detail_id " ,
//            "LEFT JOIN pm_project_tree ppt ON ppt.tree_id = ppd.tree_id " ,
//            "LEFT JOIN sys_dict sd ON sd.dict_id = ppp.dict_id ",
//            "WHERE" ,
//            "ppt.is_delete =0 AND sd.is_delete = 0 " ,
//            "AND is_through = 0" ,
//            "AND convert(varchar, ppd.create_time, 111) = #{map.column}" ,
//            "GROUP BY dict_name",
            "SELECT\n" ,
                    "\ttest.name,\n" ,
                    "CASE\n" ,
                    "\t\n" ,
                    "\tWHEN test.money IS NULL THEN\n" ,
                    "\t0 ELSE test.money \n" ,
                    "END money\n" ,
                    "FROM\n" ,
                    "\t(\n" ,
                    "SELECT\n" ,
                    "dict_name name,\n" ,
                    "(\n" ,
                    "SELECT\n" ,
                    "Round( SUM( [VALUE] ), 2 ) money \n" ,
                    "FROM\n" ,
                    "\tpm_project_purpose ppp2\n" ,
                    "\tLEFT JOIN ${map.tableName} ppd ON ppd.detail_id = ppp2.detail_id\n" ,
                    "\tLEFT JOIN pm_project_tree ppt ON ppt.tree_id = ppd.tree_id\n" ,
                    "\tLEFT JOIN sys_dict sd ON sd.dict_id = ppp2.dict_id \n" ,
                    "WHERE\n" ,
                    "\tppt.is_delete = 0 \n" ,
                    "\tAND sd.is_delete = 0 \n" ,
                    "\tAND is_through = 0 \n" ,
                    "\tAND ppp2.dict_id = ppp.dict_id \n" ,
                    "\tAND CONVERT ( VARCHAR, ppd.create_time, 111 ) =  #{map.column} \n" ,
                    "GROUP BY\n" ,
                    "\tppp2.dict_id \n" ,
                    "\t) money \n" ,
                    "FROM\n" ,
                    "\tsys_dict sd\n" ,
                    "\tLEFT JOIN pm_project_purpose ppp ON sd.dict_id = ppp.dict_id\n" ,
                    "\tLEFT JOIN ${map.tableName} ppd ON ppd.detail_id = ppp.detail_id \n" ,
                    "WHERE\n" ,
                    "\tsd.is_delete = 0 \n" ,
                    "\tAND is_through = 0 \n" ,
                    "GROUP BY\n" ,
                    "\tdict_name,\n" ,
                    "\tppp.dict_id \n" ,
                    ") test",
            "</when>",
            // 根据月进行查询
            " <when test='map.time==\"month\"'>" ,

            "SELECT\n" ,
            "\ttest.name,\n" ,
            "CASE\n" ,
            "\t\n" ,
            "\tWHEN test.money IS NULL THEN\n" ,
            "\t0 ELSE test.money \n" ,
            "END money\n" ,
            "FROM\n" ,
            "\t(\n" ,
            "SELECT\n" ,
            "dict_name name,\n" ,
            "(\n" ,
            "SELECT\n" ,
            "Round( SUM( [VALUE] ), 2 ) money \n" ,
            "FROM\n" ,
            "\tpm_project_purpose ppp2\n" ,
            "\tLEFT JOIN ${map.tableName} ppd ON ppd.detail_id = ppp2.detail_id\n" ,
            "\tLEFT JOIN pm_project_tree ppt ON ppt.tree_id = ppd.tree_id\n" ,
            "\tLEFT JOIN sys_dict sd ON sd.dict_id = ppp2.dict_id \n" ,
            "WHERE\n" ,
            "\tppt.is_delete = 0 \n" ,
            "\tAND sd.is_delete = 0 \n" ,
            "\tAND is_through = 0 \n" ,
            "\tAND ppp2.dict_id = ppp.dict_id \n" ,
            "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
            "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
            "AND cast(month(ppd.create_time ) as VARCHAR )+'月' = #{map.column}" ,
            "GROUP BY\n" ,
            "\tppp2.dict_id \n" ,
            "\t) money \n" ,
            "FROM\n" ,
            "\tsys_dict sd\n" ,
            "\tLEFT JOIN pm_project_purpose ppp ON sd.dict_id = ppp.dict_id\n" ,
            "\tLEFT JOIN ${map.tableName} ppd ON ppd.detail_id = ppp.detail_id \n" ,
            "WHERE\n" ,
            "\tsd.is_delete = 0 \n" ,
            "\tAND is_through = 0 \n" ,
            "GROUP BY\n" ,
            "\tdict_name,\n" ,
            "\tppp.dict_id \n" ,
            ") test",
            "</when>",

            // 根据季度进行查询
            " <when test='map.time==\"season\"'>" ,
            "SELECT\n" ,
            "\ttest.name,\n" ,
            "CASE\n" ,
            "\t\n" ,
            "\tWHEN test.money IS NULL THEN\n" ,
            "\t0 ELSE test.money \n" ,
            "END money\n" ,
            "FROM\n" ,
            "\t(\n" ,
            "SELECT\n" ,
            "dict_name name,\n" ,
            "(\n" ,
            "SELECT\n" ,
            "Round( SUM( [VALUE] ), 2 ) money \n" ,
            "FROM\n" ,
            "\tpm_project_purpose ppp2\n" ,
            "\tLEFT JOIN ${map.tableName} ppd ON ppd.detail_id = ppp2.detail_id\n" ,
            "\tLEFT JOIN pm_project_tree ppt ON ppt.tree_id = ppd.tree_id\n" ,
            "\tLEFT JOIN sys_dict sd ON sd.dict_id = ppp2.dict_id \n" ,
            "WHERE\n" ,
            "\tppt.is_delete = 0 \n" ,
            "\tAND sd.is_delete = 0 \n" ,
            "\tAND is_through = 0 \n" ,
            "\tAND ppp2.dict_id = ppp.dict_id \n" ,
            "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
            "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
            "AND month(ppd.create_time ) in ${map.season}" ,
            "GROUP BY\n" ,
            "\tppp2.dict_id \n" ,
            "\t) money \n" ,
            "FROM\n" ,
            "\tsys_dict sd\n" ,
            "\tLEFT JOIN pm_project_purpose ppp ON sd.dict_id = ppp.dict_id\n" ,
            "\tLEFT JOIN ${map.tableName} ppd ON ppd.detail_id = ppp.detail_id \n" ,
            "WHERE\n" ,
            "\tsd.is_delete = 0 \n" ,
            "\tAND is_through = 0 \n" ,
            "GROUP BY\n" ,
            "\tdict_name,\n" ,
            "\tppp.dict_id \n" ,
            ") test",
            "</when>",
            // 根据年进行查询
            " <when test='map.time==\"year\"'>" ,
            "SELECT\n" ,
            "\ttest.name,\n" ,
            "CASE\n" ,
            "\t\n" ,
            "\tWHEN test.money IS NULL THEN\n" ,
            "\t0 ELSE test.money \n" ,
            "END money\n" ,
            "FROM\n" ,
            "\t(\n" ,
            "SELECT\n" ,
            "dict_name name,\n" ,
            "(\n" ,
            "SELECT\n" ,
            "Round( SUM( [VALUE] ), 2 ) money \n" ,
            "FROM\n" ,
            "\tpm_project_purpose ppp2\n" ,
            "\tLEFT JOIN ${map.tableName} ppd ON ppd.detail_id = ppp2.detail_id\n" ,
            "\tLEFT JOIN pm_project_tree ppt ON ppt.tree_id = ppd.tree_id\n" ,
            "\tLEFT JOIN sys_dict sd ON sd.dict_id = ppp2.dict_id \n" ,
            "WHERE\n" ,
            "\tppt.is_delete = 0 \n" ,
            "\tAND sd.is_delete = 0 \n" ,
            "\tAND is_through = 0 \n" ,
            "\tAND ppp2.dict_id = ppp.dict_id \n" ,
            "  <when test='map.startTime!=null and map.startTime !=\"\"'> AND ppd.create_time &gt;=  #{map.startTime} </when>",
            "  <when test='map.endTime!=null and map.endTime!=\"\"'> AND ppd.create_time &lt;=  #{map.endTime} </when>",
            "AND YEAR ( ppd.create_time ) =#{map.column}" ,
            "GROUP BY\n" ,
            "\tppp2.dict_id \n" ,
            "\t) money \n" ,
            "FROM\n" ,
            "\tsys_dict sd\n" ,
            "\tLEFT JOIN pm_project_purpose ppp ON sd.dict_id = ppp.dict_id\n" ,
            "\tLEFT JOIN ${map.tableName} ppd ON ppd.detail_id = ppp.detail_id \n" ,
            "WHERE\n" ,
            "\tsd.is_delete = 0 \n" ,
            "\tAND is_through = 0 \n" ,
            "GROUP BY\n" ,
            "\tdict_name,\n" ,
            "\tppp.dict_id \n" ,
            ") test",
            "</when>",
            "</choose>",

            "</script> ",
    })
    List<Map<String, Object>> dynamicColumnDetailData(@Param("map")Map<String, Object> map);
}
