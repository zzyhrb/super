package com.newgrand.superviseanalysis.system.organ.controller;


import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newgrand.superviseanalysis.system.organ.entity.SysOrgan;
import com.newgrand.superviseanalysis.system.organ.entity.SysOrganType;
import com.newgrand.superviseanalysis.system.organ.service.IOrganService;
import com.newgrand.superviseanalysis.system.organ.service.IOrganTypeService;
import com.newgrand.superviseanalysis.system.organ.vo.OrganTypeVO;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import com.newgrand.superviseanalysis.utils.ResponseCode;
import com.newgrand.superviseanalysis.utils.ResponseMsg;
import com.newgrand.superviseanalysis.utils.Result;
import com.newgrand.superviseanalysis.utils.TreeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */
@Controller
@RequestMapping("/organ")
public class OrganController {
    // 组织服务
    private IOrganService organService;

    // 组织类型服务
    private IOrganTypeService organTypeService;

    @Autowired
    public OrganController(IOrganTypeService organTypeService,IOrganService organService ) {
        this.organTypeService = organTypeService;
        this.organService = organService;
    }

    @RequestMapping("/page")
    public String Page(HttpServletRequest request){
        return "organ/list";
    }

    @ResponseBody
    @RequestMapping("/tree")
    public Result tree(HttpServletRequest request,HttpSession httpSession){
        SysOrgan organ = (SysOrgan) httpSession.getAttribute("organ");
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");

        QueryWrapper<SysOrgan> queryWrapper = new QueryWrapper();
        queryWrapper.select("organ_id","organ_name","parent_id").and(i -> i.eq("is_delete",0).eq("organ_id",user.getOrganId()));
        queryWrapper.or(i -> i.likeRight("parent_id",user.getOrganId()).eq("is_delete",0));

        queryWrapper.orderByAsc("organ_order");
        List<Map<String, Object>> list = organService.listMaps(queryWrapper);

        JSONArray treeJson = TreeUtils.treeRecursionDataList(list, BigInteger.valueOf(Long.parseLong(organ.getParentId())),"organ_id","organ_name","parent_id", null);
        return Result.success(treeJson);
    }

    @ResponseBody
    @RequestMapping("/detail")
    public Result detail(String organId){
        OrganTypeVO organ = organService.getOrganTypeById(organId);
        return Result.success(organ);
    }

    @RequestMapping("/addOrEditPage")
    public ModelAndView addOrEditPage( HttpServletRequest request, String type, SysOrgan sysOrgan){
        Map<String, Object> map = new HashMap<>();
        if("add".equals(type)){
            map.put("parentId",sysOrgan.getParentId());

        }else{
            sysOrgan = organService.getById(sysOrgan.getOrganId());
            map.put("organId",sysOrgan.getOrganId());
            map.put("parentId",sysOrgan.getParentId());
            map.put("typeId",sysOrgan.getTypeId());
            map.put("sysOrgan",sysOrgan);

        }
        QueryWrapper<SysOrganType> queryWrapper = new QueryWrapper();
        SysOrganType sysOrganType = new SysOrganType();
        queryWrapper.setEntity(sysOrganType);
        map.put("typeList",organTypeService.list(queryWrapper));
        map.put("type",type);
        return new ModelAndView("organ/addOrEdit",map);
    }

    @ResponseBody
    @RequestMapping("/addOrEdit")
    public Result addOrEdit(HttpSession httpSession,HttpServletRequest request, SysOrgan sysOrgan, String type){
        SysUser user = (SysUser)httpSession.getAttribute("userInfo");
        try {
            if ("add".equals(type)){
                String treeId = organService.getTreeId(sysOrgan.getParentId());
                sysOrgan.setOrganId(treeId);
                sysOrgan.setCreateTime(new Date());
                sysOrgan.setUserId(user.getUserId());
                organService.save(sysOrgan);
            } else {
                organService.saveOrUpdate(sysOrgan);
            }
            return Result.success(ResponseMsg.INSERT_UPDATE_SUCCESS);
        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_999,ResponseMsg.INSERT_UPDATE_ERROR);
        }
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Result delete(HttpServletRequest request,SysOrgan organ){
        try {
            UpdateWrapper<SysOrgan> updateWrapper = new UpdateWrapper();
            updateWrapper.set("is_delete",-1);
            updateWrapper.eq("organ_id",organ.getOrganId());
            updateWrapper.or(i -> i.eq("parent_id",organ.getOrganId()));
            organService.update(updateWrapper);
            return Result.success(ResponseMsg.DELETE_SUCCESS);
        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_999,ResponseMsg.DELETE_ERROR);
        }
    }

}
