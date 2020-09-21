/**
 * @Title ManagementController.java
 * @Package com.ry.oa.activiti.controller
 * @Description TODO(工作流管理控制器源文件)
 * @author 王雷
 * @date 2019年4月8日 下午4:57:51
 * @Copyright 2019 www.hrbhechuagnekeji.com Inc. All rights reserved.
 * <p>注意：本内容仅限于黑龙江瑞银能源工程有限公司内部传阅，禁止外泄以及用于其他的商业目的。</p>
 */
package com.newgrand.superviseanalysis.daily.workFlow;


import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.newgrand.superviseanalysis.utils.ResponseCode;
import com.newgrand.superviseanalysis.utils.ResponseMsg;
import com.newgrand.superviseanalysis.utils.Result;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ModelQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/activity-model")
public class ManagementController {


    /**
     * @FieldType RepositoryService
     * @Fields repositoryService : TODO(部署服务)
     */
    @Autowired
    private RepositoryService repositoryService;


    /**
     * @Title list
     * @Description TODO(流程列表)
     *     <p>只查询当前登录用户创建的流程</p>
     * @author 王雷
     * @date 2019年4月9日 上午9:23:12
     * 
     * @return /activiti/management/list
     */
    @RequestMapping("/page")
    public String list(Model model) {
        return "activiti/management/list";
    }


    @ResponseBody
    @RequestMapping("/list")
    public Result list(HttpServletRequest request){
        ModelQuery modelQuery = repositoryService.createModelQuery();
        List<org.activiti.engine.repository.Model> models = modelQuery.list();
        return Result.success(models);
    }

    /**
     * @Title loadAddOrEdit
     * @Description TODO(加载添加/修改页)
     * @author 王雷
     * @date 2019年4月17日 上午8:55:54
     * 
     * @param model
     * @param flag 标记
     * @param modelId 模型ID
     * @return
     */
    @RequestMapping("/addOrEditPage")
    public String loadAddOrEdit(Model model, 
            @RequestParam(required = true, defaultValue = "0") String type,
            @RequestParam(required = false) String modelId) {
        if ("edit".equals(type)) {
            org.activiti.engine.repository.Model modelData = repositoryService.getModel(modelId);
            String name = modelData.getName();// 获取模型名称
            String key = modelData.getKey();// 获取模型关键字
            Map<?, ?> map = (Map<?, ?>) JSONObject.parse(modelData.getMetaInfo());// 获取模型元信息
            String description = map.get("description").toString();// 获取模型描述
            model.addAttribute("name", name);
            model.addAttribute("key", key);
            model.addAttribute("description", description);

        }
        
        model.addAttribute("type", type);
        
        return "activiti/management/addOrEdit";
    }
    
    /**
     * @Title save
     * @Description TODO(保存工作流程)
     * @author 王雷
     * @date 2019年4月9日 上午10:32:32
     * 
     * @return
     * @throws IOException 
     */
    @SuppressWarnings("deprecation")
    @ResponseBody
    @RequestMapping(value = "/addOrEdit", method = RequestMethod.POST)
    public Result save(
            @RequestParam(required = true) String name, 
            @RequestParam(required = true) String key, 
            @RequestParam(required = true) String description, 
            HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        org.activiti.engine.repository.Model modelData = repositoryService.newModel();
        
        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        description = StringUtils.defaultString(description);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelData.setMetaInfo(modelObjectNode.toString());
        modelData.setName(name);
        modelData.setKey(StringUtils.defaultString(key));

        repositoryService.saveModel(modelData);
        repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
        
        return Result.success(ResponseMsg.INSERT_UPDATE_SUCCESS);
    }
    
    /**
     * @Title loadEditor
     * @Description TODO(跳转流程编辑器)
     * @author 王雷
     * @date 2019年4月16日 上午9:45:07
     * 
     * @param id 流程ID
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "/editor/{id}", method = RequestMethod.GET)
    public void loadEditor(@PathVariable(value = "id") String id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + id + "&ctx=");
    }
    
    /**
     * 
     * @Title modal
     * @Description TODO(流程部署弹出层)
     * @author 田霖禹
     * @date 2020年3月18日 上午8:02:12
     * 
     * @param model
     * @param modelId
     * @return
     */
	@RequestMapping("/modal")
	public String modal(Model model, String modelId) {
		model.addAttribute("modelId",modelId);
		return "activiti/management/modal";
	}
	
    
    /**
     * @Title deployProcessEngine
     * @Description TODO(部署模型)
     * @author 王雷
     * @date 2019年4月25日 下午3:00:58
     * 
     * @param modelId 模型ID
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    @RequestMapping("/deployProcessEngine")
    @ResponseBody
    public Result deployProcessEngine(HttpSession session,@RequestParam(required = true) String modelId) throws JsonProcessingException, IOException {
        try {
            // 根据模型ID获取模型实例
            org.activiti.engine.repository.Model modelData = repositoryService.getModel(modelId);
            ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
            byte[] bpmnBytes = null;
            
            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            bpmnBytes = new BpmnXMLConverter().convertToXML(model);
            String processName = modelData.getName() + ".bpmn";
            // 部署流程定义
            Deployment deployment = repositoryService.createDeployment().name(modelData.getName())
                    .category(modelData.getCategory()).addString(processName, new String(bpmnBytes, "UTF-8")).deploy();
            // 将部署ID保存到模型Model表中
            modelData.setDeploymentId(deployment.getId());
            repositoryService.saveModel(modelData);
        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_999,"部署失败！");
        }

        return Result.success("部署成功！");

    }
    
    /**
     * @Title removeModel
     * @Description TODO(删除流程定义)
     * @author 王雷
     * @date 2019年4月17日 上午8:41:28
     * 
     * @param modelId 模型ID
     * @return ok/no
     */
    @ResponseBody
    @RequestMapping("/removeModel")
    public Result removeModel(String modelId) {
        try {
            repositoryService.deleteModel(modelId);
        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_999,ResponseMsg.DELETE_ERROR);
        }
        return Result.success(ResponseMsg.DELETE_SUCCESS);
    }
    
}
