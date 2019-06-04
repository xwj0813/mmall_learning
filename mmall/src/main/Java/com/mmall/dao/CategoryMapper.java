package com.mmall.dao;

import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);
    List<Category> selectCategoryChildrenByParentId(Integer parentId);

    List<Product> selectByNameAndProductId(@Param("productName")String productName,@Param("productId")String productId);

}