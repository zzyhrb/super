package com.newgrand.superviseanalysis.daily.projectInsert.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newgrand.superviseanalysis.daily.projectInsert.service.IProjectInsertService;
import com.newgrand.superviseanalysis.daily.workFlow.service.IActivitiService;
import com.newgrand.superviseanalysis.system.dict.service.IDictService;
import com.newgrand.superviseanalysis.system.organ.entity.SysOrgan;
import com.newgrand.superviseanalysis.system.organ.service.IOrganService;
import com.newgrand.superviseanalysis.system.project.entity.*;
import com.newgrand.superviseanalysis.system.project.service.IProjectPurposeService;
import com.newgrand.superviseanalysis.system.project.service.IProjectService;
import com.newgrand.superviseanalysis.system.project.service.IProjectTableService;
import com.newgrand.superviseanalysis.system.project.service.IProjectTreeService;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import com.newgrand.superviseanalysis.utils.ResponseCode;
import com.newgrand.superviseanalysis.utils.ResponseMsg;
import com.newgrand.superviseanalysis.utils.Result;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
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
@RequestMapping("/projectInsert")
public class ProjectInsertController {
    // 项目服务
    private IProjectService projectService;
    // 项目树服务
    private IProjectTreeService projectTreeService;
    // 字典服务
    private IDictService dictService;
    // 项目对应表服务
    private IProjectTableService projectTableService;
    // 项目录入服务
    private IProjectInsertService projectInsertService;
    // 工作流服务
    private IActivitiService activitiService;
    // 项目明细服务
    private IProjectPurposeService projectPurposeService;
    // 组织服务
    private IOrganService organService;


    @Autowired
    public ProjectInsertController(IProjectService projectService, IProjectTreeService projectTreeService, IDictService dictService, IProjectTableService projectTableService, IProjectInsertService projectInsertService, IActivitiService activitiService, IProjectPurposeService projectPurposeService, IOrganService organService) {
        this.projectService = projectService;
        this.projectTreeService = projectTreeService;
        this.dictService = dictService;
        this.projectTableService = projectTableService;
        this.projectInsertService = projectInsertService;
        this.activitiService = activitiService;
        this.projectPurposeService = projectPurposeService;
        this.organService = organService;
    }

    /**
     * @throws
     * @title 进入信息录入页面
     * @description
     * @author admin
     * @updateTime 2020/4/28 9:04
     */
    @RequestMapping("/page")
    public String Page(HttpSession httpSession, Model model) {
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");
        // 通过组织ID查询组织列表
        List<SysOrgan> organList = organService.list(new QueryWrapper<SysOrgan>().eq("is_delete", 0).eq("organ_id", user.getOrganId()).or(i ->
                i.eq("is_delete", 0).eq("parent_id", user.getOrganId())).orderByAsc("organ_id"));
        model.addAttribute("organList", organList);
        return "projectInsert/list";
    }

    @ResponseBody
    @RequestMapping("/insert-list/data")
    public Result list(HttpSession httpSession, PmProjectTable pmProjectTable, String time) {
        try {
            String[] timeShus = time.split("~");
            Map<String, Object> map = new HashMap<>();
            map.put("startTime", timeShus[0]);
            map.put("endTime", timeShus[1]);

            pmProjectTable = projectTableService.getOne(new QueryWrapper<>(pmProjectTable));
            SysUser user = (SysUser) httpSession.getAttribute("userInfo");
            List<Map<String, Object>> list = new ArrayList<>();
            if (1 == user.getRoleId()) {
                list = projectInsertService.list(pmProjectTable.getTableName(), null, map);

            } else {
                list = projectInsertService.list(pmProjectTable.getTableName(), user.getUserId(), map);

            }
            return Result.success(list);
        } catch (Exception e) {
            return Result.success(Collections.emptyList());
        }

    }

