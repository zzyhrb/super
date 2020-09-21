package com.newgrand.superviseanalysis.utils;


import java.util.List;
import java.util.Map;

/**
 * @author tly
 * @version 1.0.0
 * @ClassName EchartsUtil.java
 * @Description TODO
 * @createTime 2020年04月08日 09:59:00
 */
public class EchartsUtil {
    /**
     * @throws
     * @title 返回Echarts类型的数据
     * @description
     * @author admin
     * @updateTime 2020/4/8 10:16
     */
    public static EchartsData getEcharts(List<String> legend, List<String> xAxis, List<Map<String,Object>> series) {
        // 图形数据对象
        EchartsData echartsData = new EchartsData();
        // 设置legend 数据
        echartsData.setLegend(legend);
        // 设置X轴数据
        echartsData.setXAxis(xAxis);
        // 设置series
        echartsData.setSeries(series);
        return echartsData;
    }
}
