package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.mmall.common.ResponseCode;

import javax.servlet.http.HttpSession;

/**
 * 用户模块
 * Created by xujia on 2019/4/6.
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;
    /**
     * 登陆接口
     * @param username 用户名
     * @param passwd 密码
     * @param session session
     * @return 返回工具类
     */
    @RequestMapping(value="login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        //service-->mybatis-->dao
       ServerResponse<User> response= iUserService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 登出功能
     * @param session session
     * @return
     */
    @RequestMapping(value="logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> logout(HttpSession session){
     //清楚session、中的用户信息
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 注册接口
     * @param user 用户信息
     * @return
     */
    @RequestMapping(value="register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
       return iUserService.register(user);
    }

    //检验用户是否已存在

    /**
     * 检查是否有效
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value="check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
         return iUserService.checkValid(str,type);
    }

    /**
     * 获取用户信息
     * @param session
     * @return
     */
    @RequestMapping(value="get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
      User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
           return ServerResponse.createBySuccess(user);
        }
        return  ServerResponse.createByErrorMessage("用户未登录，无妨获取用户信息");
    }

    /**
     * 是否忘记问题
     * @param username
     * @return
     */
    @RequestMapping(value="forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.seleteQuestion(username);
    }
    @RequestMapping(value="forPOST_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
         return iUserService.checkAnswer(username,question,answer);
    }

    /**
     * 忘记密码
     * @param username 用户名
     * @param password  密码
     * @param forPOSTToken
     * @return
     */
    @RequestMapping(value="forPOST_rest_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String username ,String password,String forPOSTToken){
         return iUserService.forgetRestPassword(username,password,forPOSTToken);
    }

    /**
     * 重置密码
     * @param session session
     * @param passwordold 旧密码
     * @param passwordnew 新密码
     * @return
     */
    @RequestMapping(value="reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordold,String passwordnew){
       User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServerResponse.createByErrorMessage("用户未登录");
        }
      return iUserService.resetPassword(passwordold,passwordnew,user);
    }

    /**
     * 更新用户信息
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value="update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session,User user){
        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return  ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        ServerResponse<User> response=iUserService.updateInformation(user);
        if(response.isSuccess()){
            //设置用户名
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }

}