    @RequestMapping("/addOrEditPage")
    public String addOrEditPage(PmProject pmProject, Model model, String type, PmProjectTableModel pmProjectTableModel) {
        pmProject = projectService.getById(pmProject.getProjectId());

        QueryWrapper<PmProjectTable> projectTableWrapper = new QueryWrapper<>();
        projectTableWrapper.eq("project_id", pmProject.getProjectId());
        PmProjectTable projectTable = projectTableService.getOne(projectTableWrapper);

//        if( projectTable == null){
//
//            projectTable = new PmProjectTable();
//            projectTable.setProjectId(pmProject.getProjectId());
//            projectTable.setTableName("pm_project_detail_" + PinYin4JUtil.toPinyin(pmProject.getProjectName()));
//            projectTableService.create(projectTable.getTableName());
//            projectTableService.saveOrUpdate(projectTable);
//        }

        if ("add".equals(type)) {
            QueryWrapper<PmProjectTree> projectTreeWrapper = new QueryWrapper<>();
            projectTreeWrapper.select("tree_id id", "tree_name title", "parent_id pid", "column_detail", "dict_id", "type_id");
            projectTreeWrapper.eq("is_delete", 0);

            projectTreeWrapper.eq("project_id", pmProject.getProjectId());
            List<Map<String, Object>> tableList = projectTreeService.listMaps(projectTreeWrapper);
            model.addAttribute("tableList", tableList);
            model.addAttribute("pmProject", pmProject);
        } else {
            List<Map<String, Object>> tableList = activitiService.getDetail(projectTable.getTableName(), pmProjectTableModel.getApplyFlag(), projectTable.getProjectId());
            QueryWrapper<PmProjectPurpose> queryWrapper = new QueryWrapper();
            PmProjectPurpose pmProjectPurpose = new PmProjectPurpose();
            pmProjectPurpose.setApplyFlag(pmProjectTableModel.getApplyFlag());
            queryWrapper.setEntity(pmProjectPurpose);
            List<PmProjectPurpose> detailList = projectPurposeService.list(queryWrapper);
            model.addAttribute("tableList", tableList);
            model.addAttribute("pmProject", pmProject);
            model.addAttribute("detailList", detailList);
            model.addAttribute("applyFlag", pmProjectTableModel.getApplyFlag());

        }

        model.addAttribute("tableName", projectTable.getTableName());
        model.addAttribute("type", type);

        return "projectInsert/addOrEdit";
    }

