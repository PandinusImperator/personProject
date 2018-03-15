package com.wangwd.person.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @RequestMapping(value = {"", "/", "/index", "/index/"}, method = RequestMethod.GET)
    public String index(Model model) {
//        model.addAttribute("test", "Hello SpringBoot 2.0 Release！");
        return "test";
    }

    @RequestMapping(value = {"/testOne"}, method = RequestMethod.GET)
    @ResponseBody
    public String test(Model model) {
        model.addAttribute("test", "Hello SpringBoot 2.0 Release！");
        return "test2.0";
    }
}
