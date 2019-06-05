package com.how2java.tmall.service;

import com.how2java.tmall.dao.CategoryDao;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import net.bytebuddy.TypeCache;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.List;

/**
 * @Author: tyk
 * @Date: 2019/6/5 10:49
 * @Description:
 */
@Service
public class CategoryService {
    @Autowired
    public CategoryDao categoryDao;

    public Page4Navigator<Category> list(int start,int size,int navigatePages) {
        return null;
    }

    public List<Category> list() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        List list = categoryDao.findAll(sort);
        return list;
    }

    public void add(Category bean) {
        categoryDao.save(bean);
    }

    public void delete(int id) {
        categoryDao.deleteById(id);
    }

    public void update(Category bean) {
        categoryDao.save(bean);
    }

    public Category get(int id) {
        Category category = categoryDao.getOne(id);
        return category;
    }

    public void removeCategoryFromProduct(List<Category> categories) {
        for (Category category : categories) {
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category) {
        List<Product> products = category.getProducts();
        if (null!=products) {
            for (Product product : products) {
                product.setCategory(null);
            }
        }

        List<List<Product>> productsByRow = category.getProductsByRow();
        if (null != productsByRow) {
            for (List<Product> products1 : productsByRow) {
                for (Product product : products1) {
                    product.setCategory(null);
                }
            }
        }
    }

}
