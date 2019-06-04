package com.mmall.service.Impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by xujia on 2019/4/6.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> login(String username, String password) {
       //判断用户是否存在
        int resultCount=userMapper.checkUsername(username);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //密码登陆MD5
       String  md5password=MD5Util.MD5EncodeUtf8(password);
        System.out.println(md5password);
        User user=userMapper.selectLogin(username,md5password);
        if(user==null){//为空
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功",user);
    }

    public ServerResponse<String> register(User user){
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 判断是否有效
     * @param str 对应的值
     * @param type 类型 是username 还是email
     * @return
     */
   public  ServerResponse<String> checkValid(String str,String type){
       //" " 为false
       if(StringUtils.isNoneBlank(type)){
           //开始检验
           if(Const.USERNAME.equals(type)){
                int resultCount=userMapper.checkUsername(str);
                     if(resultCount>0){
                         return ServerResponse.createByErrorMessage("用户名已存在");
                     }
                 }
           if(Const.EMAIL.equals(type)){
               int resultCount=userMapper.checkEmail(str);
               if(resultCount>0){
                   return ServerResponse.createByErrorMessage("邮箱已存在");
               }
           }
       }else{
           return ServerResponse.createByErrorMessage("参数错误");
       }
       return ServerResponse.createBySuccessMessage("校验成功");
   }

    public ServerResponse<String> seleteQuestion(String username){
       //检验用户名
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
       if(validResponse.isSuccess()){
           return  ServerResponse.createByErrorMessage("用户不存在");
       }
        String question=userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNoneBlank(question)){
            return ServerResponse.createBySuccessMessage(question);
        }
        return  ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    public ServerResponse<String> checkAnswer(String username,String question, String answer){
        int resultCount=userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            //说明问题及答案是这个用户的，并且是正确的
            String forgetToken= UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    /**
     * 忘记密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    public ServerResponse<String> forgetRestPassword(String username ,String passwordNew,String forgetToken){
    //检验参数
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            return  ServerResponse.createByErrorMessage("用户不存在");
        }
        //获取toen
        String token=TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return  ServerResponse.createByErrorMessage("token无效");
        }
        if(StringUtils.equals(forgetToken,token)){
             String md5Password=MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount=userMapper.updatePasswordByUsername(username,passwordNew);
            if(rowCount>0){
                return  ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return  ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return  ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 重置密码
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     * @return
     */
   public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
    //防止横向越权，要检验一下这个用户的旧密码，一定要指定是这个用户因为我们会查询一个count（1），如果不指定id，name结果就是true
       int resultCount=userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
       if(resultCount==0){
           return ServerResponse.createByErrorMessage("旧密码错误");
       }
       user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
       int updateCount=userMapper.updateByPrimaryKeySelective(user);
       if(updateCount==0){
           return ServerResponse.createBySuccessMessage("更新密码成功");
       }
       return ServerResponse.createByErrorMessage("更新密码错误");
   }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    public ServerResponse<User> updateInformation(User user){
      //username不能被更新、、
        //email 需要被校验,新的email是不是已经存在，如果存在的eamil，如果相同的话，不能是我们当前这个用户的
        int resultCount=userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return  ServerResponse.createByErrorMessage("email已存在，请更换email再次更新");
        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount=userMapper.updateByPrimaryKeySelective(updateUser);
        if(resultCount>0){
            return  ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);

    }




    //backend

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
