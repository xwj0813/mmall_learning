package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created by xujia on 2019/4/6.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化JSON的时候，如果是null的对象，key消失
public class ServerResponse <T> implements Serializable{
    private int status;
    private String msg;
    private T data;

    //构造方法
    private  ServerResponse(int status){
    this.status=status;
    }
    private  ServerResponse(int status,T data){
        this.status=status;
        this.data=data;
    }
    private  ServerResponse(int status,String msg){
        this.status=status;
        this.msg=msg;
    }
    private  ServerResponse(int status,String msg,T data){
        this.status=status;
        this.msg=msg;
        this.data=data;
    }

    /**
     * 判断是否登陆成功
     * @return 0 位成功
     */
    @JsonIgnore
//    使之不在json序列化结果当中
    public boolean isSuccess(){
        return this.status==ResponseCode.SUCCESS.getCode();
    }

    /**
     * 获取状态
     * @return status
     */
    public int getStatus(){
        return status;
    }

    /**
     * 获取数据
     * @return data
     */
    public T getData(){
        return data;
    }

    public String getMsg(){
        return msg;
    }
    public  static<T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }
   public static <T> ServerResponse<T> createBySuccessMessage(String msg){
       return  new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
   }

   public  static <T> ServerResponse<T> createBySuccess(T data){
     return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
 }
    public  static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }
    /***
     * 错误提示
     */
    public  static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
    public  static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){
        return new ServerResponse<T>(errorCode,errorMessage);
    }



    public static  void main(String[] args){
        ServerResponse str1= new ServerResponse(1,new Object());
        System.out.println("console"+str1);
        ServerResponse str2=new ServerResponse(1,"aa");
        System.out.println("console"+str2);
    }
}
