package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * 用户接口
 * Created by xujia on 2019/4/6.
 */

public interface IUserService {
     //    登陆
    ServerResponse<User> login(String username, String passwd);
   //注册接口
    ServerResponse<String> register(User user);
   //判断是否游侠
    ServerResponse<String> checkValid(String str,String type);
    ServerResponse<String> seleteQuestion(String username);
    ServerResponse<String> checkAnswer(String username,String question, String answer);
    ServerResponse<String> forgetRestPassword(String username ,String passwordNew,String forgetToken);
    ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);
    ServerResponse<User> updateInformation(User user);
    ServerResponse<User> getInformation(Integer userId);
    ServerResponse checkAdminRole(User user);
}
