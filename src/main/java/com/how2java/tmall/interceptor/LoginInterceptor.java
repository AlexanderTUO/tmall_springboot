package com.how2java.tmall.interceptor;

import com.how2java.tmall.pojo.User;
import org.apache.commons.lang.StringUtils;
import org.hsqldb.lib.StringUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HttpSession session = httpServletRequest.getSession();
        String contextPath = httpServletRequest.getContextPath();
        String[] requireAuthPages = new String[]{
                "buy",
                "alipay",
                "payed",
                "cart",
                "bought",
                "confirmPay",
                "orderConfirmed",

                "foreBuyOne",
                "foreBuy",
                "foreAddCart",
                "foreCart",
                "foreChangeItem",
                "foreDeleteItem",
                "foreCreateOrder",
                "forePayed",
                "foreBought",
                "foreConfirmPay",
                "foreOrderConfirmed",
                "foreDeleteOrder",
                "foreReview",
                "foreDoReview"
        };

        String uri = httpServletRequest.getRequestURI();
        uri = StringUtils.remove(uri, contextPath + "/");

        String page = uri;

        for (String requireAuthPage : requireAuthPages) {
            if (StringUtils.startsWith(page,requireAuthPage)) {
                User user = (User) session.getAttribute("user");
                if (user == null) {
                    httpServletResponse.sendRedirect("login");
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
