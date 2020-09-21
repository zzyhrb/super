package com.newgrand.superviseanalysis.decision.yeareconomics.service;

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
public interface IYearEconomicsService {

    List<Map<String, Object>> options(List<String> timeLine, String name);

    List<Map<String, Object>> series(String bar, List<String> legend);

    List<String> allYear(List<String> projectTables);
}
