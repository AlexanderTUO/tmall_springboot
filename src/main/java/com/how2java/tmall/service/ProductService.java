package com.how2java.tmall.service;

import com.how2java.tmall.dao.ProductDao;
import com.how2java.tmall.es.ProductESDAO;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import com.how2java.tmall.util.SpringContextUtil;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "products")
public class ProductService  {

	private static final Logger logger = LoggerFactory.getLogger(Product.class);
	
	@Autowired
	ProductDao productDAO;
	@Autowired
	ProductESDAO productESDAO;

	@Autowired
    CategoryService categoryService;
	@Autowired
    ProductImageService productImageService;
	@Autowired
	OrderItemService orderItemService;
	@Autowired
	ReviewService reviewService;

	//清除所有
	@CacheEvict(allEntries = true)
	public void add(Product bean) {
		productDAO.save(bean);
		productESDAO.save(bean);
	}

	@CacheEvict(allEntries = true)
	public void delete(int id) {
		productDAO.delete(id);
		productESDAO.delete(id);
	}


	@Cacheable(key = "'product-one-'+#p0")
	public Product get(int id) {
		logger.info("获取id为"+id+"的产品信息！");
		return productDAO.findOne(id);
	}

	@CacheEvict(allEntries = true)
	public void update(Product bean) {
		productDAO.save(bean);
		productESDAO.save(bean);
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
		//绕一绕，调用productService的方法，利用缓存
		ProductService productService = SpringContextUtil.getBean(ProductService.class);
		List<Product> products = productService.listByCategory(category);

		productImageService.setFirstProductImages(products);
		category.setProducts(products);
	}

	public void fillByRow(List<Category> categorys) {
		int productNumberEachRow = 8;
		for (Category category : categorys) {
			List<Product> products =  category.getProducts();
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

	@Cacheable(key = "'products-cid-'+#p0.id")
	public List<Product> listByCategory(Category category){
		logger.info("获取类别id为"+category.getId()+"的类别的所有产品");
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
//		Sort sort = new Sort(Sort.Direction.DESC, "id");
//		Pageable pageable = new PageRequest(start, size, sort);
//		List<Product> products = productDAO.findByNameLike("%"+keyword+"%", pageable);
//		return products;

		//改用ElasticSearch

		//初始化数据到es
		initDatabase2Es();
		//建立查询条件：1、构建es查询器 2、分页条件
		FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
				.add(QueryBuilders.matchPhraseQuery("name", keyword), ScoreFunctionBuilders.weightFactorFunction(100))
				.scoreMode("sum")
				.setMinScore(10);

		Sort sort = new Sort(Sort.Direction.DESC,"id");
		Pageable pageable = new PageRequest(start, size,sort);
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withPageable(pageable)
				.withQuery(functionScoreQueryBuilder).build();

		//根据条件进行查询
		Page<Product> page = productESDAO.search(searchQuery);
		return page.getContent();
	}

	/**
	 * 初始化数据到es
	 */
	public void initDatabase2Es() {
		Pageable pageable = new PageRequest(0, 5);
		Page<Product> page = productESDAO.findAll(pageable);
		if (page.getContent().isEmpty()) {
			List<Product> products = productDAO.findAll();
			for (Product product : products) {
				productESDAO.save(product);
			}
		}
	}



}
