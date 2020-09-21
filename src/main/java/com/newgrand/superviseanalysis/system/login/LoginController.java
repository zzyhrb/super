package com.newgrand.superviseanalysis.system.login;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newgrand.superviseanalysis.system.organ.entity.SysOrgan;
import com.newgrand.superviseanalysis.system.organ.service.IOrganService;
import com.newgrand.superviseanalysis.system.role.entity.SysRole;
import com.newgrand.superviseanalysis.system.role.service.IRoleService;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
public class LoginController {

    private IUserService userService;
    private IOrganService organService;
    private IRoleService roleService;


    @Autowired
    public LoginController(IUserService userService, IOrganService organService, IRoleService roleService) {
        this.userService = userService;
        this.organService = organService;
        this.roleService = roleService;
    }

    @GetMapping("/load")
    public String loadLogin(Model model) {
        model.addAttribute("title", "黑龙江省总工会直属事业单位经营监督分析系统");
        return "login";
    }

    @ResponseBody
    @PostMapping("/login")
    public Result login(HttpServletRequest request, SysUser sysUser) {
        HttpSession session = request.getSession();
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        sysUser.setUserPassword(Base64Util.encode(sysUser.getUserPassword()));
        queryWrapper.setEntity(sysUser);

        SysUser user = userService.getOne(queryWrapper);
        if (user != null) {
            session.setAttribute("userInfo", user);
            SysOrgan organ = organService.getById(user.getOrganId());
            SysRole role = roleService.getById(user.getRoleId());
            session.setAttribute("organ", organ);
            session.setAttribute("role", role);
            session.setAttribute("loginTime",new Date());

        } else {
            return Result.failure(ResponseCode.ERROR_999, ResponseMsg.LOGIN_ERROR);
        }
        return Result.success(ResponseMsg.LOGIN_SUCCESS);
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("userInfo");
        return "redirect:/load";
    }
}
