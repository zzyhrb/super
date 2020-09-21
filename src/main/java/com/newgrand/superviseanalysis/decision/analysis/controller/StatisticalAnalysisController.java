package com.newgrand.superviseanalysis.decision.analysis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newgrand.superviseanalysis.decision.analysis.service.IStatisticalAnalysisService;
import com.newgrand.superviseanalysis.system.organ.entity.SysOrgan;
import com.newgrand.superviseanalysis.system.organ.service.IOrganService;
import com.newgrand.superviseanalysis.system.project.service.IProjectTableService;
import com.newgrand.superviseanalysis.system.project.service.IProjectTypeService;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import com.newgrand.superviseanalysis.utils.EchartsData;
import com.newgrand.superviseanalysis.utils.EchartsUtil;
import com.newgrand.superviseanalysis.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tly
 * @version 1.0.0
 * @ClassName StatisticalAnalysisController.java
 * @Description TODO
 * @createTime 2020年04月09日 15:52:00
 */
@Controller
@RequestMapping("/statistical-analysis")
public class StatisticalAnalysisController {
    private IStatisticalAnalysisService statisticalAnalysisService;
    private IProjectTypeService projectTypeService;
    private IProjectTableService projectTableService;
    private IOrganService organService;

    @Autowired
    public StatisticalAnalysisController(IStatisticalAnalysisService statisticalAnalysisService, IProjectTypeService projectTypeService, IProjectTableService projectTableService, IOrganService organService) {
        this.statisticalAnalysisService = statisticalAnalysisService;
        this.projectTypeService = projectTypeService;
        this.projectTableService = projectTableService;
        this.organService = organService;
    }

    /**
     * @throws
     * @title 统计分析页面跳转
     * @description
     * @author admin
     * @updateTime 2020/4/9 15:52
     */
    @RequestMapping("/page")
    public String page(HttpServletRequest request) {
        return "analysis/list";
    }

    /**
     * @throws
     * @title 经济趋势
     * @description
     * @author admin
     * @updateTime 2020/4/9 15:52
     */
    @ResponseBody
    @PostMapping("/economicTrend")
    public Result economicTrend(HttpSession httpSession, String economicsType, String date) {
        String[] timeShus = date.split("~");
        Map<String, Object> map = new HashMap<>();
        map.put("startTime", timeShus[0]);
        map.put("endTime", timeShus[1]);
        map.put("economicsType", economicsType);
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");

        // 构建legend查询器
        QueryWrapper legendWrapper = new QueryWrapper<>();
        // 查询项目类型名称
        legendWrapper.select("type_name");
        // 查询正在使用
        legendWrapper.eq("is_delete", 0);
        legendWrapper.in("type_id", 1,2,3);

        // 查询lengend
        List<String> legend = projectTypeService.listObjs(legendWrapper);
        // 获取单位下所属项目表名
        List<String> tableNames = projectTableService.selectTableNamesByOrganId(user.getOrganId());

        List<String> economicTrendXAxis = statisticalAnalysisService.economicTrendXAxis(map, user, tableNames);

        List<Map<String, Object>> economicTrendSeries = statisticalAnalysisService.economicTrendSeries(map, user, tableNames, economicTrendXAxis);

        EchartsData echarts = EchartsUtil.getEcharts(legend, economicTrendXAxis, economicTrendSeries);
        return Result.success(echarts);
    }

    /**
     * @title 经济趋势比例
     * @description
     * @author admin
     * @updateTime 2020/5/27 11:41
     * @throws
     */
    @ResponseBody
    @PostMapping("/economicTrendPie")
    public Result economicTrendPie(HttpSession httpSession, String economicsType, String date) {
        String[] timeShus = date.split("~");
        Map<String, Object> map = new HashMap<>();
        map.put("startTime", timeShus[0]);
        map.put("endTime", timeShus[1]);
        map.put("economicsType", economicsType);
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");

        // 构建legend查询器
        QueryWrapper legendWrapper = new QueryWrapper<>();
        // 查询项目类型名称
        legendWrapper.select("type_name");
        // 查询正在使用
        legendWrapper.eq("is_delete", 0);
        legendWrapper.in("type_id", 1,2,3);

        // 查询lengend
        List<String> legend = projectTypeService.listObjs(legendWrapper);
        // 获取单位下所属项目表名
        List<String> tableNames = projectTableService.selectTableNamesByOrganId(user.getOrganId());

        List<Map<String, Object>> economicTrendSeries = statisticalAnalysisService.economicTrendPieSeries(map, user, tableNames, legend);

        EchartsData echarts = EchartsUtil.getEcharts(legend, null, economicTrendSeries);
        return Result.success(echarts);
    }

    /**
     * @throws
     * @title 主要盈利
     * @description
     * @author admin
     * @updateTime 2020/4/9 15:52
     */
    @ResponseBody
    @PostMapping("/page")
    public Result Page(String type, String startTime, String endTime) {
        return Result.success("");
    }

    /**
     * @throws
     * @title 主要盈利
     * @description
     * @author admin
     * @updateTime 2020/4/9 15:52
     */
    @ResponseBody
    @PostMapping("/mainProfitChart")
    public Result mainProfitChart(HttpSession httpSession, String profitType, String date) {
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");
        String[] timeShus = date.split("~");

        Map<String, Object> map = new HashMap<>();
        map.put("startTime", timeShus[0]);
        map.put("endTime", timeShus[1]);
        map.put("organId",user.getOrganId());
        List<Map<String,Object>> top5= statisticalAnalysisService.queryTop5(profitType,user,map);
        // 查询lengend
        List<String> legend = statisticalAnalysisService.mainProfitChartLegend(top5);
        List<Map<String,Object>> mainProfitChartSeries = statisticalAnalysisService.mainProfitChartXAxis(top5);

        EchartsData echarts = EchartsUtil.getEcharts(legend, null, mainProfitChartSeries);
        return Result.success(echarts);
    }

    /**
     * @throws
     * @title 下级统计
     * @description
     * @author admin
     * @updateTime 2020/4/9 15:52
     */
    @ResponseBody
    @PostMapping("/childrenOrganChart")
    public Result childrenOrganChart(HttpSession httpSession, String profitType, String date) {
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");
        String[] timeShus = date.split("~");

        Map<String, Object> map = new HashMap<>();
        map.put("startTime", timeShus[0]);
        map.put("endTime", timeShus[1]);

        // 构建legend查询器
        QueryWrapper legendWrapper = new QueryWrapper<>();
        // 查询项目类型名称
        legendWrapper.select("type_name");
        // 查询正在使用
        legendWrapper.eq("is_delete", 0);
        legendWrapper.in("type_id", 1,2,3);

        // 查询lengend
        List<String> legend = projectTypeService.listObjs(legendWrapper);

        QueryWrapper xAxisWrapper = new QueryWrapper<>();

        SysOrgan sysOrgan = new SysOrgan();
        sysOrgan.setParentId(user.getOrganId());
        xAxisWrapper.select("organ_name");
        xAxisWrapper.setEntity(sysOrgan);
        xAxisWrapper.orderByAsc("organ_order");
        List<String> xAxis = organService.listObjs(xAxisWrapper);

        // 获取单位下所属项目表名
        List<String> tableNames = projectTableService.selectTableNamesByOrganId(user.getOrganId());
        List<Map<String,Object>> childrenOrganChartSeries = statisticalAnalysisService.childrenOrganChartSeries(map, xAxis);

        EchartsData echarts = EchartsUtil.getEcharts(legend, xAxis, childrenOrganChartSeries);
        return Result.success(echarts);
    }
}
