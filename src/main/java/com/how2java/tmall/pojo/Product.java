package com.how2java.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @Author: tyk
 * @Date: 2019/6/4 17:17
 * @Description:
 */
@Entity
@Table(name="product")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})//这个属性不进行Json的转换，放置json转换异常错误
@Document(indexName = "tmall_springboot",type = "product")//该注解将被包含在javadoc中
@Data
public class Product {
//    JPA提供的四种标准用法为TABLE,SEQUENCE,IDENTITY,AUTO.
//    TABLE：使用一个特定的数据库表格来保存主键。
//    SEQUENCE：根据底层数据库的序列来生成主键，条件是数据库支持序列。
//    IDENTITY：主键由数据库自动生成（主要是自动增长型）
//    AUTO：主键由程序控制。

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//生成规则，id自动增长
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cid")
    private Category category;

    private String name;
    private String subTitle;
    private float originalPrice;
    private float promotePrice;
    private int stock;
    private Date createDate;

    @Transient//不会序列化
    private ProductImage firstProductImage;

    @Transient
    private List<ProductImage> productSingleImages;//单个产品图片集

    @Transient
    private List<ProductImage> productDetailImages;//产品细节图片集

    @Transient
    private int saleCount;//产品销量

    @Transient
    private int reviewCount;//评价数量

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", category=" + category +
                ", name='" + name + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", originalPrice=" + originalPrice +
                ", promotePrice=" + promotePrice +
                ", stock=" + stock +
                ", createDate=" + createDate +
                ", firstProductImage=" + firstProductImage +
                ", productSingleImages=" + productSingleImages +
                ", productDetailImages=" + productDetailImages +
                ", saleCount=" + saleCount +
                ", reviewCount=" + reviewCount +
                '}';
    }
}
