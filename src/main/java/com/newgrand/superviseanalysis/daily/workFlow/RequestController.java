/**
 * @Title RequestController.java
 * @Package com.ry.oa.activiti.controller
 * @Description TODO(流程任务控制器源文件)
 * @author 王雷
 * @date 2019年4月8日 下午4:58:05
 * @Copyright 2019 www.hrbhechuagnekeji.com Inc. All rights reserved.
 * <p>注意：本内容仅限于黑龙江瑞银能源工程有限公司内部传阅，禁止外泄以及用于其他的商业目的。</p>
 */
package com.newgrand.superviseanalysis.daily.workFlow;

import com.newgrand.superviseanalysis.daily.workFlow.service.IActivitiService;
import com.newgrand.superviseanalysis.system.dict.service.IDictService;
import com.newgrand.superviseanalysis.system.project.entity.PmProject;
import com.newgrand.superviseanalysis.system.project.service.IProjectPurposeService;
import com.newgrand.superviseanalysis.system.project.service.IProjectTableService;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import com.newgrand.superviseanalysis.system.user.service.IUserService;
import com.newgrand.superviseanalysis.utils.Result;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author 王雷
 * @ClassName RequestController
 * @Package com.ry.oa.activiti.controller
 * @Description TODO(流程任务控制器)
 * @date 2019年4月8日 下午4:58:05
 * @Copyright 2019 www.hrbhechuagnekeji.com Inc. All rights reserved.
 * <p>注意：本内容仅限于黑龙江瑞银能源工程有限公司内部传阅，禁止外泄以及用于其他的商业目的。</p>
 */
