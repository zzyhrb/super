package com.newgrand.superviseanalysis.decision.analysis.service;

import com.newgrand.superviseanalysis.system.user.entity.SysUser;

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
public interface IStatisticalAnalysisService {

    List<String> economicTrendXAxis(Map<String, Object> map, SysUser uesr, List<String> tableNames);

    List<Map<String, Object>> economicTrendSeries(Map<String, Object> map, SysUser uesr, List<String> tableNames, List<String> economicTrendXAxis);

    List<Map<String, Object>> queryTop5(String profitType, SysUser uesr, Map<String, Object> map);

    List<String> mainProfitChartLegend(List<Map<String, Object>> top5);

    List<Map<String, Object>> mainProfitChartXAxis(List<Map<String, Object>> top5);

    List<Map<String, Object>> childrenOrganChartSeries(Map<String, Object> map, List<String> xAxis);

    List<Map<String, Object>> economicTrendPieSeries(Map<String, Object> map, SysUser user, List<String> tableNames, List<String> legend);
}
