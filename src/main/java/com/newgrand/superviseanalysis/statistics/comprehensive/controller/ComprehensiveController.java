package com.newgrand.superviseanalysis.statistics.comprehensive.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newgrand.superviseanalysis.daily.workFlow.service.IActivitiService;
import com.newgrand.superviseanalysis.statistics.comprehensive.service.IComprehensiveService;
import com.newgrand.superviseanalysis.system.project.entity.PmProject;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTable;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTree;
import com.newgrand.superviseanalysis.system.project.service.IProjectService;
import com.newgrand.superviseanalysis.system.project.service.IProjectTableService;
import com.newgrand.superviseanalysis.system.project.service.IProjectTreeService;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import com.newgrand.superviseanalysis.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.HashMap;
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
@RequestMapping("/comprehensive")
public class ComprehensiveController {

    private IProjectService projectService;
    private IProjectTreeService projectTreeService;
    private IProjectTableService projectTableService;
    private IComprehensiveService comprehensiveService;
    private IActivitiService activitiService;

    @Autowired
    public ComprehensiveController(IProjectService projectService, IProjectTreeService projectTreeService, IProjectTableService projectTableService, IComprehensiveService comprehensiveService, IActivitiService activitiService) {
        this.projectService = projectService;
        this.projectTreeService = projectTreeService;
        this.projectTableService = projectTableService;
        this.comprehensiveService = comprehensiveService;
        this.activitiService = activitiService;
    }

    /**
     * @throws
     * @title 跳转综合查询页面
     * @description
     * @author admin
     * @updateTime 2020/4/24 9:39
     */
    @RequestMapping("/page")
    public String Page(HttpSession httpSession, Model model) {
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");
        List<PmProject> projectList = projectService.list(new QueryWrapper<PmProject>().and(i -> i.likeRight("organ_id", user.getOrganId()).eq("is_delete", 0)));
        model.addAttribute("projectList", projectList);
        return "comprehensive/list";
    }

    @ResponseBody
    @RequestMapping("/list")
    public Result list(PmProjectTable pmProjectTable, String parentId, String applyFlag, String date, String universalQuerySql, String queryState) {
        Map<String, Object> map = new HashMap<>();
        map.put("parentId", parentId);
        map.put("applyFlag", applyFlag);
        String[] dateShus = date.split("~");
        map.put("startTime", dateShus[0]);
        map.put("endTime", dateShus[1]);
        if ("on".equals(queryState)) {
            map.put("universalQuerySql", universalQuerySql);
        }

        pmProjectTable = projectTableService.getOne(new QueryWrapper<PmProjectTable>().setEntity(pmProjectTable));
        List<Map<String, Object>> list = comprehensiveService.list(pmProjectTable, map);
//        List<Map<String, Object>> list = activitiService.getDetail(pmProjectTable.getTableName(), "0", pmProjectTable.getProjectId());
        int count = 0;
        for (Map<String, Object> listMap:list) {
            if("0".equals(listMap.get("parent_id"))){
                count++;
                listMap.put("parent_id", Integer.valueOf(listMap.get("parent_id").toString()));
                listMap.put("tree_id", BigInteger.valueOf(Long.parseLong(String.valueOf(count)+listMap.get("tree_id"))));
            }else{
                listMap.put("parent_id", BigInteger.valueOf(Long.parseLong(String.valueOf(count)+listMap.get("parent_id"))));
                listMap.put("tree_id", BigInteger.valueOf(Long.parseLong(String.valueOf(count)+listMap.get("tree_id"))));
            }


        }
        return Result.success(list);
    }

    /**
     * @throws
     * @title 通过项目ID查询项目字段
     * @description
     * @author admin
     * @updateTime 2020/4/24 10:40
     */
    @ResponseBody
    @RequestMapping("/tree/list")
    public Result columnList(PmProjectTree pmProjectTree) {
        List<PmProjectTree> treeList = projectTreeService.list(new QueryWrapper<PmProjectTree>().eq("project_id", pmProjectTree.getProjectId()).eq("is_delete", 0).ne("parent_id", "0"));
        return Result.success(treeList);
    }
}