    @ResponseBody
    @PostMapping("/add")
    public Result add(HttpServletRequest request, HttpSession httpSession, PmProject pmProject, PmProjectTable pmProjectTable, BigDecimal[] detailValue, String[] detailName, String[] treeId, String[] detailId, String type, String applyFlag, String time, String isThrough) {
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = new Date();

            applyFlag = "add".equals(type) ? UUID.randomUUID().toString() : applyFlag; // 一次上报标识
            PmProjectPurpose pmProjectPurpose = new PmProjectPurpose();
            pmProjectPurpose.setApplyFlag(applyFlag);
            projectPurposeService.remove(new QueryWrapper<PmProjectPurpose>().setEntity(pmProjectPurpose));
            for (int i = 0; i < detailValue.length; i++) {
                PmProjectTableModel pmProjectTableModel = new PmProjectTableModel();

//                //String StrBd=detailValue[i]+"".replace(",","");
//                String StrBd="48,576.1024".replace(",","");
//                //BigDecimal iV=Integer.parseInt(v);
//
//                BigDecimal bd=new BigDecimal(StrBd);
//                //设置小数位数，第一个变量是小数位数，第二个变量是取舍方法(四舍五入)
//                bd=bd.setScale(2, BigDecimal.ROUND_HALF_UP);
//
//                System.out.println(detailValue[i]+"".replace(",","")+"========================");
                pmProjectTableModel.setDetailName(detailName[i]);
                pmProjectTableModel.setDetailValue(detailValue[i]);
//              pmProjectTableModel.setDetailValue(bd);
                pmProjectTableModel.setTreeId(treeId[i]);
                pmProjectTableModel.setProjectId(pmProject.getProjectId());
                pmProjectTableModel.setOrganId(pmProject.getOrganId());
                pmProjectTableModel.setUserId(user.getUserId());
                pmProjectTableModel.setApplyFlag(applyFlag);
                pmProjectTableModel.setCreateTime2(date);

                String[] timeShus = time.split("~");
                pmProjectTableModel.setCreateTime(simple.parse(timeShus[0]));
                pmProjectTableModel.setEndTime(simple.parse(timeShus[1]));

                if ("add".equals(type)) {
                    pmProjectTableModel.setIsThrough(1);
                    pmProjectTableModel.setDetailId(UUID.randomUUID().toString());

                    projectTableService.save(pmProjectTable.getTableName(), pmProjectTableModel);
                } else {
                    if("-1".equals(isThrough)){
                        pmProjectTableModel.setIsThrough(1);
                    }else{
                        pmProjectTableModel.setIsThrough(Integer.parseInt(isThrough));
                    }
                    pmProjectTableModel.setDetailId(detailId[i]);

                    projectTableService.update(pmProjectTable.getTableName(), pmProjectTableModel);

                    if (i == 0 && "-1".equals(isThrough)) {
                        List<String> procId = activitiService.getProcId(pmProjectTableModel.getApplyFlag()); // 获取流程ID
                        activitiService.removeProcHi(procId); // 删除历史流程
                    }

                }

                // 设置添加明细项
                if (request.getParameterValues(treeId[i] + "dictId") != null) {
                    String[] dictShus = request.getParameterValues(treeId[i] + "dictId");
                    String[] values = request.getParameterValues(treeId[i] + "value");


                    for (int j = 0; j < dictShus.length; j++) {
                        pmProjectPurpose.setDictId(Integer.valueOf(dictShus[j]));
                        pmProjectPurpose.setDetailId(pmProjectTableModel.getDetailId());
                        pmProjectPurpose.setValue(new BigDecimal(values[j]));
                        pmProjectPurpose.setTreeId(treeId[i]);
                        pmProjectPurpose.setApplyFlag(applyFlag);
                        projectPurposeService.save(pmProjectPurpose);
                    }
                }
            }
            return Result.success(ResponseMsg.INSERT_UPDATE_SUCCESS);
        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_SERVICE_VALIDATOR, ResponseMsg.INSERT_UPDATE_ERROR);
        }
    }

    @GetMapping("/checkDictPage")
    public String checkDict(String dictId, Model model, String treeId,String organId) {
        model.addAttribute("dictId", dictId);
        model.addAttribute("treeId", treeId);
        model.addAttribute("organId", organId);
        return "projectInsert/checkDict";
    }

    @ResponseBody
    @PostMapping("/del")
    public Result del(PmProject pmProject, PmProjectTableModel pmProjectTableModel) {
        try {
            QueryWrapper<PmProjectTable> projectTableWrapper = new QueryWrapper<>();
            projectTableWrapper.eq("project_id", pmProject.getProjectId());
            PmProjectTable projectTable = projectTableService.getOne(projectTableWrapper);
            projectTableService.del(projectTable, pmProjectTableModel.getApplyFlag());
            List<String> procId = activitiService.getProcId(pmProjectTableModel.getApplyFlag()); // 获取流程ID
            activitiService.removeProcHi(procId); // 删除历史流程
            return Result.success(ResponseMsg.DELETE_SUCCESS);
        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_999, ResponseMsg.DELETE_ERROR);
        }

    }

    //注解时间类型
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        //转换日期
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));// CustomDateEditor为自定义日期编辑器

    }
    @ResponseBody
    @PostMapping("/returns")
    public Result returns(PmProject pmProject, PmProjectTableModel pmProjectTableModel){

        pmProject = projectService.getById(pmProject.getProjectId());

        QueryWrapper<PmProjectTable> projectTableWrapper = new QueryWrapper<>();
        projectTableWrapper.eq("project_id", pmProject.getProjectId());
        PmProjectTable projectTable = projectTableService.getOne(projectTableWrapper);

    //   String tableName="pm_project_detail_hljzghyyxm";
           String tableName=projectTable.getTableName();
            System.out.print("表名是=======================>:"+tableName);
        projectTableService.setIsThrow(tableName,pmProjectTableModel.getApplyFlag(),-1);// 审批未通过
        return Result.success("成功！");
    }
}
