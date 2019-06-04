package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by xujia on 2019/4/28.
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}
