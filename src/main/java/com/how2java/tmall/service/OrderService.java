package com.how2java.tmall.service;

import com.how2java.tmall.dao.OrderDAO;
import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
	public static final String waitPay = "waitPay";
	public static final String waitDelivery = "waitDelivery";
	public static final String waitConfirm = "waitConfirm";
	public static final String waitReview = "waitReview";
	public static final String finish = "finish";
	public static final String delete = "delete";	
	
	@Autowired
    OrderDAO orderDAO;

	@Autowired
	OrderItemService orderItemService;
	

	public Page4Navigator<Order> list(int start, int size, int navigatePages) {
    	Sort sort = new Sort(Sort.Direction.DESC, "id");
		Pageable pageable = new PageRequest(start, size,sort);
		Page pageFromJPA =orderDAO.findAll(pageable);
		return new Page4Navigator<>(pageFromJPA,navigatePages);
	}

	public void removeOrderFromOrderItem(List<Order> orders) {
		for (Order order : orders) {
			removeOrderFromOrderItem(order);
		}
	}

	private void removeOrderFromOrderItem(Order order) {
		List<OrderItem> orderItems= order.getOrderItems();
		for (OrderItem orderItem : orderItems) {
			orderItem.setOrder(null);
		}
	}

	public Order get(int oid) {
		return orderDAO.findOne(oid);
	}

	public void update(Order bean) {
		orderDAO.save(bean);
	}

	public void add(Order order) {
		orderDAO.save(order);
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackForClassName = "Exception")
	public float add(Order order, List<OrderItem> orderItems) {
		//新增订单，同时在订单项中加入订单id，用事务控制
		float total = 0;
		add(order);

		//测试事务是否生效
		if (false) {
			throw new RuntimeException();
		}

		for (OrderItem orderItem : orderItems) {
			orderItem.setOrder(order);
			orderItemService.update(orderItem);
			total += orderItem.getNumber() * orderItem.getProduct().getPromotePrice();
		}
		return total;
	}


	public List<Order> listByUserWithoutDelete(User user) {
		List<Order> orders = listByUserNotDelete(user);
		orderItemService.fill(orders);
		return orders;
	}

	public List<Order> listByUserNotDelete(User user) {
		return orderDAO.findByUserAndStatusNotOrderByIdDesc(user, OrderService.delete);
	}

}
