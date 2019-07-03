package com.how2java.tmall.service;

import com.how2java.tmall.dao.OrderItemDAO;
import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {
	@Autowired
    OrderItemDAO orderItemDAO;
	@Autowired
    ProductImageService productImageService;

	public void add(OrderItem orderItem) {
		orderItemDAO.save(orderItem);
	}

	public void delete(int id) {
		orderItemDAO.delete(id);
	}

	public void update(OrderItem orderItem) {
		orderItemDAO.save(orderItem);
	}

	public OrderItem get(int id) {
		return orderItemDAO.findOne(id);
	}

	public void fill(List<Order> orders) {
		for (Order order : orders) 
			fill(order);
	}

	public void fill(Order order) {
		List<OrderItem> orderItems = listByOrder(order);
		float total = 0;
		int totalNumber = 0;			
		for (OrderItem oi :orderItems) {
			total+=oi.getNumber()*oi.getProduct().getPromotePrice();
			totalNumber+=oi.getNumber();
			productImageService.setFirstProductImage(oi.getProduct());
		}
		order.setTotal(total);
		order.setOrderItems(orderItems);
		order.setTotalNumber(totalNumber);		
		order.setOrderItems(orderItems);
	}

	/**
	 * 获取销量
	 * @param product
	 * @return
	 */
	public int getSaleCount(Product product) {
		List<OrderItem> orderItems = findByProduct(product);
		int count = 0;
		for (OrderItem item : orderItems) {
			if (item.getOrder() != null && null != item.getOrder().getPayDate()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 根据产品获取订单
	 * @param product
	 * @return
	 */
	public List<OrderItem> findByProduct(Product product) {
		return orderItemDAO.findByProduct(product);
	}

	
	
	
    public List<OrderItem> listByOrder(Order order) {
    	return orderItemDAO.findByOrderOrderByIdDesc(order);
    }
	
	
}
