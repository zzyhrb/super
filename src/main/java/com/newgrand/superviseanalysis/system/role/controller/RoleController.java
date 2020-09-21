package com.newgrand.superviseanalysis.system.role.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newgrand.superviseanalysis.system.role.entity.SysRole;
import com.newgrand.superviseanalysis.system.role.service.IRoleService;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import com.newgrand.superviseanalysis.system.user.service.IUserService;
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
@RequestMapping("/role/sys-role")
public class RoleController {
    private IRoleService roleService;
    private IUserService userService;

    @Autowired
    public RoleController(IRoleService roleService, IUserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/load/list")
    public String loadList() {
        return "role/list";
    }

    @ResponseBody
    @PostMapping("/list")
    public Result list() {
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(new SysRole());
        List<SysRole> userList = roleService.list(queryWrapper);
        return Result.success(userList);
    }

    @GetMapping("/load/add/or/update")
    public String loadAddOrUpdate(Model model, String id) {
        // 修改查询角色信息
        if (null != id && !"".equals(id)) {
            SysRole role = roleService.getById(id);
            model.addAttribute("data", role);
        }
        return "role/add-or-update";
    }

    @PostMapping("/save")
    public String save(SysRole role) {
        roleService.saveOrUpdate(role);
        return "redirect:/role/sys-role/load/list";
    }

    @ResponseBody
    @PostMapping("/delete")
    public Result delete(String id) {
        // 校验是否有用户关联
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", id);
        List<SysUser> userList = userService.list(queryWrapper);

        if (userList.size() > 0) {
            return Result.failure(ResponseCode.ERROR_SERVICE_VALIDATOR, "有用户关联，不能删除！");
        } else {
            UpdateWrapper<SysRole> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("is_delete", -1);
            updateWrapper.eq("id", id);

            return Result.success(roleService.update(updateWrapper));
        }
    }
}
