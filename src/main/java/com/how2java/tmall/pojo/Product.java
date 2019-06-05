package com.how2java.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: tyk
 * @Date: 2019/6/4 17:17
 * @Description:
 */
@Entity
@Table(name="product")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})//这个属性不进行Json的转换，放置json转换异常错误
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



}
