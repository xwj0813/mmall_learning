package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by xujia on 2019/4/18.
 */
public class DateTimeUtil {
    public static final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";

   //jada--->date
    public static Date strToDate(String dateTimeStr, String formatStr){
         DateTimeFormatter dateTimeFormat= DateTimeFormat.forPattern(formatStr);
        DateTime dateTime=dateTimeFormat.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }
   // date--》java
    public static String DateToStr(Date date,String formatStr){
         if(date==null){
             return StringUtils.EMPTY;
         }
        DateTime dateTime= new  DateTime(date);
        return dateTime.toString(formatStr);
    }

    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormat= DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime=dateTimeFormat.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String DateToStr(Date date){
        if(date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime= new  DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }

}
