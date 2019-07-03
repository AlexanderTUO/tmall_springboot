package com.how2java.tmall.web;

import com.how2java.tmall.pojo.*;
import com.how2java.tmall.service.*;
import com.how2java.tmall.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import javax.swing.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    ProductImageService productImageService;

    @Autowired
    PropertyValueService propertyValueService;

    @Autowired
    ReviewService reviewService;

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

    @PostMapping("/foreLogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);

        User user = userService.getUser(name, userParam.getPassword());
        if (null == user) {
            return Result.fail("用户名或密码错误");
        }
        session.setAttribute("user",user);
        return Result.success();
    }

    @GetMapping("foreProduct/{pid}")
    public Object product(@PathVariable("pid") int pid) {
        //获取产品
        Product product = productService.get(pid);

        //获取产品的图片集合和细节图片集
        List<ProductImage> singleImages = productImageService.listSingleProductImages(product);
        product.setProductSingleImages(singleImages);

        List<ProductImage> detailImages = productImageService.listDetailProductImages(product);
        product.setProductDetailImages(detailImages);

        //获取产品的属性值和评价
        List<PropertyValue> propertyValues = propertyValueService.list(product);
        List<Review> reviews = reviewService.list(product);

        //为产品设置销量和评价数
        productService.setSaleAndReviewCount(product);

        //为产品设置首图
        productImageService.setFirstProductImage(product);

        Map<String, Object> map = new HashMap<>();
        map.put("product", product);
        map.put("propertyValue", propertyValues);
        map.put("reviews", reviews);

        return map;

    }
}
