package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * Created by xujia on 2019/4/17.
 */
public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse<String> setSaleSatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> mansgeProductDetail(Integer productId);

    ServerResponse getProductList(int pageNum,int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSiz);

    ServerResponse<ProductDetailVo> getProductDetailVO(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId, int pageNum, int pageSize,String orderBy);
}
