package com.mmall.test;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by geely
 */




public class BigDecimalTest {

    @Test
    public void test1(){
        //丢失精度
        System.out.println(0.05+0.01);
        System.out.println(1.0-0.42);
        System.out.println(4.015*100);
        System.out.println(123.3/100);
    }







    @Test
    public void test2(){
        BigDecimal b1 = new BigDecimal(0.05);
        BigDecimal b2 = new BigDecimal(0.01);
        System.out.println(b1.add(b2));
    }

    @Test
    public void test3(){
        //这样精确，用String 构造器，然后add甲方
        BigDecimal b1 = new BigDecimal("0.05");
        BigDecimal b2 = new BigDecimal("0.01");
        System.out.println(b1.add(b2));

    }

}
