package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.service.UserService;
import com.how2java.tmall.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author: tyk
 * @Date: 2019/5/17 09:58
 * @Description:
 */
@RestController
public class ForeRESTController {
    Logger logger = LoggerFactory.getLogger(ForeRESTController.class);

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @GetMapping("/forehome")
    public Object hello() {
        logger.info("进入首页！！！");
        List<Category> categories = categoryService.list();
        productService.fill(categories);
        productService.fillByRow(categories);
        categoryService.removeCategoryFromProduct(categories);
        return categories;
    }

    @PostMapping("/foreRegister")
    public Object register(@RequestBody User user) {
        String name = user.getName();
        name = HtmlUtils.htmlEscape(name);
        boolean exit = userService.isExit(name);
        if (exit) {
            String message = "用户名已被使用，不能使用";
            return Result.fail(message);
        }
        user.setName(name);
        userService.addUser(user);
        return Result.success();
    }
}
