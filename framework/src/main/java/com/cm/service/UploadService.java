package com.cm.service;


import com.cm.domain.vo.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {


    ResponseResult uploadImg(MultipartFile img);
}
