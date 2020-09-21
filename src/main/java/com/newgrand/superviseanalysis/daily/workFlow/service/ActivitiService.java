package com.newgrand.superviseanalysis.daily.workFlow.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.repository.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.activiti.editor.constants.ModelDataJsonConstants.MODEL_ID;
import static org.activiti.editor.constants.ModelDataJsonConstants.MODEL_NAME;
import static org.activiti.editor.constants.ModelDataJsonConstants.MODEL_DESCRIPTION;

@Service
@Transactional
public class ActivitiService {
    /**
     * 运行中的流程业务
     */
    private RuntimeService runtimeService;
    /**
     * 任务业务
     */
    private TaskService taskService;
    /**
     * 历史业务
     */
    private HistoryService historyService;
    /**
     * 部署服务
     */
    private RepositoryService repositoryService;

    /**
     * 构造器注入
     * @param runtimeService 运行中的流程业务
     * @param taskService 任务业务
     * @param historyService 历史业务
     * @param repositoryService 部署服务
     */
    @Autowired
    public ActivitiService(RuntimeService runtimeService, TaskService taskService, HistoryService historyService, RepositoryService repositoryService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
        this.repositoryService = repositoryService;
    }

    /**
     * 获取工作流程列表
     * @return List<Model> modelList
     */
    public List<Model> getModelList() {
        ModelQuery modelQuery = repositoryService.createModelQuery();
        return modelQuery.list();
    }

    /**
     * 获取流程详情
     * @param id 流程ID
     * @return Map<String, Object> details
     */
    public Model getModelDetails(String id) {
        return repositoryService.getModel(id);
    }

    /**
     * 获取模型编辑器流
     * @param id 模型ID
     * @return byte[]
     */
    public byte[] getModelEditor(String id) {
        return repositoryService.getModelEditorSource(id);
    }

    /**
     *
     * 保存一个默认空流程
     * @param key 流程索引
     * @param name 流程名称
     * @param description 流程描述
     * @return String modelId 流程ID
     */
    public String saveModel(String key, String name, String description) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        Model modelData = repositoryService.newModel();

        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put(MODEL_NAME, name);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, key);
        description = StringUtils.defaultString(description);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelData.setMetaInfo(modelObjectNode.toString());
        modelData.setName(name);
        modelData.setKey(StringUtils.defaultString(key));

        repositoryService.saveModel(modelData);
        repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes(StandardCharsets.UTF_8));

        return modelData.getId();
    }

    /**
     * 删除流程
     * @param modelId 流程ID
     * @return boolean
     * @throws Exception 向上一级抛出异常
     */
    public boolean deleteModel(String modelId) throws Exception {
        try {
            Model modelData = repositoryService.getModel(modelId);
            if(null != modelData){
                ProcessInstance pi = runtimeService.createProcessInstanceQuery().processDefinitionKey(modelData.getKey()).singleResult();
                if(null != pi) {
                    runtimeService.deleteProcessInstance(pi.getId(), "");
                    historyService.deleteHistoricProcessInstance(pi.getId());
                }
                repositoryService.deleteModel(modelId);
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            throw new Exception();
        }
    }

    /**
     * 获取流程JSON信息
     * @param modelId 流程ID
     * @return ObjectNode 流程节点xml对象
     */
    public ObjectNode getEditorJson(String modelId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode modelNode = null;
        Model model = repositoryService.getModel(modelId);
        if (model != null) {
            try {
                if (StringUtils.isNotEmpty(model.getMetaInfo())) {
                    modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
                } else {
                    modelNode = objectMapper.createObjectNode();
                    modelNode.put(MODEL_NAME, model.getName());
                }
                modelNode.put(MODEL_ID, model.getId());
                ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(
                        new String(repositoryService.getModelEditorSource(model.getId()), "utf-8"));
                modelNode.put("model", editorJsonNode);
            } catch (Exception e) {
                throw new ActivitiException("Error creating model JSON", e);
            }
        }
        return modelNode;
    }

    /**
     * 保存编辑器流程
     * @param modelId 流程ID
     * @param name 流程名称
     * @param description 流程描述
     * @param json_xml 流程json信息
     * @param svg_xml 流程xml信息
     */
    public void saveActiviti(String modelId, String name, String description, String json_xml, String svg_xml) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Model model = repositoryService.getModel(modelId);

            ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());

            modelJson.put(MODEL_NAME, name);
            modelJson.put(MODEL_DESCRIPTION, description);
            model.setMetaInfo(modelJson.toString());
            model.setName(name);
            repositoryService.saveModel(model);

            repositoryService.addModelEditorSource(model.getId(), json_xml.getBytes(StandardCharsets.UTF_8));

            InputStream svgStream = new ByteArrayInputStream(svg_xml.getBytes(StandardCharsets.UTF_8));
            TranscoderInput input = new TranscoderInput(svgStream);

            PNGTranscoder transcoder = new PNGTranscoder();
            // Setup output
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outStream);

            // Do the transformation
            transcoder.transcode(input, output);
            final byte[] result = outStream.toByteArray();
            repositoryService.addModelEditorSourceExtra(model.getId(), result);
            outStream.close();
        } catch (Exception e) {
            throw new ActivitiException("Error saving model", e);
        }
    }

    /**
     * 发布流程
     * @param modelId 流程ID
     * @return String null发布成功 not null发布失败
     * @throws Exception 发布异常
     */
    public String deployActiviti(String modelId) throws Exception {
        Model modelData = repositoryService.getModel(modelId);
        byte[] bytes = getModelEditor(modelId);
        if (null == bytes) {
            return "部署ID:" + modelId + "的模型数据为空，请先设计流程并成功保存，再进行发布";
        }
        JsonNode modelNode = new ObjectMapper().readTree(bytes);
        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        Deployment deployment = repositoryService.createDeployment()
                .name(modelData.getName())
                .addBpmnModel(modelData.getKey()+".bpmn", model)
                .deploy();
        modelData.setDeploymentId(deployment.getId());
        repositoryService.saveModel(modelData);
        return null;
    }

    public List<Map<String, Object>> deployList(String dept, String type) {
        List<Map<String, Object>> list = new ArrayList<>();
        DeploymentQuery deployQuery = repositoryService.createDeploymentQuery();
        deployQuery.orderByDeploymenTime().desc();
        ProcessDefinitionQuery processQuery = repositoryService.createProcessDefinitionQuery();
        List<Deployment> dataList = deployQuery.list();
        for (Deployment data : dataList) {
            if (null != dept && !"".equals(dept)) {
                processQuery = processQuery.processDefinitionKey(dept);
            }
            if (null != type && !"".equals(type)) {
                processQuery = processQuery.processDefinitionName(type);
            }
            ProcessDefinition processDefinition = processQuery.deploymentId(data.getId()).orderByDeploymentId().desc().singleResult();
            if (null != processDefinition) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", data.getId());
                map.put("name", data.getName());
                list.add(map);
            }
        }
        return list;
    }

    public void deployDelete(String modelId) throws Exception {
        try {
            repositoryService.deleteDeployment(modelId);
        } catch (Exception e) {
            throw new Exception();
        }
    }
}
