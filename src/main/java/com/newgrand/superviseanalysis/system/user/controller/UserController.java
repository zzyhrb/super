package com.newgrand.superviseanalysis.system.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newgrand.superviseanalysis.system.organ.entity.SysOrgan;
import com.newgrand.superviseanalysis.system.organ.service.IOrganService;
import com.newgrand.superviseanalysis.system.role.entity.SysRole;
import com.newgrand.superviseanalysis.system.role.service.IRoleService;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import com.newgrand.superviseanalysis.system.user.entity.SysUserAccount;
import com.newgrand.superviseanalysis.system.user.entity.SysUserRole;
import com.newgrand.superviseanalysis.system.user.service.IUserAccountService;
import com.newgrand.superviseanalysis.system.user.service.IUserService;
import com.newgrand.superviseanalysis.utils.Base64Util;
import com.newgrand.superviseanalysis.utils.ResponseCode;
import com.newgrand.superviseanalysis.utils.ResponseMsg;
import com.newgrand.superviseanalysis.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private IUserService userService;
    private IRoleService roleService;
    private IOrganService organService;
    private IUserAccountService userAccountService;

    @Autowired
    public UserController(IUserService userService, IRoleService roleService, IOrganService organService, IUserAccountService userAccountService) {
        this.userService = userService;
        this.roleService = roleService;
        this.organService = organService;
        this.userAccountService = userAccountService;
    }

    @GetMapping("/load")
    public String load() {
        return "user/list";
    }

    @ResponseBody
    @PostMapping("/list")
    public Result list(SysUserRole sysUserRole) {
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(new SysUserRole());
        List<SysUserRole> userList = userService.listWithRoleName(sysUserRole);
        return Result.success(userList);
    }

    @ResponseBody
    @PostMapping("/delete")
    public Result delete(String userId) {

        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("is_delete", -1);
        updateWrapper.eq("user_id", userId);
        return Result.success(userService.update(updateWrapper));
    }

    @GetMapping("/load/add/or/update")
    public String loadAddOrUpdate(Model model, String userId, HttpSession httpSession) {
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");
        if (null != userId && !"".equals(userId)) {
            model.addAttribute("data", userService.getById(userId));
        }

        // 查询角色列表
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(new SysRole());
        model.addAttribute("roleList", roleService.list(queryWrapper));

        // 查询机构列表
        QueryWrapper<SysOrgan> organQueryWrapper = new QueryWrapper();
        organQueryWrapper.and(i -> i.eq("is_delete", 0).eq("organ_id", user.getOrganId()));
        organQueryWrapper.or(i -> i.likeRight("parent_id", user.getOrganId()).eq("is_delete", 0));
        organQueryWrapper.orderByAsc("organ_order");

        model.addAttribute("organList", organService.list(organQueryWrapper));

        return "user/add-or-update";
    }

    @PostMapping("/save")
    public String save(SysUser sysUser) {
        // 添加
        if (sysUser.getUserId() == null) {
            sysUser.setUserPassword(Base64Util.encode(sysUser.getUserPassword()));
            sysUser.setIsDelete(0);
        }
        // 更新时间
        sysUser.setUserModified(new Date());

        userService.saveOrUpdate(sysUser);
        return "redirect:/user/load";
    }

    @GetMapping("/load/info")
    public String loadUserInfo(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        SysUser user = (SysUser) session.getAttribute("userInfo");

        model.addAttribute("data", userService.getById(user.getUserId()));
        return "user/user-info";
    }

    @PostMapping("/save/info")
    public String saveInfo(SysUser sysUser) {
        // 更新时间
        sysUser.setUserModified(new Date());
        userService.saveOrUpdate(sysUser);
        return "redirect:/user/load/info";
    }

    @GetMapping("/load/update/password")
    public String loadUpdatePassword(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        SysUser sessionUser = (SysUser) session.getAttribute("userInfo");
        model.addAttribute("userId", sessionUser.getUserId());
        return "user/update-password";
    }

    @ResponseBody
    @PostMapping("/update/password")
    public Result updatePassword(HttpServletRequest request, String password) {
        try {
            HttpSession session = request.getSession();
            SysUser user = (SysUser) session.getAttribute("userInfo");
            UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("user_password", Base64Util.encode(password));
            updateWrapper.eq("user_id", user.getUserId());
            userService.update(updateWrapper);
            session.removeAttribute("userInfo");
            return Result.success("修改密码成功");
        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_999, e.toString());
        }
    }

    @GetMapping("/switch/user")
    public String switchUser(HttpServletRequest request, Model model, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("userInfo");

        List<SysUserAccount> userList = userAccountService.list(new QueryWrapper<SysUserAccount>().eq("user_id", user.getUserId()));
        for (SysUserAccount userAccount : userList) {
            userAccount.setUserPassword(Base64Util.decode(userAccount.getUserPassword()));
        }
        model.addAttribute("userList", userList);
        return "user/switch-user";
    }

    @ResponseBody
    @PostMapping("/check/phone")
    public Result checkPhone(String userCode, String userId) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code", userCode);
        queryWrapper.eq("is_delete", 0);
        if (null != userId && !"".equals(userId)) {
            queryWrapper.ne("user_id", userId);
        }
        List<SysUser> users = userService.list(queryWrapper);
        if (users.size() == 0) {
            return Result.success(ResponseMsg.QUERY_SUCCESS);
        } else {
            return Result.failure(ResponseCode.ERROR_SERVICE_VALIDATOR, ResponseMsg.QUERY_ERROR);
        }
    }

    @ResponseBody
    @PostMapping("/check/password")
    public Result checkOldPassword(String userId, String password) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        SysUser user = userService.getOne(queryWrapper);
        if (null != user.getUserPassword() && user.getUserPassword().equals(Base64Util.encode(password))) {
            return Result.success(ResponseMsg.QUERY_SUCCESS);
        } else {
            return Result.failure(ResponseCode.ERROR_SERVICE_VALIDATOR, ResponseMsg.QUERY_ERROR);
        }
    }

    @GetMapping("/userAccount/page")
    public String userAccountList() throws SQLException {
        return "userAccount/list";
    }

    @ResponseBody
    @PostMapping("/userAccount/list")
    public Result userAccountList(HttpSession session) {
        try {
            SysUser user = (SysUser) session.getAttribute("userInfo");
            List<SysUserAccount> list = userAccountService.list(new QueryWrapper<SysUserAccount>().eq("user_id", user.getUserId()));
            return Result.success(list);
        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_SERVICE_VALIDATOR, ResponseMsg.QUERY_ERROR);
        }
    }

    @GetMapping("/userAccount/addOrEditPage")
    public String addOrEditPage(SysUserAccount userAccount, String type, Model model) {
        if ("edit".equals(type)) {
            userAccount = userAccountService.getById(userAccount.getId());
            model.addAttribute("userAccount", userAccount);
        }
        model.addAttribute("type", type);
        return "userAccount/add-or-update";
    }

    @ResponseBody
    @PostMapping("/userAccount/addOrEdit")
    public Result addOrEdit(SysUserAccount userAccount, HttpSession httpSession) {
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");
        userAccount.setUserPassword(Base64Util.encode(userAccount.getUserPassword()));
        userAccount.setUserId(user.getUserId());
        userAccountService.saveOrUpdate(userAccount);
        return Result.success(ResponseMsg.INSERT_UPDATE_SUCCESS);
    }

    @ResponseBody
    @PostMapping("/userAccount/checkAccount")
    public Result checkAccount(SysUserAccount userAccount, HttpSession httpSession) {
        SysUser user = (SysUser) httpSession.getAttribute("userInfo");
        userAccount.setUserId(user.getUserId());
        List<SysUserAccount> list = userAccountService.list(new QueryWrapper<SysUserAccount>().setEntity(userAccount));
        return Result.success(list);
    }

    @ResponseBody
    @PostMapping("/userAccount/delete")
    public Result delete(SysUserAccount userAccount) {
        try {
            userAccountService.removeById(userAccount.getId());
            return Result.success(ResponseMsg.DELETE_SUCCESS);
        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_999, ResponseMsg.DELETE_ERROR);
        }
    }

}
