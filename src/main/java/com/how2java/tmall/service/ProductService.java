package com.how2java.tmall.service;

import com.how2java.tmall.dao.ProductDao;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService  {
	
	@Autowired
	ProductDao productDAO;
	@Autowired
    CategoryService categoryService;
	@Autowired
    ProductImageService productImageService;
	@Autowired
	OrderItemService orderItemService;
	@Autowired
	ReviewService reviewService;

	public void add(Product bean) {
		productDAO.save(bean);
	}

	public void delete(int id) {
		productDAO.delete(id);
	}

	public Product get(int id) {
		return productDAO.findOne(id);
	}

	public void update(Product bean) {
		productDAO.save(bean);
	}

	public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages) {
    	Category category = categoryService.get(cid);
    	Sort sort = new Sort(Sort.Direction.DESC, "id");
    	Pageable pageable = new PageRequest(start, size, sort);    	
    	Page<Product> pageFromJPA =productDAO.findByCategory(category,pageable);
    	return new Page4Navigator<>(pageFromJPA,navigatePages);
	}

	public void fill(List<Category> categorys) {
		for (Category category : categorys) {
			fill(category);
		}
	}

	/**
	 * 为分类填充产品
	 * @param category
	 */
	public void fill(Category category) {
		List<Product> products = listByCategory(category);
		productImageService.setFirstProductImages(products);
		category.setProducts(products);
	}

	public void fillByRow(List<Category> categorys) {
		int productNumberEachRow = 8;
		for (Category category : categorys) {
			List<Product> products =  listByCategory(category);
			List<List<Product>> productsByRow =  new ArrayList<>();
			for (int i = 0; i < products.size(); i+=productNumberEachRow) {
				int size = i+productNumberEachRow;
				size= size>products.size()?products.size():size;
				List<Product> productsOfEachRow =products.subList(i, size);
				productsByRow.add(productsOfEachRow);
			}
			category.setProductsByRow(productsByRow);
		}
	}

	public List<Product> listByCategory(Category category){
		return productDAO.findByCategoryOrderById(category);
	}

	/**
	 * 为产品设置销量和评价量
	 * @param product
	 */
	public void setSaleAndReviewCount(Product product) {
		int saleCount = orderItemService.getSaleCount(product);
		product.setSaleCount(saleCount);

		int reviewCount = reviewService.getCount(product);
		product.setReviewCount(reviewCount);
	}

	/**
	 * 为产品设置销量和评价量(批量)
	 * @param products
	 */
	public void setSaleAndReviewCount(List<Product> products) {
		for (Product product : products) {
			setSaleAndReviewCount(product);
		}
	}

	public List<Product> search(String keyword, int start, int size) {
		Sort sort = new Sort(Sort.Direction.DESC, "id");
		Pageable pageable = new PageRequest(start, size, sort);
		List<Product> products = productDAO.findByNameLike("%"+keyword+"%", pageable);
		return products;
	}



}
