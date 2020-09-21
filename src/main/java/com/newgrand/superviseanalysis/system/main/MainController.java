package com.newgrand.superviseanalysis.system.main;

import com.newgrand.superviseanalysis.system.authority.service.IAuthorityService;
import com.newgrand.superviseanalysis.system.menu.entity.SysMenu;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.List;

@Controller
public class MainController {
    private IAuthorityService authorityService;

    @Autowired
    public MainController(IAuthorityService authorityService) {
        this.authorityService = authorityService;
    }


    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        SysUser user = (SysUser) session.getAttribute("userInfo");
        model.addAttribute("userInfo", user);

        // 获取当前年份
        model.addAttribute("year", Calendar.getInstance().get(Calendar.YEAR));
        return "index";
    }

    @GetMapping("/console")
    public String console(HttpSession session, Model model) {
        SysUser user = (SysUser) session.getAttribute("userInfo");
        // 快捷入口查询
        List<SysMenu> quickAccessList = authorityService.quickAccessList(user.getRoleId());
        model.addAttribute("data", user);
        model.addAttribute("quickAccessList", quickAccessList);

        return "console";
    }
}
