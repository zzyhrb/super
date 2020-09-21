package com.newgrand.superviseanalysis.decision.yeareconomics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newgrand.superviseanalysis.decision.yeareconomics.mapper.YearEconomicsMapper;
import com.newgrand.superviseanalysis.decision.yeareconomics.service.IYearEconomicsService;
import com.newgrand.superviseanalysis.system.organ.mapper.OrganMapper;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectType;
import com.newgrand.superviseanalysis.system.project.mapper.ProjectTableMapper;
import com.newgrand.superviseanalysis.system.project.mapper.ProjectTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author tly
 * @version 1.0.0
 * @ClassName ManageDataServiceImpl.java
 * @Description TODO
 * @createTime 2020年04月08日 10:56:00
 */
@Service
public class YearEconomicsServiceImpl implements IYearEconomicsService {
    @Autowired
    private YearEconomicsMapper yearEconomicsMapper;
    @Autowired
    private ProjectTypeMapper projectTypeMapper;
    @Autowired
    private OrganMapper organMapper;
    @Autowired
    private ProjectTableMapper projectTableMapper;

    @Override
    public List<Map<String, Object>> options(List<String> timeLine, String name) {

        // 构建项目类型查询器
        QueryWrapper typeWrapper = new QueryWrapper<>();
        // 查询正在使用
        typeWrapper.eq("is_delete", 0);
        typeWrapper.in("type_id", 1,2,3);
        // 查询项目类型
        List<PmProjectType> typeList = projectTypeMapper.selectList(typeWrapper);

        // 构建组织查询器
        QueryWrapper organWrapper = new QueryWrapper<>();
        // 查询组织ID
        organWrapper.select("organ_id");
        // 查询条件
        organWrapper.eq("is_delete", 0);
        organWrapper.gt("len(organ_id)", 4);
        // 查询组织结果
        List<String> organList = organMapper.selectObjs(organWrapper);
        // 创建返回的options
        List<Map<String, Object>> options = new ArrayList<>();

        // 循环年份
        for (String timeLineStr : timeLine) {
            Map<String, Object> map = new HashMap<>();
            Map<String, Object> titleMap = new HashMap<>();
            // 放入text属性
            titleMap.put("text", timeLineStr + name);
            // 放入title属性
            map.put("title", titleMap);

            // 创建series
            List<Map<String, Object>> seriesList = new ArrayList<>();

            // 创建饼图容器
            Map<String, Object> dataMapPie = new HashMap<>();
//            List<Map<String, Object>> dataPieList = new ArrayList<>();

            // 循环项目类型
            for (PmProjectType projectType : typeList) {
                Map<String, Object> dataMapPie2 = new HashMap<>();
                // 声明总金额
                double sumMoney = 0;

                Map<String, Object> dataMap = new HashMap<>();
                List<Map<String, Object>> dataList = new ArrayList<>();

                // 循环组织
                for (String organStr : organList) {

                    Map<String, Object> dataListMap = new HashMap<>();
                    List<String> tableList = projectTableMapper.queryTableListByOrganId(organStr);
                    if (tableList == null || tableList.isEmpty() || tableList.get(0) == null || "".equals(tableList.get(0))) {
                        // 放入柱状图值
                        dataListMap.put("value", 0);

                    } else {
                        // 查询金额
                        double money = yearEconomicsMapper.allMoney(projectType.getTypeId(), tableList,timeLineStr);
                        // 计算总金额
                        sumMoney = sumMoney + money;
                        // 放入柱状图值
                        dataListMap.put("value", Double.parseDouble(String.format("%.2f", money)));
                    }
                    dataList.add(dataListMap);
                }

                dataMap.put("data", dataList);
                seriesList.add(dataMap);

                dataMapPie2.put("name", projectType.getTypeName());
                dataMapPie2.put("value", Double.parseDouble(String.format("%.2f", sumMoney)));
//                dataPieList.add(dataMapPie2);

            }
//            dataMapPie.put("data", dataPieList);
//            seriesList.add(dataMapPie);
            map.put("series", seriesList);
            options.add(map);
        }
        return options;
    }

    @Override
    public List<Map<String, Object>> series(String type, List<String> legend) {
        List<Map<String, Object>> series = new ArrayList<>();
//        Map<String, Object> labelMap = new HashMap<String, Object>();
//        labelMap.put("show",true);
//        labelMap.put("position", "inside");
        for (String legendStr : legend) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", legendStr);
            map.put("type", type);
//            map.put("label", labelMap);
            series.add(map);

        }
        return series;
    }

    @Override
    public List<String> allYear(List<String> projectTables) {
        try {
            return yearEconomicsMapper.allYear(projectTables,Collections.emptyMap());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
