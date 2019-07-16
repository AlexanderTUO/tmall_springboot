package com.how2java.tmall.web;

import com.sun.org.apache.bcel.internal.generic.VariableLengthInstruction;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.util.Date;

/**
 * @Author: tyk
 * @Date: 2019/5/17 09:58
 * @Description:
 */
@Controller
public class ForePageController {
    Logger logger = LoggerFactory.getLogger(ForePageController.class);
    @GetMapping(value = "/")//相当于get类型的RequestMapping
    public String index() {
        logger.info("");
        return "redirect:home";
    }

    @GetMapping(value = "/home")//相当于get类型的RequestMapping
    public String home() {
        logger.info("");
        return "fore/home";
    }

    @GetMapping(value = "/register")
    public String register() {
        return "fore/register";
    }

    @GetMapping(value = "/registerSuccess")
    public String registerSuccess() {
        return "fore/registerSuccess";
    }

    @GetMapping(value = "/login")
    public String login() {
        return "fore/login";
    }

    @GetMapping(value = "/foreLogout")
    public String loginOut(HttpSession session) {
//        session.removeAttribute("user");
        //shiro方式
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            subject.logout();

        }
        return "redirect:home";
    }

    @GetMapping(value = "/product")
    public String product() {
        return "fore/product";
    }

    @GetMapping(value = "/category")
    public String category() {
        return "fore/category";
    }

    @GetMapping(value = "/search")
    public String search() {
        return "fore/search";
    }

    @GetMapping(value = "/buy")
    public String buy() {
        return "fore/buy";
    }

    @GetMapping(value = "/cart")
    public String cart() {
        return "fore/cart";
    }

    @GetMapping(value = "/alipay")
    public String alipay() {
        return "fore/alipay";
    }

    @GetMapping(value = "/payed")
    public String payed() {
        return "fore/payed";
    }

    @GetMapping(value = "/bought")
    public String bought() {
        return "fore/bought";
    }

    @GetMapping(value = "/confirmPay")
    public String confirmPay() {
        return "fore/confirmPay";
    }

    @GetMapping(value = "orderConfirmed")
    public String orderConfirmed() {
        return "fore/orderConfirmed";
    }

    @GetMapping(value = "review")
    public String review() {
        return "fore/review";
    }
}
