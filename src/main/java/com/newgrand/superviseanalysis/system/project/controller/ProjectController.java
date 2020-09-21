package com.newgrand.superviseanalysis.system.project.controller;


import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newgrand.superviseanalysis.system.dict.entity.SysDict;
import com.newgrand.superviseanalysis.system.dict.service.IDictService;
import com.newgrand.superviseanalysis.system.organ.entity.SysOrgan;
import com.newgrand.superviseanalysis.system.organ.service.IOrganService;
import com.newgrand.superviseanalysis.system.project.entity.PmProject;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTable;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTree;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectType;
import com.newgrand.superviseanalysis.system.project.service.IProjectService;
import com.newgrand.superviseanalysis.system.project.service.IProjectTableService;
import com.newgrand.superviseanalysis.system.project.service.IProjectTreeService;
import com.newgrand.superviseanalysis.system.project.service.IProjectTypeService;
import com.newgrand.superviseanalysis.system.project.vo.ProjectOrganUserVO;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import com.newgrand.superviseanalysis.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */
@Controller
@RequestMapping("/project")
public class ProjectController {
    // 组织服务
    @Autowired
    private IOrganService organService;
    // 项目服务
    @Autowired
    private IProjectService projectService;
    // 项目树服务
    @Autowired
    private IProjectTreeService projectTreeService;
    // 项目类型服务
    @Autowired
    private IProjectTypeService projectTypeService;
    // 字典服务
    @Autowired
    private IDictService dictService;
    // 项目对应表服务
    @Autowired
    private IProjectTableService projectTableService;

    @RequestMapping("/page")
    public String Page(HttpServletRequest request) {
        return "project/list";
    }

    @ResponseBody
    @RequestMapping("/list")
    public Result list(ProjectOrganUserVO projectOrganUserVO) {
        return Result.success(projectService.getProject(projectOrganUserVO));
    }

    @ResponseBody
    @RequestMapping("/del")
    public Result delete(PmProject pmProject) {
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("project_id", pmProject.getProjectId());
        updateWrapper.set("is_delete", -1);
        if (projectService.update(updateWrapper)) {
            return Result.success(ResponseMsg.DELETE_SUCCESS);
        } else {
            return Result.success(ResponseMsg.DELETE_ERROR);
        }
    }

    @RequestMapping("/addOrEditPage")
    public String addOrEditPage(HttpServletRequest request, PmProject pmProject, Model model, String type, HttpSession httpSession) {
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");
        if ("edit".equals(type)) {
            pmProject = projectService.getById(pmProject.getProjectId());
            model.addAttribute("pmProject", pmProject);
        }
        SysOrgan sysOrgan = new SysOrgan();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.gt("len(organ_id)", 4);
        queryWrapper.setEntity(sysOrgan);
        List organList = organService.list(queryWrapper);
        model.addAttribute("organList", organList);
        model.addAttribute("type", type);
        model.addAttribute("projectList", projectService.list(new QueryWrapper<PmProject>().and(i -> i.eq("is_delete", 0).likeRight("organ_id", user.getOrganId()))));
        return "project/addOrEdit";
    }

    @ResponseBody
    @RequestMapping("/addOrEdit")
    public Result addOrEdit(PmProject pmProject, String copyProjectId, String type, HttpSession httpSession) {
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");
        try {
            pmProject.setCreateTime(new Date());
            pmProject.setIsDelete(0);
            pmProject.setUserId(user.getUserId());
            projectService.saveOrUpdate(pmProject);
            if ("add".equals(type)) {
                String maxTreeId = projectService.getProjectMaxTreeId();
                Integer projectTreeId = Integer.parseInt(maxTreeId) + 1;
                String treeId = "";
                if (String.valueOf(projectTreeId).length() == 1) {
                    treeId = "0" + String.valueOf(projectTreeId);
                } else {
                    treeId = String.valueOf(projectTreeId);
                }

                PmProjectTree projectTree = new PmProjectTree();
                projectTree.setTreeId(treeId);
                projectTree.setProjectId(pmProject.getProjectId());
                projectTree.setTreeOrder(1);
                projectTree.setUserId(user.getUserId());
                projectTree.setTreeName(pmProject.getProjectName());
                projectTree.setCreateTime(new Date());
                projectTree.setParentId("0");
                projectTreeService.save(projectTree);

                // 从其它项目复制
                if (copyProjectId != null) {
                    projectTreeService.copyProjectTree(projectTree, copyProjectId);

                }
                // 创建项目表
                PmProjectTable projectTable = new PmProjectTable();
                projectTable.setProjectId(pmProject.getProjectId());
                projectTable.setTableName("pm_project_detail_" + PinYin4JUtil.toPinyin(pmProject.getProjectName()));
                projectTableService.create(projectTable.getTableName());
                projectTableService.save(projectTable);
            }

            return Result.success(ResponseMsg.INSERT_UPDATE_SUCCESS);
        } catch (NumberFormatException e) {
            return Result.success(ResponseMsg.INSERT_UPDATE_ERROR);
        }
    }

