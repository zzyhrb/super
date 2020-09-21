package com.newgrand.superviseanalysis.decision.manage.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newgrand.superviseanalysis.decision.manage.service.IManageDataService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Collections;
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
@RequestMapping("/manage-data")
public class ManageDataController {
    private IManageDataService manageDataService;
    private IProjectTableService projectTableService;
    private IProjectService projectService;
    private IProjectTreeService projectTreeService;

    @Autowired
    public ManageDataController(IManageDataService manageDataService, IProjectTableService projectTableService, IProjectService projectService, IProjectTreeService projectTreeService) {
        this.manageDataService = manageDataService;
        this.projectTableService = projectTableService;
        this.projectService = projectService;
        this.projectTreeService = projectTreeService;
    }

    /**
     * @throws
     * @title 经营数据查询页面跳转
     * @description
     * @author tly
     * @updateTime 2020/4/7 14:38
     */
    @RequestMapping("/page")
    public String Page(HttpSession httpSession, Model model) {
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");
        List<PmProject> projectList = projectService.list(new QueryWrapper<PmProject>().and(i -> i.likeRight("organ_id", user.getOrganId()).eq("is_delete", 0)));

        model.addAttribute("projectList", projectList);
        return "manage/list";
    }

    /**
     * @throws
     * @title 经营数据查询
     * @description
     * @author admin
     * @updateTime 2020/4/7 14:38
     */
    @ResponseBody
    @PostMapping("/list")
    public Result list(PmProjectTree pmProjectTree) {
        if("".equals(pmProjectTree.getParentId())){
            // 查询明细
            pmProjectTree.setColumnDetail(null);
            pmProjectTree.setParentId(null);
            if("".equals(pmProjectTree.getTreeId())){
                pmProjectTree.setTreeId(null);
            }
            return Result.success(projectTreeService.list(new QueryWrapper<PmProjectTree>().setEntity(pmProjectTree).ne("parent_id", 0)));
        }else{
            if("".equals(pmProjectTree.getTreeId())){
                pmProjectTree.setTreeId(null);
            }
            pmProjectTree.setColumnDetail(null);
            return Result.success(projectTreeService.list(new QueryWrapper<PmProjectTree>().setEntity(pmProjectTree).ne("parent_id", 0)));
        }

    }
    /**
     * @title 动态列加载
     * @description
     * @author admin
     * @updateTime 2020/4/29 15:38
     * @throws
     */
    @ResponseBody
    @PostMapping("/dynamic/column")
    public Result dynamicColumn(String date, String time, String projectId) {
        Map<String, Object> map = new HashMap<>();
        String[] dateShus = date.split("~");
        map.put("startTime", dateShus[0]);
        map.put("endTime", dateShus[1]);
        map.put("time", time);
        PmProjectTable projectTable = projectTableService.getOne(new QueryWrapper<PmProjectTable>().eq("project_id", projectId));

        if (projectTable != null) {
            map.put("tableName",projectTable.getTableName());
            List<Map<String,Object>> projectTableModel = manageDataService.dynamicColumn(map);
            return Result.success(projectTableModel);
        } else {
            return Result.success(Collections.emptyList());
        }
    }

    @ResponseBody
    @PostMapping("/dynamic/columnData")
    public Result dynamicColumnData(String date, String time, String projectId, String column, String parentId, String columnDetail) {
        Map<String, Object> map = new HashMap<>();
        String[] dateShus = date.split("~");
        map.put("startTime", dateShus[0]);
        map.put("endTime", dateShus[1]);
        map.put("time", time);
        map.put("column", column);
        map.put("parentId", parentId);
        map.put("projectId", projectId);

        PmProjectTable projectTable = projectTableService.getOne(new QueryWrapper<PmProjectTable>().eq("project_id", projectId));

        if (projectTable != null) {
            map.put("tableName",projectTable.getTableName());
            List<Map<String,Object>> projectTableModel = null;

            if("1".equals(columnDetail)){
                projectTableModel = manageDataService.dynamicColumnDetailData(map);
            }else{
                projectTableModel = manageDataService.dynamicColumnData(map);
            }
            return Result.success(projectTableModel);
        } else {
            return Result.success(Collections.emptyList());
        }
    }

    @ResponseBody
    @PostMapping("/projectTreeByProjectId")
    public Result projectTreeByProjectId(PmProjectTree pmProjectTree) {
        PmProjectTree projectTree = projectTreeService.getOne(new QueryWrapper<PmProjectTree>().setEntity(pmProjectTree));
        return Result.success(projectTree);
    }


}
