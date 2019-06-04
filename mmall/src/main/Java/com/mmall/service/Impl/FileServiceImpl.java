package com.mmall.service.Impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by xujia on 2019/4/28.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file,String path){
        //原始名
        String fileName=file.getOriginalFilename();
        //拓展名
        String fileExtensionName=fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadName= UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件，文件名为：{}，上传路径:{},新文件名：{}",fileName,path,uploadName);
        File fileDir=new File(path);
        if(!fileDir.exists()){//不存在
            //设置一个权限
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        File targetFile =new File(path,uploadName);

        try {
            file.transferTo(targetFile);//文件上传
            //文件上传成功
            // 将targetFile 上传FTP服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //已经上传到ftp服务器

            //TODO: 2019/4/28  上传 完场，删除upload下面的文件
            targetFile.delete();

        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
       return targetFile.getName();

    }
}
