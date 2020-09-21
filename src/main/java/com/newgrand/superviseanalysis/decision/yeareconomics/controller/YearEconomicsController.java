package com.newgrand.superviseanalysis.decision.yeareconomics.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newgrand.superviseanalysis.decision.yeareconomics.service.IYearEconomicsService;
import com.newgrand.superviseanalysis.system.organ.service.IOrganService;
import com.newgrand.superviseanalysis.system.project.service.IProjectTableService;
import com.newgrand.superviseanalysis.system.project.service.IProjectTypeService;
import com.newgrand.superviseanalysis.utils.EchartsData;
import com.newgrand.superviseanalysis.utils.EchartsUtil;
import com.newgrand.superviseanalysis.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */
@Controller
@RequestMapping("/year-economics")
public class YearEconomicsController {
    @Autowired
    private IProjectTableService projectTableService;
    @Autowired
    private IYearEconomicsService yearEconomicsService;
    @Autowired
    private IOrganService organService;
    @Autowired
    private IProjectTypeService projectTypeService;

    /**
     * @throws
     * @title 年经济统计查询页面跳转
     * @description
     * @author tly
     * @updateTime 2020/4/7 14:38
     */
    @RequestMapping("/page")
    public String page() {
        return "yeareconomics/list";
    }

    /**
     * @throws
     * @title 年经济统计数据查询
     * @description
     * @author admin
     * @updateTime 2020/4/7 14:38
     */
    @ResponseBody
    @PostMapping("/echarts")
    public Result echarts() {
        // 构建legend查询器
        QueryWrapper legendWrapper = new QueryWrapper<>();
        // 查询项目类型名称
        legendWrapper.select("type_name");
        // 查询正在使用
        legendWrapper.eq("is_delete", 0);
        legendWrapper.in("type_id", 1,2,3);

        // 查询lengend
        List<String> legend = projectTypeService.listObjs(legendWrapper);

        // 查询series
        List<Map<String, Object>> series = yearEconomicsService.series("bar", legend);

        // 构建查询器
        QueryWrapper projectTableWrapper = new QueryWrapper<>();
        projectTableWrapper.select("table_name");
        List<String> projectTables = projectTableService.listObjs(projectTableWrapper);
        List<String> timeLine = yearEconomicsService.allYear(projectTables);

        // 构建查询器
        QueryWrapper queryWrapper = new QueryWrapper();
        // 查询组织名称
        queryWrapper.select("organ_name");
        // 查询正在使用
        queryWrapper.eq("is_delete", 0);
        // 查询ID大于4即二级以下组织
        queryWrapper.gt("len(organ_id)", 4);
        // 查询xAxis
        List<String> xAxis = organService.listObjs(queryWrapper);
        // 查询options
        List<Map<String, Object>> options = yearEconomicsService.options(timeLine, "直属事业单位经济情况");

        EchartsData echarts = EchartsUtil.getEcharts(legend, xAxis, series);
        echarts.setTimeline(timeLine);
        echarts.setOptions(options);
        return Result.success(echarts);

    }
}
