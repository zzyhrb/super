package com.newgrand.superviseanalysis.system.error.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author tly
 * @version 1.0.0
 * @ClassName ErrorController.java
 * @Description TODO
 * @createTime 2020年04月09日 15:26:00
 */
@Controller
@RequestMapping("/error")
public class ErrorController {

    @RequestMapping("/403")
    public String forbidden(){
        return "error/403";
    }

    @RequestMapping("/500")
    public String serverError(){
        return "error/500";
    }

    @RequestMapping("/404")
    public String notFound(){
        return "error/404";
    }



}
