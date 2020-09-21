package com.newgrand.superviseanalysis.system.menu.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newgrand.superviseanalysis.system.menu.entity.SysMenu;
import com.newgrand.superviseanalysis.system.menu.service.IMenuService;
import com.newgrand.superviseanalysis.utils.ResponseCode;
import com.newgrand.superviseanalysis.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 王雷
 * @since 2020-04-01
 */
@Controller
@RequestMapping("/menu/sys-menu")
public class MenuController {
    private IMenuService menuService;

    @Autowired
    public MenuController(IMenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/load/list")
    public String loadList() {
        return "menu/list";
    }

    @ResponseBody
    @GetMapping("/list")
    public Result list() {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(new SysMenu());
        queryWrapper.orderByAsc("sort");
        List<SysMenu> menuList = menuService.list(queryWrapper);
        return Result.success(menuList);
    }

    @GetMapping("/load/add/or/update")
    public String loadAddOrUpdate(Model model, String parentId, String id) {
        // 修改查询角色信息
        if (null != id && !"".equals(id)) {
            SysMenu data = menuService.getById(id);
            model.addAttribute("data", data);
        }

        model.addAttribute("parentId", null != parentId && !"".equals(parentId) ? parentId : "0");
        return "menu/add-or-update";
    }

    @PostMapping("/save")
    public String save(SysMenu menu) {
        menuService.saveOrUpdate(menu);
        return "redirect:/menu/sys-menu/load/list";
    }

    @ResponseBody
    @PostMapping("/delete")
    public Result delete(String id) {
        UpdateWrapper<SysMenu> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("is_delete", -1);
        updateWrapper.eq("id", id);
        boolean flag = menuService.update(updateWrapper);

        if (flag) {
            return Result.success("删除成功");
        } else {
            return Result.failure(ResponseCode.ERROR_SERVICE_VALIDATOR, "删除失败");
        }
    }

    @ResponseBody
    @PostMapping("/check/children")
    public Result checkChildren(String id) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        List<SysMenu> dataList = menuService.list(queryWrapper);
        if (dataList.size() > 0) {
            return Result.failure(ResponseCode.ERROR_SERVICE_VALIDATOR, "有子级，不可删除！");
        } else {
            return Result.success("无子级");
        }
    }
}
