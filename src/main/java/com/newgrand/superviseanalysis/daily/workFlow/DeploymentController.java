/**
 * @Title DeploymentController.java
 * @Package com.ry.oa.activiti.controller
 * @Description TODO(部署流程控制器源文件)
 * @author 王雷
 * @date 2019年4月16日 上午10:42:48
 * @Copyright 2019 www.hrbhechuagnekeji.com Inc. All rights reserved.
 * <p>注意：本内容仅限于黑龙江瑞银能源工程有限公司内部传阅，禁止外泄以及用于其他的商业目的。</p>
 */
package com.newgrand.superviseanalysis.daily.workFlow;

import com.newgrand.superviseanalysis.system.project.service.IProjectTableService;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import com.newgrand.superviseanalysis.utils.ResponseCode;
import com.newgrand.superviseanalysis.utils.ResponseMsg;
import com.newgrand.superviseanalysis.utils.Result;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @author 王雷
 * @ClassName DeploymentController
 * @Package com.ry.oa.activiti.controller
 * @Description TODO(部署流程控制器)
 * @date 2019年4月16日 上午10:42:48
 * @Copyright 2019 www.hrbhechuagnekeji.com Inc. All rights reserved.
 * <p>注意：本内容仅限于黑龙江瑞银能源工程有限公司内部传阅，禁止外泄以及用于其他的商业目的。</p>
 */
@Controller
@RequestMapping("/activity-deployment")
public class DeploymentController {


    /**
     * @FieldType RepositoryService
     * @Fields repositoryService : TODO(部署服务)
     */
    @Autowired
    private RepositoryService repositoryService;
    /**
     * @FieldType RuntimeService
     * @Fields runtimeService : TODO(工作流运行服务)
     */
    @Autowired
    private RuntimeService runtimeService;
    /**
     * @FieldType IdentityService
     * @Fields identityService : TODO(用户，用户组服务。)
     */
    @Autowired
    private IdentityService identityService;

    @Autowired
    private IProjectTableService projectTableService;

    @RequestMapping("/page")
    public String list(Model model) {
        return "activiti/deployment/list";
    }

    /**
     * @param model
     * @return /activiti-deployment/list
     * @Title list
     * @Description TODO(部署流程列表)
     * @author 王雷
     * @date 2019年4月16日 下午1:11:01
     */
    @ResponseBody
    @RequestMapping("/list")
    public Result list() {
        List<Map<String, Object>> datalist = new ArrayList<>();// 返回数据集合
        // 获取已部署流程列表
        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery().orderByDeploymenTime();
        List<Deployment> deploymentList = deploymentQuery.orderByDeploymenTime().desc().list();
        for (Deployment deployment : deploymentList) {
            Map<String, Object> map = new HashMap<>();
            map.put("deploymentName", deployment.getName());
            // 根据部署ID查询流程部署信息
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
            map.put("deploymentId", processDefinition.getDeploymentId());
            map.put("id", processDefinition.getId());
            map.put("name", processDefinition.getName());

            datalist.add(map);
        }
        return Result.success(datalist);
    }

    /**
     * @param model
     * @param deploymentId 部署ID
     * @return /activiti/deployment/showModel
     * @Title loadActivitiPictureModel
     * @Description TODO(加载图片模型)
     * @author 王雷
     * @date 2019年4月16日 下午1:12:05
     */
    @RequestMapping("/loadActivitiPictureModel")
    public String loadActivitiPictureModel(Model model, String deploymentId) {
        model.addAttribute("deploymentId", deploymentId);
        return "/activiti/deployment/showModel";
    }

    /**
     * @param deploymentId 部署ID
     * @param response
     * @throws IOException
     * @Title showDeploymentPicture
     * @Description TODO(获取图片)
     * @author 王雷
     * @date 2019年4月16日 下午1:12:47
     */
    @RequestMapping("/showDeploymentPicture")
    public void showDeploymentPicture(String deploymentId, HttpServletResponse response) throws IOException {
        // 根据流程ID从仓库中获取流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        // 获取资源图片名称
        String diagramResourceName = processDefinition.getDiagramResourceName();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        ProcessDiagramGenerator p = new DefaultProcessDiagramGenerator();
        InputStream in = p.generateDiagram(bpmnModel, "png", Collections.<String>emptyList(), Collections.<String>emptyList(), "宋体", "宋体", null,
                null, 1.0);
        OutputStream out = response.getOutputStream();
        byte[] b = new byte[1024];
        int i = 0;
        while ((i = in.read(b)) != -1) {
            out.write(b, 0, i);
        }
        // 关闭流
        out.close();
        in.close();
    }

    /**
     * @param deploymentId
     * @return
     * @Title deleteDeployment
     * @Description TODO(删除流程部署)
     * @author 王雷
     * @date 2019年4月16日 下午1:14:57
     */
    @ResponseBody
    @RequestMapping("/removeDeployment")
    public Result removeDeployment(String deploymentId) {
        try {
            // 普通删除，如果当前规则下有正在执行的流程，则抛异常
            repositoryService.deleteDeployment(deploymentId);
        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_SERVICE_VALIDATOR, ResponseMsg.DELETE_ERROR);
        }

        return Result.success(ResponseMsg.DELETE_SUCCESS);
    }


    @RequestMapping(value = "/queryPage", method = RequestMethod.GET)
    public String queryPage(String deploymentId, Model model) {
        model.addAttribute("deploymentId", deploymentId);
        return "activiti/deployment/showModal";
    }


    @RequestMapping(value = "/applyPage", method = RequestMethod.GET)
    public String applyPage(String deploymentId, HttpSession httpSession, Model model) {
        model.addAttribute("deploymentId", deploymentId);
        return "activiti/deployment/applyPage";
    }

    @ResponseBody
    @PostMapping(value = "/applyList")
    public Result applyList(HttpSession httpSession, Model model) {
        try {
            SysUser user = (SysUser) httpSession.getAttribute("userInfo");
            // 获取单位下所属项目表名
            List<String> tableNames = projectTableService.selectTableNamesByOrganId(user.getOrganId());
            List<Map<String, Object>> applyList = projectTableService.getApplyList(tableNames, user);
            return Result.success(applyList);
        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_999, e.toString());
        }
    }

    /**
     * @param deploymentId 部署ID
     * @return
     * @Title applyList
     * @Description TODO(启动流程)
     * @author 王雷
     * @date 2019年4月16日 下午2:48:32
     */
    @ResponseBody
    @RequestMapping("/startDeployment")
    public Result startDeployment(HttpServletRequest request, String deploymentId, String applyFlag, String tableName, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("userInfo");

        // 获取流程管理
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        // 设置开始人员
        identityService.setAuthenticatedUserId(String.valueOf(user.getUserId()));
        // 动态设置任务执行人员
        Map<String, Object> variables = new HashMap<>();

        variables.put("user", user.getUserId()); // 设置上报人ID
        variables.put("organ", user.getOrganId()); // 设置上报人组织
        variables.put("applyFlag", applyFlag); // 设置上报标识
        variables.put("tableName", tableName); // 设置项目表名

        ProcessInstance instance = runtimeService.startProcessInstanceById(processDefinition.getId(), variables);
        projectTableService.setIsThrow(tableName,applyFlag,2);// 审批中

        return Result.success("上报成功！");
    }

}
