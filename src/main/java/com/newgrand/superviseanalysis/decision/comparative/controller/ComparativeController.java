package com.newgrand.superviseanalysis.decision.comparative.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newgrand.superviseanalysis.decision.comparative.service.IComparativeService;
import com.newgrand.superviseanalysis.system.project.entity.PmProject;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTable;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTree;
import com.newgrand.superviseanalysis.system.project.service.IProjectService;
import com.newgrand.superviseanalysis.system.project.service.IProjectTableService;
import com.newgrand.superviseanalysis.system.project.service.IProjectTreeService;
import com.newgrand.superviseanalysis.system.project.service.IProjectTypeService;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import com.newgrand.superviseanalysis.utils.EchartsData;
import com.newgrand.superviseanalysis.utils.EchartsUtil;
import com.newgrand.superviseanalysis.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tly
 * @version 1.0.0
 * @ClassName ComparativeController.java
 * @Description TODO
 * @createTime 2020/4/26 13:33
 */
@Controller
@RequestMapping("/comparative")
public class ComparativeController {
    private IComparativeService statisticalAnalysisService;
    private IProjectTypeService projectTypeService;
    private IProjectTableService projectTableService;
    private IProjectService projectService;
    private IComparativeService comparativeService;
    private IProjectTreeService projectTreeService;

    @Autowired
    public ComparativeController(IComparativeService statisticalAnalysisService, IProjectTypeService projectTypeService, IProjectTableService projectTableService, IProjectService projectService, IComparativeService comparativeService, IProjectTreeService projectTreeService) {
        this.statisticalAnalysisService = statisticalAnalysisService;
        this.projectTypeService = projectTypeService;
        this.projectTableService = projectTableService;
        this.projectService = projectService;
        this.comparativeService = comparativeService;
        this.projectTreeService = projectTreeService;
    }

    /**
     * @throws
     * @title 统计分析页面跳转
     * @description
     * @author admin
     * @updateTime 2020/4/9 15:52
     */
    @RequestMapping("/page")
    public String page(HttpSession httpSession, Model model) {
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");
        List<PmProject> projectList = projectService.list(new QueryWrapper<PmProject>().and(i -> i.likeRight("organ_id", user.getOrganId()).eq("is_delete", 0)));
        model.addAttribute("projectList", projectList);
        return "comparative/list";
    }

    /**
     * @title 对比分析图形
     * @description 
     * @author admin 
     * @updateTime 2020/4/26 14:35 
     * @throws 
     */
    @ResponseBody
    @PostMapping("/comparativeChart")
    public Result comparativeChart(String date, String time, Integer projectId, String parentId) {
        Map<String, Object> map = paramMap(date, time, projectId, parentId);

        List<String> legend = comparativeService.comparativeLegend(map);
        List<String> xAxis = comparativeService.comparativeXAxis(map, "asc");

        List<Map<String, Object>> series = comparativeService.comparativeSeries(map, "bar", legend);
        EchartsData echarts = EchartsUtil.getEcharts(legend, xAxis, series);
        return Result.success(echarts);
    }

    /**
     * @title 比例分析图形
     * @description
     * @author admin
     * @updateTime 2020/4/27 14:04
     * @throws
     */
    @ResponseBody
    @PostMapping("/proportionChart")
    public Result proportionChart(String date, String time, Integer projectId, String parentId) {
        Map<String, Object> map = paramMap(date, time, projectId, parentId);
        List<String> legend = comparativeService.comparativeXAxis(map, "asc");
        List<Map<String, Object>> series = comparativeService.proportionSeries(map, "asc");
        EchartsData echarts = EchartsUtil.getEcharts(legend, null, series);
        return Result.success(echarts);
    }

    /**
     * @title 同比分析图形
     * @description
     * @author admin
     * @updateTime 2020/4/27 14:04
     * @throws
     */
    @ResponseBody
    @PostMapping("/sameProportionChart")
    public Result sameProportionChart(String date, String time, Integer projectId, String parentId) {
        Map<String, Object> map = paramMap(date, time, projectId, parentId);
        List<String> xAxis = comparativeService.sameProportionXAxis(map);
        List<Double> series = comparativeService.sameProportionSeries(map);
        EchartsData echarts = new EchartsData();
        echarts.setXAxis(xAxis);
        echarts.setSeries(series);
        echarts.setLegend(projectTreeService.listObjs(new QueryWrapper<PmProjectTree>().select("tree_name").eq("tree_id",parentId)));
        return Result.success(echarts);
    }

    /**
     * @title
     * @description
     * @author admin
     * @updateTime 2020/4/27 14:02
     * @throws
     */
    private Map<String, Object> paramMap(String date, String time, Integer projectId, String parentId) {
        Map<String, Object> map = new HashMap<>();
        String[] dateShus = date.split("~");
        map.put("startTime", dateShus[0]);
        map.put("endTime", dateShus[1]);
        map.put("time", time);
        map.put("projectId", projectId);
        map.put("tableName", getPmProjectTable(projectId).getTableName());
        map.put("parentId", parentId);
        return map;
    }

    /**
     * @title
     * @description
     * @author admin
     * @updateTime 2020/4/27 14:02
     * @throws
     */
    private PmProjectTable getPmProjectTable(Integer projectId) {
        return projectTableService.getOne(new QueryWrapper<PmProjectTable>().eq("project_id", projectId));
    }


}
