package com.how2java.tmall.web;

import com.how2java.tmall.comparator.*;
import com.how2java.tmall.pojo.*;
import com.how2java.tmall.service.*;
import com.how2java.tmall.util.Result;
import lombok.experimental.var;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.math.RandomUtils;
import org.omg.CORBA.CODESET_INCOMPATIBLE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.persistence.Table;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: tyk
 * @Date: 2019/5/17 09:58
 * @Description:
 */
@RestController
public class ForeRESTController {
    private static final Logger logger = LoggerFactory.getLogger(ForeRESTController.class);

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

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    OrderService orderService;

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

    /**
     * 检查是否登录
     * @param session
     * @return
     */
    @GetMapping("/foreCheckLogin")
    public Object checkLogin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return Result.success();
        }
        return Result.fail("未登录");
    }

    @GetMapping("/foreProduct/{pid}")
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

        return Result.success(map);
    }

    @GetMapping("/foreCategory/{cid}")
    public Object category(@PathVariable int cid, String sort) {
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setSaleAndReviewCount(category.getProducts());
        categoryService.removeCategoryFromProduct(category);
        if (null != sort) {
            switch (sort) {
                case "all":
                    Collections.sort(category.getProducts(),new ProductAllComparator());
                    break;
                case "date":
                    Collections.sort(category.getProducts(),new ProductDateComparator());
                    break;
                case "price":
                    Collections.sort(category.getProducts(), new ProductPriceComparator());
                    break;
                case "review":
                    Collections.sort(category.getProducts(), new ProductReviewComparator());
                    break;
                case "saleCount":
                    Collections.sort(category.getProducts(),new ProductSaleCountComparator());
                    break;
            }
        }
        return category;
    }

    @PostMapping("/foreSearch")
    public Object search(String keyword) {
        if (null == keyword) {
            keyword = "";
        }
        List<Product> products = productService.search(keyword, 0, 20);
        productImageService.setFirstProductImages(products);
        productService.setSaleAndReviewCount(products);
        return products;
    }

    /**
     *  立即购买
     * @param pid
     * @param num
     * @param session
     * @return
     */
    @GetMapping("/foreBuyOne")
    public Object buyOne(int pid,int num,HttpSession session) {
        return buyOneAndAddCart(pid, num, session);
    }

    /**
     *  添加到购物车
     * @param pid
     * @param num
     * @param session
     * @return
     */
    @GetMapping("/foreAddCart")
    public Object foreAddCart(int pid,int num,HttpSession session) {
        buyOneAndAddCart(pid, num, session);
        return Result.success();
    }

    /**
     * 添加订单项到购物车
     * @param pid
     * @param num
     * @param session
     * @return
     */
    private Object buyOneAndAddCart(int pid, int num, HttpSession session) {
        User user = (User) session.getAttribute("user");
        //分两种情况，一，购物车中订单项存在，则增加数量 二、不存在，则新创建订单项
        List<OrderItem> orderItems = orderItemService.listByUser(user);
        boolean flag = false;
        int oiid = 0;
        for (OrderItem orderItem : orderItems) {
            if (pid == orderItem.getProduct().getId()) {
                orderItem.setNumber(orderItem.getNumber()+num);
                orderItemService.update(orderItem);
                oiid = orderItem.getId();
                flag = true;
                break;
            }
        }
        if (!flag) {
            OrderItem orderItem = new OrderItem();
            orderItem.setNumber(num);
            Product product = new Product();
            product.setId(pid);
            orderItem.setProduct(product);
            orderItem.setUser(user);
            orderItemService.add(orderItem);
            oiid = orderItem.getId();
        }
        return oiid;
    }

    /**
     * 根据订单项id获取订单项详细信息
     * @param oiid
     * @param session
     * @return
     */
    @GetMapping("/foreBuy")
    public Object foreBuy(String[] oiid, HttpSession session) {
        List<OrderItem> list = new ArrayList<>();
        float total = 0;
        for (String stid : oiid) {
            int id = Integer.parseInt(stid);
            OrderItem item = orderItemService.get(id);
            total += item.getProduct().getPromotePrice() * item.getNumber();
            list.add(item);
        }

        productImageService.setFirstProductImageOnOrderItem(list);

        session.setAttribute("ois", list);

        Map<String, Object> map = new HashedMap();
        map.put("orderItems", list);
        map.put("total", total);

        return Result.success(map);
    }

    /**
     * 获取购物车订单项信息
     * @param session
     * @return
     */
    @GetMapping("/foreCart")
    public Object foreCart(HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<OrderItem> orderItems = orderItemService.listByUser(user);
        productImageService.setFirstProductImageOnOrderItem(orderItems);
        return orderItems;
    }

    @PostMapping("foreCreateOrder")
    public Object createOrder(@RequestBody Order order, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            Result.fail("未登录");
        }

        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())+RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setUser(user);
        order.setStatus(OrderService.waitPay);
        order.setCreateDate(new Date());

        List<OrderItem> orderItems = (List<OrderItem>) session.getAttribute("ois");

        float total = orderService.add(order, orderItems);

        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("oid", order.getId());
        return Result.success(map);
    }

    @GetMapping("/foreChangeOrderItem")
    public Object foreChangeOrderItem(int pid, int num,HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            Result.fail("未登录");
        }

        OrderItem item = orderItemService.getByPid(pid);
        item.setNumber(num);
        orderItemService.update(item);
        return Result.success();
    }

    @PostMapping("/foreDeleteOrderItem")
    public Object foreChangeOrderItem1(int oiid,HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            Result.fail("未登录");
        }
        orderItemService.delete(oiid);
        return Result.success();
    }

    @GetMapping("/forePayed")
    public Object forePayed(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }

    @GetMapping(value = "foreBought")
    public Object foreBought(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("未登录");
        }
        List<Order> list = orderService.listByUserWithoutDelete(user);
        orderService.removeOrderFromOrderItem(list);
        return list;
    }

}
