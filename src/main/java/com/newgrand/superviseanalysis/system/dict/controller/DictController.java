package com.newgrand.superviseanalysis.system.dict.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newgrand.superviseanalysis.system.dict.entity.SysDict;
import com.newgrand.superviseanalysis.system.dict.service.IDictService;
import com.newgrand.superviseanalysis.system.dict.vo.DictOrganUserVo;
import com.newgrand.superviseanalysis.system.organ.entity.SysOrgan;
import com.newgrand.superviseanalysis.system.organ.service.IOrganService;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import com.newgrand.superviseanalysis.utils.ResponseCode;
import com.newgrand.superviseanalysis.utils.ResponseMsg;
import com.newgrand.superviseanalysis.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 田霖禹
 * @since 2020-04-01
 */
@Controller
@RequestMapping("/dict")
public class DictController {
    private IDictService dictService;
    private IOrganService organService;

    @Autowired
    public DictController(IDictService dictService, IOrganService organService) {
        this.dictService = dictService;
        this.organService = organService;
    }

    @RequestMapping("/page")
    public String Page(HttpServletRequest request) {
        return "dict/list";
    }

    @ResponseBody
    @RequestMapping("/list")
    public Result list(HttpServletRequest request, DictOrganUserVo dictOrganUserVo, HttpSession httpSession) {

        if (dictOrganUserVo.getParentId() == null) {
            dictOrganUserVo.setParentId((long) 0);
        } else {
            SysUser user = (SysUser) httpSession.getAttribute("userInfo");
            dictOrganUserVo.setOrganId(user.getOrganId());
        }

        List<DictOrganUserVo> list = dictService.listWithOrganNameAndUserRealName(dictOrganUserVo);
        return Result.success(list);

    }

    @RequestMapping("/addOrEditPage")
    public ModelAndView addOrEditPage(HttpServletRequest request, String type, SysDict sysDict) {
        Map<String, Object> map = new HashMap<>();
        if ("add".equals(type)) {
            QueryWrapper<SysDict> queryWrapper = new QueryWrapper();
            queryWrapper.eq("parent_id", 0);
            queryWrapper.select("max(dict_code) dict_code");
            List<SysDict> list = dictService.list(queryWrapper);
            if (list == null || list.isEmpty()) {
                sysDict.setDictCode("01");
            } else {
                String dictCode = String.valueOf(Integer.parseInt(list.get(0).getDictCode()) + 1);
                sysDict.setDictCode(dictCode.length() == 1 ? "0" + dictCode : dictCode);
            }
        } else {
            sysDict = dictService.getById(sysDict.getDictId());
        }
        map.put("sysDict", sysDict);
        map.put("type", type);
        return new ModelAndView("dict/addOrEdit", map);
    }

    @ResponseBody
    @RequestMapping("/addOrEdit")
    public Result addOrEdit(HttpSession httpSession, HttpServletRequest request, String type, SysDict sysDict) {
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");

        try {
            if ("add".equals(type)) {
                sysDict.setCreateTime(new Date());
                sysDict.setUserId(user.getUserId());
                dictService.save(sysDict);
            } else {
                dictService.saveOrUpdate(sysDict);
            }
            return Result.success(ResponseMsg.INSERT_UPDATE_SUCCESS);
        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_999, ResponseMsg.INSERT_UPDATE_ERROR);
        }
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Result delete(SysDict sysDict) {
        try {
            // 删除当前字典
            sysDict.setIsDelete(-1);
            dictService.update(sysDict, new UpdateWrapper<SysDict>().eq("dict_id",sysDict.getDictId()));

            // 删除其下属字典
            sysDict.setParentId(sysDict.getDictId());
            sysDict.setDictId(null);
            dictService.update(sysDict, new UpdateWrapper<SysDict>().eq("parent_id", sysDict.getParentId()));
            return Result.success(ResponseMsg.DELETE_SUCCESS);
        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_999, ResponseMsg.DELETE_ERROR);
        }
    }

    @RequestMapping("/detail")
    public String detail(HttpServletRequest request, Model model, SysDict sysDict) {
        model.addAttribute("sysDict", sysDict);
        return "dict/detail/list";
    }

    @RequestMapping("/detail/addOrEditPage")
    public ModelAndView detailAddOrEditPage(String type, SysDict sysDict, HttpSession httpSession, Model model) {
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");
        Map<String, Object> map = new HashMap<>();
        if ("add".equals(type)) {
            QueryWrapper<SysDict> queryWrapper = new QueryWrapper();
            queryWrapper.eq("parent_id", sysDict.getParentId());
            queryWrapper.select("max(dict_code) dict_code");
            List<SysDict> list = dictService.list(queryWrapper);
            String dictCode = dictService.getOne(new QueryWrapper<SysDict>().select("dict_code").eq("dict_id", sysDict.getParentId())).getDictCode();

            if (list == null || list.isEmpty() || list.get(0) == null) {
                sysDict.setDictCode(dictCode + "01");
            } else {
                dictCode = String.valueOf(Integer.parseInt(list.get(0).getDictCode()) + 1);
                sysDict.setDictCode(dictCode.length() == 3 ? "0" + dictCode : dictCode);
            }
        } else {
            sysDict = dictService.getById(sysDict.getDictId());
        }
        // 查询组织列表
        QueryWrapper<SysOrgan> queryWrapper = new QueryWrapper();
        queryWrapper.select("organ_id", "organ_name", "parent_id").and(i -> i.eq("is_delete", 0).eq("organ_id", user.getOrganId()));
        queryWrapper.or(i -> i.likeRight("parent_id", user.getOrganId()).eq("is_delete", 0));

        queryWrapper.orderByAsc("organ_order");
        List<SysOrgan> organList = organService.list(queryWrapper);
        model.addAttribute("sysDict", sysDict);
        model.addAttribute("organList", organList);
        model.addAttribute("type", type);
        return new ModelAndView("dict/detail/addOrEdit", map);
    }
}
