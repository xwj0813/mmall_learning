package com.mmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by xujia on 2019/5/25.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService{
    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse add(Integer userId,Shipping shipping){
        shipping.setUserId(userId);
        int rowCount=shippingMapper.insert(shipping);
        if(rowCount>0){
            Map result= Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",result);
        }
        return ServerResponse.createByErrorMessage("新建失败");
    }

    //删除地址

    /**
     * 删除地址
     * @param userId
     * @param shippingId
     * @return
     */
    public ServerResponse<String>  del(Integer userId,Integer shippingId){
        //横向越权
        // int resultCount=shippingMapper.deleteByPrimaryKey(shippingId);
        int resultCount=shippingMapper.deleteByShippingIdUserId(userId,shippingId);
        if(resultCount>0){
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除失败");
    }

    /**
     * 更新地址
     * @param userId 用户id
     * @param shippingId  地址id
     * @return
     */
    public ServerResponse update(Integer userId,Shipping shipping){
        shipping.setUserId(userId);
        int resultCount=shippingMapper.updateByShipping(shipping);
        if(resultCount>0){
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新失败");
    }

    /**
     * 查询收货地址详情
     * @param userId 用户id
     * @param shipping
     * @return
     */
    public ServerResponse<Shipping> select(Integer userId,Integer shippingId){

        Shipping objShipping=shippingMapper.selectByShippingIdUserId(userId,shippingId);
        if(objShipping==null){
            return ServerResponse.createByErrorMessage("查询失败");
        }

        return ServerResponse.createBySuccess("更新地址成功",objShipping);
    }

    public ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize){

        PageHelper.startPage(pageNum,pageSize);

        List<Shipping> lstShipping=shippingMapper.selectByUserId(userId);
        //返回pageInfo
        PageInfo pageInfo=new PageInfo(lstShipping);

        return ServerResponse.createBySuccess(pageInfo);
    }

}
