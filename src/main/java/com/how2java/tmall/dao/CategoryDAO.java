package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: tyk
 * @Date: 2019/6/5 10:52
 * @Description:
 */
public interface CategoryDao extends JpaRepository<Category,Integer> {
}
