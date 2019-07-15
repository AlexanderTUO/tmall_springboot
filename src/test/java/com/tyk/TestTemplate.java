package com.tyk;

import com.how2java.tmall.TYKApplication;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.service.OrderItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: tyk
 * @Date: 2019/5/17 11:00
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=TYKApplication.class)
public class TestTemplate {

    @Autowired
    OrderItemService orderItemService;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestTemplate.class);

    @Test
    public void findByName() {
//        User user = userService.findByName("admin");
    }

    @Test
    public void findOrderItemByPid() {
        OrderItem orderItem = orderItemService.getByPid(204);
        LOGGER.info(orderItem.toString());
    }
}
