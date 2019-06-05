package com.how2java.tmall.service;

import com.how2java.tmall.dao.ProductDao;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: tyk
 * @Date: 2019/6/5 11:48
 * @Description:
 */
@Service
public class ProductService {
    @Autowired
    private ProductDao productDao;

    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        return null;
    }

    public List<Category> list() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        List list = productDao.findAll(sort);
        return list;
    }

    public void add(Product bean) {
        productDao.save(bean);
    }

    public void delete(int id) {
        productDao.deleteById(id);
    }

    public void update(Product bean) {
        productDao.save(bean);
    }

    public Product get(int id) {
        Product product = productDao.getOne(id);
        return product;
    }

    /**
     * 为分类填充产品
     * @param category
     */
    public void fill(Category category) {
        //根据分类获取产品
        List<Product> products = listByCategory(category);

        //填充产品图片

        //在分类中填充产品
        category.setProducts(products);

    }

    /**
     * 为多个分类填充产品
     * @param categories
     */
    public void fill(List<Category> categories) {
        for (Category category : categories) {
            fill(category);
        }

    }

    /**
     * 为分类填充推荐产品
     * @param categories
     */
    public void fillByRow(List<Category> categories) {
        int productNumEachRow = 8;
        for (Category category : categories) {
            List<Product> products = listByCategory(category);
            List<List<Product>> productsByRow = new ArrayList<>();
            for (int i = 0; i < products.size(); i+=productNumEachRow) {
                int size = i + productNumEachRow;
                size = size > products.size() ? products.size() : size;
                List<Product> productsEachRow = products.subList(i, size);
                productsByRow.add(productsEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    /**
     * 查询某个分类下的所有产品
     * @param category
     */
    public List<Product> listByCategory(Category category) {
        return productDao.findByCategoryOrderById(category);
    }

}
