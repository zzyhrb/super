package com.newgrand.superviseanalysis.daily.workFlow;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.newgrand.superviseanalysis.daily.workFlow.service.ActivitiService;
import org.activiti.engine.ActivitiException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

/**
 * @author tly
 * @version 1.0.0
 * @ClassName ActivitiController.java
 * @Description TODO
 * @createTime 2020年04月03日 10:43:00
 */
@Controller
@RequestMapping("activiti")

public class ActivitiController {
    @Autowired
    private ActivitiService activitiService;

    /**
     * @throws
     * @title 工作流加载模板数据
     * @description
     * @author admin
     * @updateTime 2020/4/3 10:59
     */
    @ResponseBody
    @GetMapping("/model/{modelId}/json")
    public ObjectNode getEditorJson(@PathVariable String modelId) {
        return activitiService.getEditorJson(modelId);
    }

    /**
     * @throws
     * @title 工作流加载菜单工具栏
     * @description
     * @author admin
     * @updateTime 2020/4/3 10:59
     */
    @ResponseBody
    @GetMapping("/editor/stencilset")
    public String getStencilset() {
        InputStream stencilsetStream = this.getClass().getClassLoader().getResourceAsStream("stencilset.json");
        try {
            assert stencilsetStream != null;
            return IOUtils.toString(stencilsetStream, "utf-8");
        } catch (Exception e) {
            throw new ActivitiException("Error while loading stencil set", e);
        }
    }

    /**
     * @throws
     * @title 工作流保存模型
     * @description
     * @author admin
     * @updateTime 2020/4/3 11:11
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/model/{modelId}/save")
    public void saveActiviti(@PathVariable String modelId, String name, String description, String json_xml, String svg_xml) {
        try {
            activitiService.saveActiviti(modelId, name, description, json_xml, svg_xml);
        } catch (Exception e) {
        }
    }
}
