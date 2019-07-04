package com.how2java.tmall.comparator;

import com.how2java.tmall.pojo.Product;

import java.util.Comparator;

/**
 * @Author: tyk
 * @Date: 2019/7/4 17:06
 * @Description:
 */
public class ProductSaleCountComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        return o2.getSaleCount() - o1.getSaleCount();
    }
}
