package com.newgrand.superviseanalysis.decision.analysis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newgrand.superviseanalysis.decision.analysis.mapper.StatisticalAnalysisMapper;
import com.newgrand.superviseanalysis.decision.analysis.service.IStatisticalAnalysisService;
import com.newgrand.superviseanalysis.decision.yeareconomics.mapper.YearEconomicsMapper;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectType;
import com.newgrand.superviseanalysis.system.project.mapper.ProjectTableMapper;
import com.newgrand.superviseanalysis.system.project.mapper.ProjectTypeMapper;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author tly
 * @version 1.0.0
 * @ClassName StatisticalAnalysisServiceImpl.java
 * @Description TODO
 * @createTime 2020年04月08日 10:56:00
 */
@Service
public class StatisticalAnalysisServiceImpl implements IStatisticalAnalysisService {
    private StatisticalAnalysisMapper statisticalAnalysisMapper;
    private ProjectTypeMapper projectTypeMapper;
    private ProjectTableMapper projectTableMapper;
    private YearEconomicsMapper yearEconomicsMapper;


    @Autowired
    public StatisticalAnalysisServiceImpl(StatisticalAnalysisMapper statisticalAnalysisMapper, ProjectTypeMapper projectTypeMapper, ProjectTableMapper projectTableMapper, YearEconomicsMapper yearEconomicsMapper) {
        this.statisticalAnalysisMapper = statisticalAnalysisMapper;
        this.projectTypeMapper = projectTypeMapper;
        this.projectTableMapper = projectTableMapper;
        this.yearEconomicsMapper = yearEconomicsMapper;
    }

