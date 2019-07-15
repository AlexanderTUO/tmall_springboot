package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemDAO extends JpaRepository<OrderItem,Integer>{
	List<OrderItem> findByOrderOrderByIdDesc(Order order);

	List<OrderItem> findByProduct(Product product);

	List<OrderItem> findByUserAndOrderIsNull(User user);

	@Query("select oi from OrderItem oi where oi.product.id=:pid")
	OrderItem findByPid(@Param("pid") int pid);
}
