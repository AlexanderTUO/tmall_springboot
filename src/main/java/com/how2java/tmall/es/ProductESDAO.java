package com.how2java.tmall.es;

import com.how2java.tmall.pojo.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: tyk
 * @Date: 2019/7/17 11:04
 * @Description:
 */
public interface ProductESDAO extends ElasticsearchRepository<Product,Integer> {
}
