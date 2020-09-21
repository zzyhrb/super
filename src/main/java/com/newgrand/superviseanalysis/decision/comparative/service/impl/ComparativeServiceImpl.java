package com.newgrand.superviseanalysis.decision.comparative.service.impl;

import com.newgrand.superviseanalysis.decision.comparative.mapper.ComparativeMapper;
import com.newgrand.superviseanalysis.decision.comparative.service.IComparativeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tly
 * @version 1.0.0
 * @ClassName StatisticalAnalysisServiceImpl.java
 * @Description TODO
 * @createTime 2020年04月08日 10:56:00
 */
@Service
public class ComparativeServiceImpl implements IComparativeService {
    private ComparativeMapper comparativeMapper;

    @Autowired
    public ComparativeServiceImpl(ComparativeMapper comparativeMapper) {
        this.comparativeMapper = comparativeMapper;

    }


    @Override
    public List<String> comparativeLegend(Map<String, Object> map) {
        return comparativeMapper.comparativeLegend(map);
    }

    @Override
    public List<String> comparativeXAxis(Map<String, Object> map,String order) {
        return comparativeMapper.comparativeXAxis(map,order);
    }

    @Override
    public List<Map<String, Object>> comparativeSeries(Map<String, Object> map, String type, List<String> legend) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> labelMap = new HashMap<String, Object>();
        labelMap.put("show",true);
        labelMap.put("position", "inside");
        for (String legendStr : legend) {
            map.put("legendStr", legendStr);
            Map<String, Object> seriesMap = new HashMap<>();
            seriesMap.put("name", legendStr);
            seriesMap.put("type", type);
            seriesMap.put("label", labelMap);

            if ("season".equals(map.get("time"))) {
                if ("第一季度".equals(legendStr)) {
                    map.put("day", "(1,2,3)");
                } else if ("第二季度".equals(legendStr)) {
                    map.put("day", "(4,5,6)");
                } else if ("第三季度".equals(legendStr)) {
                    map.put("day", "(7,8,9)");
                } else {
                    map.put("day", "(10,11,12)");
                }
            }
            List<Double> seriesData = comparativeMapper.comparativeSeries(map);
            seriesMap.put("data", seriesData);
            list.add(seriesMap);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> proportionSeries(Map<String, Object> map,String order) {
        List<Map<String, Object>> list = comparativeMapper.proportionSeries(map,order);
        return list;
    }

    @Override
    public List<String> sameProportionXAxis(Map<String, Object> map) {
        return comparativeMapper.sameProportionXAxis(map);
    }

    @Override
    public List<Double> sameProportionSeries(Map<String, Object> map) {
        return comparativeMapper.sameProportionSeries(map);
    }
}
