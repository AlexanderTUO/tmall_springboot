package com.how2java.tmall.interceptor;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Author: tyk
 * @Date: 2019/7/11 09:40
 * @Description: 其它拦截器
 */
public class OtherInterceptor implements HandlerInterceptor {

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private CategoryService categoryService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        //获取搜索框下分类、context存入servletContext中，将购物车订单项数量存入session中
        HttpSession session = httpServletRequest.getSession();
        User user = (User) session.getAttribute("user");
        int cartTotalItemNumber = 0;
        if (null != user) {
            List<OrderItem> orderItems = orderItemService.listByUser(user);
            cartTotalItemNumber = orderItems.size();
        }

        List<Category> categories = categoryService.list();
        String contextPath = httpServletRequest.getServletContext().getContextPath();

        httpServletRequest.getServletContext().setAttribute("categories_below_search",categories);
        session.setAttribute("cartTotalItemNumber",cartTotalItemNumber);
        httpServletRequest.getServletContext().setAttribute("contextPath",contextPath);
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
