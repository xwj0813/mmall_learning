package com.mmall.service.Impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Interner;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimaUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by xujia on 2019/5/12.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;


    public ServerResponse<CartVo> add(Integer userId,Integer count,Integer productId){
        if(productId==null||count==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        Cart cart=cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart==null){
            //此时没有改商品的记录，需要重新加入购物
           Cart cartItem=new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            //插入
            cartMapper.insert(cartItem);
        }else{
            //此时购物车已经存在
            count=cart.getQuantity()+count;
            //更新数量
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        //购物车与库存的关系
        CartVo cartVo=this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);

    }

    public ServerResponse<CartVo> update(Integer userId,Integer count,Integer productId){
        if(productId==null||count==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        Cart cart=cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart!=null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        CartVo cartVo=this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
        //将productIDs转成list,以，分割，转成list
        List<String> productList= Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId,productList);
        CartVo cartVo=this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> list (Integer userId){
        CartVo cartVo=this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);

    }

    /**
     * 全选
     * @param userId
     * @return
     */
    public ServerResponse<CartVo> selectOrUnselectAll(Integer userId,Integer productId,Integer checked){
        cartMapper.checkedOrUncheckedProduct(userId,productId,checked);
        return this.list(userId);
    }

    public ServerResponse<Integer> selectCartProductCount(Integer userId){
        if(userId==null) {
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));

    }

    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo=new CartVo();
        List<Cart> cartList=cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList= Lists.newArrayList();
        BigDecimal cartTotalPrice=new BigDecimal("0");
        if(CollectionUtils.isNotEmpty(cartList)){
            for (Cart cartItem:cartList){
                //将查询购物车的东西转成cartProductVO
                CartProductVo cartProductVo=new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());
                //查询商品
                Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product!=null){
                    //构建cartProductVo
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStatus(product.getStatus());
                    //判断库存，与购买量之间的关系
                    int buyLimitCount=0;
                    if(product.getStock()>=cartItem.getQuantity()){
                        //库存多
                        buyLimitCount=cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount=product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车更新有效库存
                        Cart cartForQuantity=new Cart();
                        cartForQuantity.setId(cartItem.getProductId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    BigDecimal totalPrice= BigDecimaUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity());
                    cartProductVo.setProductTotalPrice(totalPrice);
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                if(cartItem.getChecked()==Const.Cart.CHECKED){
                    //如果该商品已经勾选，增加到购物车当中
                    cartTotalPrice=BigDecimaUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVo(cartProductVoList);
        //是否全选
        cartVo.setAllchecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("http://img.happymmall.com/"));
        return  cartVo;
    }

    private  boolean getAllCheckedStatus(Integer userId){
       if(userId==null){
           return false;
       }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
    }
}
