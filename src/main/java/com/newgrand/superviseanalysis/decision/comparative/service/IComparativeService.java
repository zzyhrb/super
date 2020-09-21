package com.newgrand.superviseanalysis.decision.comparative.service;

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
public interface IComparativeService {

    List<String> comparativeLegend(Map<String, Object> map);

    List<String> comparativeXAxis(Map<String, Object> map,String order);

    List<Map<String, Object>> comparativeSeries(Map<String, Object> map, String type, List<String> legend);

    List<Map<String, Object>> proportionSeries(Map<String, Object> map,String order);

    List<String> sameProportionXAxis(Map<String, Object> map);

    List<Double> sameProportionSeries(Map<String, Object> map);

}
