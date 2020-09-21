package com.newgrand.superviseanalysis.filter;

import com.newgrand.superviseanalysis.system.authority.service.IAuthorityService;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class GlobalInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(GlobalInterceptor.class);
    private IAuthorityService authorityService;

    // 白名单列表
    private static String[] whiteArray = {"/load", "/login", "/logout", "/index", "/console", "/", "/authority/role/menu/list"};

    @Autowired
    public GlobalInterceptor(IAuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            logger.info("请求路径---->"+request.getRequestURI());
            // 统一拦截（查询当前session是否存在user）(这里user会在每次登陆成功后，写入session)
            SysUser user = (SysUser) request.getSession().getAttribute("userInfo");
            if (user != null) { // 登录
                boolean flag = false; // 是否可访问开关

                // 已经登录的用户不可以再访问登录
                if ((request.getRequestURI().equals("/load") || request.getRequestURI().equals("/login")) && !"switchUser".equals(request.getParameter("type"))) {
                    response.sendRedirect("/index");
                }

                // 是否在白名单里
//                if (Arrays.stream(whiteArray).filter(str -> str.equals(request.getRequestURI())).count() > 0) {
//                    flag = true;
//                }

                // 判断是否有权限访问
//                List<SysMenu> authorityList = authorityService.listWithRoleMenu(user.getRoleId());
//                for (SysMenu menu :
//                        authorityList) {
//                    if (null != menu.getUrl() && !"".equals(menu.getUrl())) {
//                        if (request.getRequestURI().equals(menu.getUrl())) {
//                            flag = true;
//                        }
//                    }
//                }
                return true;
            } else { // 未登录
                // 特殊请求放行
                if (request.getRequestURI().equals("/load")) {
                    return true;
                } else if (request.getRequestURI().equals("/login")) {
                    return true;
                }
            }
            response.sendRedirect("/load");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