    @RequestMapping("/set")
    public ModelAndView set(PmProject pmProject) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectId", pmProject.getProjectId());
        return new ModelAndView("project/set/list", map);
    }

    @ResponseBody
    @RequestMapping("/set/list")
    public Result setList(String projectId) {
        QueryWrapper<PmProjectTree> queryWrapper = new QueryWrapper();
        queryWrapper.select("tree_id", "tree_name", "parent_id", "column_detail");
        queryWrapper.eq("is_delete", "0");
        queryWrapper.eq("project_id", projectId);
        queryWrapper.orderByAsc("tree_order");
        List<Map<String, Object>> list = projectTreeService.listMaps(queryWrapper);
        ArrayList<String> param = new ArrayList<String>();
        param.add("column_detail");
        JSONArray treeJson = TreeUtils.treeRecursionDataList(list, BigInteger.valueOf(0), "tree_id", "tree_name", "parent_id", param);

        return Result.success(treeJson);
    }

    @ResponseBody
    @RequestMapping("/set/detail")
    public Result setDetail(HttpServletRequest request, String treeId) {
        PmProjectTree pmProjectTree = projectTreeService.getById(treeId);
        return Result.success(pmProjectTree);
    }

    @RequestMapping("/set/addOrEditPage")
    public String addOrEditPage(Model model, PmProjectTree pmProjectTree, String type) {
        if ("add".equals(type)) {
            pmProjectTree = projectTreeService.getById(pmProjectTree.getParentId());
            model.addAttribute("parentId", pmProjectTree.getTreeId());
            model.addAttribute("projectId", pmProjectTree.getProjectId());

        } else {
            pmProjectTree = projectTreeService.getById(pmProjectTree.getTreeId());
            model.addAttribute("treeId", pmProjectTree.getTreeId());
            model.addAttribute("parentId", pmProjectTree.getParentId());
            model.addAttribute("columnDetail", pmProjectTree.getColumnDetail());
            model.addAttribute("pmProjectTree", pmProjectTree);
            model.addAttribute("typeId", pmProjectTree.getTypeId());
            model.addAttribute("projectId", pmProjectTree.getProjectId());

        }
        // 查询项目类型
        QueryWrapper<PmProjectType> queryWrapper = new QueryWrapper();
        PmProjectType pmProjectType = new PmProjectType();
        queryWrapper.setEntity(pmProjectType);

        // 查询字典
        QueryWrapper<SysDict> sysDictQueryWrapper = new QueryWrapper<>();
        SysDict sysDict = new SysDict();
        sysDict.setParentId((long) 0);
        sysDictQueryWrapper.setEntity(sysDict);
        sysDictQueryWrapper.orderByAsc("dict_order");

        model.addAttribute("typeList", projectTypeService.list(queryWrapper));
        model.addAttribute("dictList", dictService.list(sysDictQueryWrapper));
        model.addAttribute("type", type);

        return "project/set/addOrEdit";

    }

    @ResponseBody
    @RequestMapping("/set/addOrEdit")
    public Result addOrEdit(HttpSession httpSession, PmProjectTree pmProjectTree, String type) {
        try {
            SysUser user = (SysUser) httpSession.getAttribute("userInfo");
            if (pmProjectTree.getColumnDetail() == 0) {
                pmProjectTree.setDictId(null);
            }
            if ("add".equals(type)) {
                String treeId = projectTreeService.getTreeId(pmProjectTree.getParentId());
                pmProjectTree.setTreeId(treeId);
                pmProjectTree.setCreateTime(new Date());
                pmProjectTree.setUserId(user.getUserId());
                projectTreeService.save(pmProjectTree);

            } else {
                projectTreeService.saveOrUpdate(pmProjectTree);

            }
            return Result.success(ResponseMsg.INSERT_UPDATE_SUCCESS);
        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_999, ResponseMsg.INSERT_UPDATE_ERROR);
        }
    }

    @ResponseBody
    @RequestMapping("/set/delete")
    public Result delete(PmProjectTree pmProjectTree) {
        try {
            UpdateWrapper<PmProjectTree> updateWrapper = new UpdateWrapper();
            updateWrapper.set("is_delete", -1);
            updateWrapper.eq("tree_id", pmProjectTree.getTreeId());
            updateWrapper.or(i -> i.eq("parent_id", pmProjectTree.getTreeId()));
            projectTreeService.update(updateWrapper);
            return Result.success(ResponseMsg.DELETE_SUCCESS);
        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_999, ResponseMsg.DELETE_ERROR);
        }
    }

    //注解时间类型
    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {
        //转换日期
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));// CustomDateEditor为自定义日期编辑器

    }
}
