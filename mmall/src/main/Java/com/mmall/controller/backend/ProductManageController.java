package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 产品接口
 * Created by xujia on 2019/4/17.
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService  iFileService;

    @RequestMapping( "productSave.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage  (ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.saveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("无操作权限，需要管理员权限");
        }
    }

    @RequestMapping( "set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId,Integer status){
        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage  (ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.setSaleSatus(productId,status);
        }else{
            return ServerResponse.createByErrorMessage("无操作权限，需要管理员权限");
        }
    }

    /**
     * 获取产品详情
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping( "detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId){
        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage  (ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.mansgeProductDetail(productId);
        }else{
            return ServerResponse.createByErrorMessage("无操作权限，需要管理员权限");
        }
    }

    @RequestMapping( "list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value="pageNum",defaultValue = "1") int pageNum ,@RequestParam(value = "1pageSize",defaultValue = "10") int pageSize){
        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage  (ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //添加动态分页

            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无操作权限，需要管理员权限");
        }
    }

    @RequestMapping( "search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session,String productName,Integer productId, @RequestParam(value="pageNum",defaultValue = "1") int pageNum ,@RequestParam(value = "1pageSize",defaultValue = "10") int pageSize){
        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage  (ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //添加动态分页

            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无操作权限，需要管理员权限");
        }
    }

    /**
     * 上传图片
     * @return
     */
    @RequestMapping( "upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage  (ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFile=iFileService.upload(file,path);
            //拼接url
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile;
            Map fileMap= Maps.newHashMap();
            fileMap.put("uri",targetFile);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }else{
            return ServerResponse.createByErrorMessage("无操作权限，需要管理员权限");
        }
    }

    @RequestMapping( "richtext.do")
    @ResponseBody
    public Map richtextUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap=Maps.newHashMap();
        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;

        }
        //富文本返回值有主见的要求
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFile=iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFile)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            //拼接url
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Controller-Allow-Headers","X-File-Name");
            return resultMap;
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }
}
