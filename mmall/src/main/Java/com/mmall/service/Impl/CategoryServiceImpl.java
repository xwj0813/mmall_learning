package com.mmall.service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by xujia on 2019/4/15.
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService{
    private Logger logger= LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;
    public ServerResponse addCategory(String categoryName,Integer parentId){
        if(parentId==null|| StringUtils.isBlank(categoryName)){
            return  ServerResponse.createByErrorMessage("添加参数错误");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        //添加
        int rowCount=categoryMapper.insert(category);
        if(rowCount>0){
         return ServerResponse.createBySuccess("添加成功");
        }
        return ServerResponse.createByErrorMessage("添加失败");
    }

    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId==null|| StringUtils.isBlank(categoryName)){
            return  ServerResponse.createByErrorMessage("添加参数错误");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        int rowCount=categoryMapper.updateByPrimaryKeySelective (category);
        if(rowCount>0){
            return ServerResponse.createBySuccess("更新成功");
        }
        return ServerResponse.createByErrorMessage("更新失败");
    }

    /**
     * 查询平级子分类
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Category>> getChildrenParalleCategory(Integer categoryId){
        List<Category> categoryList= categoryMapper.selectCategoryChildrenByParentId(categoryId);
       if(CollectionUtils.isEmpty(categoryList)){//打印日志
           logger.info("未找到当前分类的子分类");
       }
       return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询孩子节点
     * @param categoryId
     * @return
     */
    public ServerResponse selectCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySet= Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        //返回类型变换
        List<Integer> categoryIdList= Lists.newArrayList();
        if(categoryId!=null){
            for(Category categoryItem:categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
       Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null){
            categorySet.add(category);
        }
        //查找子节点，递归算法一定要有一个退出的条件
        List<Category> categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for( Category categoryItem:categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
}
