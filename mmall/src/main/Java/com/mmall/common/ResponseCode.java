package com.mmall.common;

/**
 * Created by xujia on 2019/4/6.
 */
public enum ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");
    private final int code;
    private final String desc;

    /**
     * 构造函数
     * @param code 代码字
     * @param desc 描述
     */
    ResponseCode(int code,String desc){
        this.code=code;
        this.desc=desc;
    }

    /**
     * 获取code
     * @return code
     */
   public int getCode(){
       return code;
   }

    /**
     * 获取描述
     * @return dsc
     */
  public String getDesc(){
      return desc;
  }
}
