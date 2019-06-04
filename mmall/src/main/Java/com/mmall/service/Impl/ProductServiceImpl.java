package com.mmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;

import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujia on 2019/4/17.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;
     //更新还是插入
    public  ServerResponse saveOrUpdateProduct(Product product){
        if(product!=null){
           if(StringUtils.isNotBlank(product.getSubImages())){
               String [] subImageArray=product.getSubImages().split(",");
               if(subImageArray.length>0){
                   product.setMainImage(subImageArray[0]);
               }
            }
            if(product.getId()!=null){
                //更新
              int rowCount=  productMapper.updateByPrimaryKey(product);
                if(rowCount>0){
                    return ServerResponse.createBySuccess("更新产品成功");
                }
                return ServerResponse.createByErrorMessage("更新失败");
            }else{
               int count= productMapper.insert(product);
                if(count>0){
                    return ServerResponse.createBySuccess("新增产品成功");
                }
                return ServerResponse.createByErrorMessage("新增失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

   public ServerResponse<String> setSaleSatus(Integer productId,Integer status){
       if(productId==null || status==null){
           return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
       }
       Product product=new Product();
       product.setId(productId);
       product.setStatus(status);
       int count=productMapper.updateByPrimaryKeySelective(product);
       if(count>0){
           return  ServerResponse.createBySuccess("修改产品销售状态成功");
       }
      return ServerResponse.createByErrorMessage("修改产品销售状态失败");
   }

    public ServerResponse<ProductDetailVo> mansgeProductDetail(Integer productId){
        if(productId==null){
            return ServerResponse.createByErrorCodeMessage  (ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("产品已下架或已经删除");
        }
        //vo对象--》value Object
        ProductDetailVo  productDetailVo=assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);

    }

    private  ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo=new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImage(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        //imageHost需要配置
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());//分类
        if(category==null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setCreateTime(DateTimeUtil.DateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.DateToStr(product.getUpdateTime()));
        return  productDetailVo;
    }

    public ServerResponse getProductList(int pageNum,int pageSize){
        //startPage--start
        //填充sql
        //pageHelper--收尾
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList=productMapper.selectList();
        List<ProductListVO> productListVOList= Lists.newArrayList();
        for(Product productItem:productList){
            ProductListVO productListVO=assembleProductListVO(productItem);
            productListVOList.add(productListVO);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVOList);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 获取ProductListVO对象
     * @return
     */
    private ProductListVO assembleProductListVO(Product product){
        ProductListVO productListVO=new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setName(product.getName());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus());
        productListVO.setSubtitle(product.getSubtitle());
        productListVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        return productListVO;
    }

    /**
     * 后台商品搜索功能
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){
            //转义成sql
            productName=new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList=productMapper.selectByNameAndProductId(productName,productId);
        //转化为listVO
        List<ProductListVO> productListVOList= Lists.newArrayList();
        for(Product productItem:productList){
            ProductListVO productListVO=assembleProductListVO(productItem);
            productListVOList.add(productListVO);
        }
        //翻页
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVOList);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse<ProductDetailVo> getProductDetailVO(Integer productId){
        if(productId==null){
            return ServerResponse.createByErrorCodeMessage  (ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("产品已下架或已经删除");
        }
        if(product.getStatus()!= Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或已经删除");

        }
        //vo对象--》value Object
        ProductDetailVo  productDetailVo=assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,
                                                                Integer categoryId,
                                                                int pageNum,
                                                                 int pageSize,String orderBy){
        if(StringUtils.isBlank(keyword)&&categoryId==null){
            //不合法
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList=new ArrayList<Integer>();
        if(categoryId!=null){
            Category category=categoryMapper.selectByPrimaryKey(categoryId);
            if(category==null&&StringUtils.isBlank(keyword)){
                //没有该分类，并关键字为空
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVO> productListVOList=Lists.newArrayList();
                PageInfo pageInfo=new PageInfo(productListVOList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList=iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword=new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        //排序处理
       if(StringUtils.isNotBlank(orderBy)){//不为空
           if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
               String [] orderByArray=orderBy.split("_");
               PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
           }
       }
        List<Product> productList=productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,
                categoryIdList.size()==0?null:categoryIdList);
        List<ProductListVO>productListVOList=Lists.newArrayList();
        for(Product product:productList){
            ProductListVO productListVO=assembleProductListVO(product);
            productListVOList.add(productListVO);
        }
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVOList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
