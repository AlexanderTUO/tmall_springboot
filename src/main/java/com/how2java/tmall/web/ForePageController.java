package com.how2java.tmall.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DateFormat;
import java.util.Date;

/**
 * @Author: tyk
 * @Date: 2019/5/17 09:58
 * @Description:
 */
@org.springframework.stereotype.Controller
public class ForePageController {
    Logger logger = LoggerFactory.getLogger(ForePageController.class);
    @RequestMapping("hello")
    public String hello(Model model) {
        logger.info("hello,我是日志！");
        model.addAttribute("now", DateFormat.getDateTimeInstance().format(new Date()));
        return "hello";
    }
}