    @Override
    public List<String> economicTrendXAxis(Map<String, Object> map, SysUser user, List<String> tableNames) {
        try {
            List<String> economicTrendXAxis = new ArrayList<>();
            if ("day".equals(map.get("economicsType")) || map.get("economicsType") == null) {
                economicTrendXAxis = statisticalAnalysisMapper.XAxisByDay(tableNames, map);
            } else if ("month".equals(map.get("economicsType"))) {
                economicTrendXAxis = statisticalAnalysisMapper.XAxisByMonth(tableNames, map);
            } else if ("season".equals(map.get("economicsType"))) {
                economicTrendXAxis = statisticalAnalysisMapper.XAxisBySeason(tableNames, map);
            } else {
                economicTrendXAxis = yearEconomicsMapper.allYear(tableNames, map);
            }
            return economicTrendXAxis;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, Object>> economicTrendSeries(Map<String, Object> map, SysUser user, List<String> tableNames, List<String> economicTrendXAxis) {
        List<Map<String, Object>> economicTrendSeries = new ArrayList<>();
        // 构建项目类型查询器
        QueryWrapper typeWrapper = new QueryWrapper<>();
        // 查询正在使用
        typeWrapper.eq("is_delete", 0);
        typeWrapper.in("type_id", 1, 2, 3);
        // 查询项目类型
        List<PmProjectType> typeList = projectTypeMapper.selectList(typeWrapper);

        // 循环项目类型
        for (PmProjectType projectType : typeList) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("name", projectType.getTypeName());
            dataMap.put("type", "line");

            List<Double> dataList = new ArrayList<>();

            for (String xAxis : economicTrendXAxis) {
                map.put("day", xAxis);

                if ("season".equals(map.get("economicsType"))) {
                    if ("第一季度".equals(xAxis)) {
                        map.put("day", "(1,2,3)");
                    } else if ("第二季度".equals(xAxis)) {
                        map.put("day", "(4,5,6)");
                    } else if ("第三季度".equals(xAxis)) {
                        map.put("day", "(7,8,9)");
                    } else {
                        map.put("day", "(10,11,12)");
                    }
                }
                Double totalMoney = statisticalAnalysisMapper.totalMoneyByType(projectType.getTypeId(), tableNames, map);
                dataList.add(totalMoney);
            }
            dataMap.put("data", dataList);
            economicTrendSeries.add(dataMap);
        }
        return economicTrendSeries;
    }

    @Override
    public List<Map<String, Object>> queryTop5(String profitType, SysUser user, Map<String, Object> map) {
        List<String> tableNames = projectTableMapper.selectTableNamesByOrganId(user.getOrganId());
        List<Map<String, Object>> top5;
        if ("organ".equals(profitType)) {
            top5 = statisticalAnalysisMapper.queryTop5Organ(tableNames, map);
        } else {
            top5 = statisticalAnalysisMapper.queryTop5Project(tableNames, map);
        }
        return top5;
    }

    @Override
    public List<String> mainProfitChartLegend(List<Map<String, Object>> top5) {
        List<String> mainProfitChartLegend = new ArrayList<>();
        try {
            for (Map<String, Object> map : top5) {
                mainProfitChartLegend.add((String) map.get("name"));
            }
            return mainProfitChartLegend;
        } catch (Exception e) {
            return mainProfitChartLegend;
        }
    }

    @Override
    public List<Map<String, Object>> mainProfitChartXAxis(List<Map<String, Object>> top5) {
        List<Map<String, Object>> mainProfitChartXAxis = new ArrayList<>();
        try {
            for (Map<String, Object> map : top5) {
                Map<String, Object> mainProfitChartXAxisMap = new HashMap<>();
                mainProfitChartXAxisMap.put("name", map.get("name"));
                mainProfitChartXAxisMap.put("value", map.get("val"));
                mainProfitChartXAxis.add(mainProfitChartXAxisMap);
            }

            return mainProfitChartXAxis;
        } catch (Exception e) {
            return mainProfitChartXAxis;
        }
    }

    @Override
    public List<Map<String, Object>> childrenOrganChartSeries(Map<String, Object> map, List<String> xAxis) {
        List<Map<String, Object>> childrenOrganChartSeries = new ArrayList<>();
        // 构建项目类型查询器
        QueryWrapper typeWrapper = new QueryWrapper<>();
        // 查询正在使用
        typeWrapper.eq("is_delete", 0);
        typeWrapper.in("type_id", 1, 2, 3);
        // 查询项目类型
        List<PmProjectType> typeList = projectTypeMapper.selectList(typeWrapper);
        Map<String, Object> labelMap = new HashMap<String, Object>();
        labelMap.put("show", true);
        labelMap.put("position", "inside");
        // 循环项目类型
        for (PmProjectType projectType : typeList) {
            map.put("typeId", projectType.getTypeId());
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("name", projectType.getTypeName());
            dataMap.put("type", "bar");
            dataMap.put("label", labelMap);

            List<Double> dataList = new ArrayList<>();

            for (String xAxisStr : xAxis) {
                map.put("day", xAxisStr);
                List<String> tableNameList = statisticalAnalysisMapper.tableNameList(xAxisStr);
                map.put("tableNames", tableNameList);
                Double totalMoney = statisticalAnalysisMapper.childrenOrganMoney(map);
                dataList.add(totalMoney);
            }
            dataMap.put("data", dataList);
            childrenOrganChartSeries.add(dataMap);
        }
        return childrenOrganChartSeries;
    }

    @Override
    public List<Map<String, Object>> economicTrendPieSeries(Map<String, Object> map, SysUser user, List<String> tableNames, List<String> legend) {
        List<Map<String, Object>> economicTrendPieSeries = new ArrayList<>();

        for (String legStr : legend) {
            Map<String, Object> economicTrendPieSeriesMap = new HashMap<String, Object>();
            economicTrendPieSeriesMap.put("name", legStr);
            map.put("typeName", legStr);
            Double money = statisticalAnalysisMapper.economicTrendPieMoney(map, tableNames);
            economicTrendPieSeriesMap.put("value", money);
            economicTrendPieSeries.add(economicTrendPieSeriesMap);
        }
        return economicTrendPieSeries;
    }
}
