package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

/**
 * Created by xujia on 2019/5/12.
 */

public interface ICartService {
    ServerResponse<CartVo> add(Integer userId, Integer count, Integer productId);
    ServerResponse<CartVo> update(Integer userId,Integer count,Integer productId);
    ServerResponse<CartVo> deleteProduct(Integer userId,String productIds);

    ServerResponse<CartVo> list (Integer userId);
    ServerResponse selectOrUnselectAll(Integer userId,Integer productId,Integer checked);

    ServerResponse<Integer> selectCartProductCount(Integer userId);

}
