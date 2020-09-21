/**
 * @Title ResponseController.java
 * @Package com.ry.oa.activiti.controller
 * @Description TODO(流程审批控制器源文件)
 * @author 王雷
 * @date 2019年4月8日 下午4:58:15
 * @Copyright 2019 www.hrbhechuagnekeji.com Inc. All rights reserved.
 * <p>注意：本内容仅限于黑龙江瑞银能源工程有限公司内部传阅，禁止外泄以及用于其他的商业目的。</p>
 */
package com.newgrand.superviseanalysis.daily.workFlow;

import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import com.newgrand.superviseanalysis.utils.Result;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @ClassName ResponseController
 * @Package com.ry.oa.activiti.controller
 * @Description TODO(流程审批控制器)
 * @author 王雷
 * @date 2019年4月8日 下午4:58:15
 * @Copyright 2019 www.hrbhechuagnekeji.com Inc. All rights reserved.
 * <p>注意：本内容仅限于黑龙江瑞银能源工程有限公司内部传阅，禁止外泄以及用于其他的商业目的。</p>
 *
 */
@Controller
@RequestMapping("/activity-response")
public class ResponseController {

    /**
     * @FieldType HistoryService
     * @Fields historyService : TODO(历史服务)
     */
    @Autowired
    private HistoryService historyService;
    /**
     * @FieldType IFileUploadService
     * @Fields fileUpload : TODO(文件上传服务)
     */

    /**
     * @param model
     * @return
     * @Title list
     * @Description TODO(任务处理列表)
     * @author 王雷
     * @date 2019年4月16日 下午2:01:30
     */
    @RequestMapping("/page")
    public String page(Model model, HttpSession session) {
        return "activiti/response/list";
    }
    
    /**
     * @Title list
     * @Description TODO(流程实例列表)
     * @author 王雷
     * @date 2019年4月18日 上午9:11:18
     * 
     * @param model
     * @return
     * @throws ParseException 
     */
    @ResponseBody
    @RequestMapping("/list")
    public Result list(Model model, HttpSession session, String content,String date,String state) throws ParseException {
        SysUser user = (SysUser) session.getAttribute("userInfo");
        String[] dateShus = date.split("~");
        //日期格式器
        SimpleDateFormat sdfm = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfm2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        historicProcessInstanceQuery.startedBy(String.valueOf(user.getUserId()));
        if (null != content && !"".equals(content)) {
            historicProcessInstanceQuery.processDefinitionName(content);
        }
        if (null != dateShus[0] && !"".equals(dateShus[0])) {
        	historicProcessInstanceQuery.startedAfter(sdfm.parse(dateShus[0]));
        }
        if (null != dateShus[1] && !"".equals(dateShus[1])) {
        	historicProcessInstanceQuery.startedBefore(sdfm2.parse(dateShus[1]+" 23:59:59"));
        }
        //已处理
        if ("1".equals(state)) {
            historicProcessInstanceQuery.finished();
		}
        //未处理
        if ("2".equals(state)) {
            historicProcessInstanceQuery.unfinished();

		}
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.orderByProcessInstanceStartTime().desc().list();
        

        return Result.success(historicProcessInstances);
    }

    @RequestMapping("/showDetailPage")
    public String showDetailPage(Model model, String id) {
        model.addAttribute("id",id);
        return "activiti/response/showDetail";
    }
    
    /**
     * @Title show
     * @Description TODO(审批详情)
     * @author 王雷
     * @date 2019年4月30日 下午1:27:53
     * 
     * @param model
     * @param id
     * @return /activiti/response/showModel
     */
    @ResponseBody
    @RequestMapping("/showDetails")
    public Result showDetails(Model model, String id) {
        // 文件查询对象
        List<HistoricActivityInstance> activityInstances = historyService.createHistoricActivityInstanceQuery().processInstanceId(id).orderByHistoricActivityInstanceId().asc().list();
        return Result.success(activityInstances);
    }
}
