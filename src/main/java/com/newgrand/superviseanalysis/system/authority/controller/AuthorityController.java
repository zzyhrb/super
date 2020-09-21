package com.newgrand.superviseanalysis.system.authority.controller;


import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newgrand.superviseanalysis.system.authority.entity.SysRoleMenu;
import com.newgrand.superviseanalysis.system.authority.service.IAuthorityService;
import com.newgrand.superviseanalysis.system.menu.entity.SysMenu;
import com.newgrand.superviseanalysis.system.menu.service.IMenuService;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import com.newgrand.superviseanalysis.utils.ResponseCode;
import com.newgrand.superviseanalysis.utils.Result;
import com.newgrand.superviseanalysis.utils.TreeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 王雷
 * @since 2020-04-02
 */
@Controller
@RequestMapping("/authority")
public class AuthorityController {
    private IAuthorityService authorityService;
    private IMenuService menuService;

    @Autowired
    public AuthorityController(IAuthorityService authorityService, IMenuService menuService) {
        this.authorityService = authorityService;
        this.menuService = menuService;
    }

    @ResponseBody
    @GetMapping("/list")
    public Result list(String roleId) {
        QueryWrapper<SysRoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(new SysRoleMenu());
        queryWrapper.eq("role_id", roleId);
        queryWrapper.eq("is_delete", 0);
        List<SysRoleMenu> menuList = authorityService.list(queryWrapper);
        return Result.success(menuList);
    }

    @GetMapping("/load/add/or/update")
    public String loadAddOrUpdate(Model model, String id) {
        // 修改查询角色信息
        if (null != id && !"".equals(id)) {
        }
        model.addAttribute("roleId", id);
        return "authority/add-or-update";
    }

    @PostMapping("/save")
    public String save(String roleId, String menus) {
        // 拆分菜单数组
        if (null != menus && !"".equals(menus)) {
            String[] menuArray = menus.split(",");
            // 先删除
            UpdateWrapper<SysRoleMenu> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("is_delete", -1);
            updateWrapper.eq("role_id", roleId);
            authorityService.update(updateWrapper);
            for (String menuId : menuArray) {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(Integer.parseInt(roleId));
                sysRoleMenu.setMenuId(Integer.parseInt(menuId));
                authorityService.save(sysRoleMenu);
            }
        }

        return "redirect:/role/sys-role/load/list";
    }

    /**
     * 权限菜单
     *
     * @return 菜单列表
     */
    @ResponseBody
    @GetMapping("/role/menu/list")
    public Result roleMenuList(HttpServletRequest request) {
        // 获取登录用户角色
        HttpSession session = request.getSession();
        SysUser user = (SysUser) session.getAttribute("userInfo");
        Integer roleId = user.getRoleId();
        List<SysMenu> list = authorityService.listWithRoleMenu(roleId);
        JSONArray treeMenu = TreeUtils.treeMenuList(list, 0);
        return Result.success(treeMenu);
    }

    /**
     * 校验是否有角色关联菜单
     *
     * @param menuId 菜单ID
     * @return 是否关联
     */
    @ResponseBody
    @PostMapping("/check/role/menu")
    public Result checkRoleMenu(String menuId) {
        QueryWrapper<SysRoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("menu_id", Integer.parseInt(menuId));
        queryWrapper.eq("is_delete", 0);
        List<SysRoleMenu> dataList = authorityService.list(queryWrapper);

        if (dataList.size() > 0) {
            return Result.failure(ResponseCode.ERROR_SERVICE_VALIDATOR, "有角色关联，不能删除！");
        } else {
            return Result.success("无角色关联");
        }
    }
}
