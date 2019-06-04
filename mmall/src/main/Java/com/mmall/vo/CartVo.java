package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by xujia on 2019/5/12.
 */
public class CartVo {

    private List<CartProductVo> cartProductVo;
    private BigDecimal cartTotalPrice;
    private Boolean allchecked;
    private String imageHost;

    public List<CartProductVo> getCartProductVo() {
        return cartProductVo;
    }

    public void setCartProductVo(List<CartProductVo> cartProductVo) {
        this.cartProductVo = cartProductVo;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Boolean getAllchecked() {
        return allchecked;
    }

    public void setAllchecked(Boolean allchecked) {
        this.allchecked = allchecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
