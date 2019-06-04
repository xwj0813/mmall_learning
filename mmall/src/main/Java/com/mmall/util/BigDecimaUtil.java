package com.mmall.util;

import java.math.BigDecimal;

/**
 * BigDecima算法工具类
 * Created by xujia on 2019/5/12.
 */
public class BigDecimaUtil {
    //私有化
    private BigDecimaUtil(){

    }
    //加法
    public  static BigDecimal add(double v1,double v2){
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }

    //减法运算
    public  static BigDecimal sub(double v1,double v2){
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }
    //乘法
    public  static BigDecimal mul(double v1,double v2){
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }

    public  static BigDecimal div(double v1,double v2){
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);//四舍五入，保留两位小数
    }

}
