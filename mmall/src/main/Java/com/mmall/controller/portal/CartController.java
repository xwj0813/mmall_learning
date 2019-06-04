package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 购物车模块
 * Created by xujia on 2019/5/12.
 */
@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService iCartService;

    /**
     * 新增购物车
     * @param session
     * @param count
     * @param productId
     * @return
     */
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer productId,Integer count){
       User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //增加购物车的逻辑
       return  iCartService.add(user.getId(),productId,count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //增加购物车的逻辑
        return  iCartService.update(user.getId(),productId,count);
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpSession session,  String productIds){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //增加购物车的逻辑
        return  iCartService.deleteProduct(user.getId(),productIds);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpSession session, Integer count, Integer productId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //增加购物车的逻辑
        return  iCartService.list(user.getId());
    }
    //全选

    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //增加购物车的逻辑
        return  iCartService.selectOrUnselectAll(user.getId(),null,Const.Cart.CHECKED);
    }
    //反选
    @RequestMapping("unselect_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unselectAll(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //增加购物车的逻辑
        return  iCartService.selectOrUnselectAll(user.getId(),null,Const.Cart.CHECKED);
    }

    @RequestMapping("unselect.do")
    @ResponseBody
    public ServerResponse<CartVo> unselect(HttpSession session,Integer productId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //增加购物车的逻辑
        return  iCartService.selectOrUnselectAll(user.getId(),productId,Const.Cart.UN_CHECKED);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVo> selectproduct(HttpSession session,Integer productId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //增加购物车的逻辑
        return  iCartService.selectOrUnselectAll(user.getId(),productId,Const.Cart.CHECKED);
    }

    public ServerResponse<Integer> selectCartProductCount(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.selectCartProductCount(user.getId());
    }
}
