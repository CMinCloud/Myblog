package com.cm.controller;

import com.cm.domain.vo.ResponseResult;
import com.cm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：CM
 * @Package：com.cm.controller
 * @Project：Myblog
 * @name：TagController
 * @Date：2023/5/22 11:27
 * @Filename：TagController
 */
@RequestMapping("tag")
@RestController
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/listAllTag")
    public ResponseResult listAllTag() {
        return tagService.listAllTags();
    }

}