@Controller
@RequestMapping("/activity-request")
public class RequestController {

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);


    /**
     * @FieldType TaskService
     * @Fields taskService : TODO(任务服务)
     */
    @Autowired
    private TaskService taskService;
    /**
     * @FieldType FormService
     * @Fields formService : TODO(表单服务)
     */
    @Autowired
    private FormService formService;
    /**
     * @FieldType RuntimeService
     * @Fields runtimeService : TODO(运行服务)
     */
    @Autowired
    private RuntimeService runtimeService;
    /**
     * @FieldType HistoryService
     * @Fields historyService : TODO(历史记录服务)
     */
    @Autowired
    private HistoryService historyService;

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private IDictService dictService;

    @Autowired
    private IProjectTableService projectTableService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IProjectPurposeService projectPurposeService;

    /**
     *
     * @param model
     * @return
     * @Title list
     * @Description TODO(任务处理列表)
     * @author 王雷
     * @date 2019年4月16日 下午2:01:30
     */
    @RequestMapping("/page")
    public String page(Model model, HttpSession session) {
        return "activiti/request/list";
    }

    @ResponseBody
    @PostMapping("/list")
    public Result list(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("userInfo");
        TaskQuery tasksQuery = taskService.createTaskQuery();
        List<Task> tasks = tasksQuery
                .taskCandidateUser(String.valueOf(user.getRoleId()))
                .or()
                .taskInvolvedUser(String.valueOf(user.getOrganId()))
                .active()
                .orderByTaskDueDate().desc()
                .list();
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Task task : tasks) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", task.getName());
            map.put("taskId", task.getId());
            map.put("processInstanceId", task.getProcessInstanceId());
            map.put("createTime", task.getCreateTime());

            if (task.getName() != null && task.getName().startsWith("当前单位")) {
                List<Map<String, Object>> approvalList = activitiService.pendingApproval(task, user);
                if (approvalList.get(0) != null && !user.getOrganId().equals(approvalList.get(0).get("VALUE_"))) {
                    continue;
                } else {
                    map.put("applyFlag", approvalList.get(1).get("VALUE_"));
                    map.put("tableName", approvalList.get(2).get("VALUE_"));
                    Map<String,Object> organMap = activitiService.getOrganProjectDate(map);
                    map.put("projectName", organMap.get("project_name"));
                    map.put("organName", organMap.get("organ_name"));
                    map.put("date", organMap.get("date"));

                }

            }else if(task.getName().startsWith("省")){
                List<Map<String, Object>> approvalList = activitiService.pendingApproval(task, user);
                map.put("applyFlag", approvalList.get(1).get("VALUE_"));
                map.put("tableName", approvalList.get(2).get("VALUE_"));
                Map<String,Object> organMap = activitiService.getOrganProjectDate(map);
                map.put("projectName", organMap.get("project_name"));
                map.put("organName", organMap.get("organ_name"));
                map.put("date", organMap.get("date"));
            }

            dataList.add(map);

        }

        return Result.success(dataList);
    }


    /**
     * @param model
     * @param taskId 任务ID
     * @return
     * @Title loadForm
     * @Description TODO(加载任务表单)
     * @author 王雷
     * @date 2019年4月16日 下午3:59:23
     */
    @RequestMapping("/loadForm")
    public String loadForm(Model model, String taskId, String processInstanceId, String applyFlag, String tableName) {
        // 获取任务列表
        TaskFormData taskFormData = formService.getTaskFormData(taskId);
        List<FormProperty> dataList = taskFormData.getFormProperties();
        // 获取附件
        // 获取部署对象，上报人。
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (null != processInstance) {

            model.addAttribute("user", userService.getById(processInstance.getStartUserId()));
        }
        model.addAttribute("taskId", taskId);
        model.addAttribute("data", processInstance);
        model.addAttribute("dataList", dataList);
        model.addAttribute("applyFlag", applyFlag);
        model.addAttribute("tableName", tableName);

        return "activiti/request/showModal";
    }

    /**
     * @title 跳转任务详情页面
     * @description
     * @author admin
     * @updateTime 2020/4/20 15:48
     * @throws
     */
    @RequestMapping("/detail")
    public String detail(Model model,String applyFlag, String tableName) {
        PmProject project = activitiService.getProjectId(tableName, applyFlag);
        List<Map<String,Object> >tableList = activitiService.getDetail(tableName, applyFlag, project.getProjectId());
        model.addAttribute("tableList",tableList);
        model.addAttribute("project",project);

        return "activiti/request/detail";
    }

    /**
     * @title 跳转详细详情页面
     * @description
     * @author admin
     * @updateTime 2020/4/20 15:48
     * @throws
     */
    @RequestMapping("/dictDetail/page")
    public String dictDetailPage(Model model,String detailId) {
        model.addAttribute("detailId", detailId);
        return "activiti/request/dictDetail";
    }

    /**
     * @title 跳转详细详情页面
     * @description
     * @author admin
     * @updateTime 2020/4/20 15:48
     * @throws
     */
    @ResponseBody
    @RequestMapping("/dictDetail/list")
    public Result dictDetailList(Model model,String detailId) {
        List<Map<String,Object>> list = projectPurposeService.getlistByDetailId(detailId);
        return Result.success(list);
    }

    /**
     * @Title reloadForm
     * @Description TODO(完成任务)
     * @author 王雷
     * @date 2019年4月16日 下午3:59:15
     *
     * @param taskId 任务ID
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/reloadForm", method = RequestMethod.POST)
    public Result reloadForm(HttpServletRequest request,String taskId,String tableName,String applyFlag) {

        // 设置表单信息
        TaskFormData taskFormData = formService.getTaskFormData(taskId);
        List<FormProperty> dataList = taskFormData.getFormProperties();
        Map<String, Object> values = new HashMap<String, Object>();
        for (FormProperty formProperty : dataList) {
            Object value = request.getParameter(formProperty.getId());
            values.put(formProperty.getId(), value);
        }

        // 创建任务查询，获取流程实例ID，避免任务结束后获取不到，所以在完成任务之前获取。
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();

        // 完成任务
        taskService.complete(taskId, values);

        //使用流程实例ID查询
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (null == processInstance) {
            logger.info("流程结束 -> " + processInstanceId);
            // 根据流程实例ID获取流程历史结束节点
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).finished().singleResult();
            // 获取结束节点ID
            @SuppressWarnings("deprecation")
            String endActivityId = historicProcessInstance.getEndActivityId();

            if ("endSuccessEvent".equals(endActivityId)) {
                logger.info("审批通过");
                projectTableService.setIsThrow(tableName,applyFlag,0);// 审批通过

                // 修改流程实例状态
//                historicProcessInstance.setLocalizedName("已通过");
            } else {
                projectTableService.setIsThrow(tableName,applyFlag,-1);// 审批未通过


                logger.info("审批不通过");
                // 修改流程实例状态
//                historicProcessInstance.setLocalizedName("未通过");
            }
        } else {
            logger.info("流程未结束");
        }

        return Result.success("成功！");
    }

}
