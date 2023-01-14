package com.cm.controller;


import com.cm.domain.dto.CategoryListDto;
import com.cm.domain.vo.ResponseResult;
import com.cm.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content/category")
public class CategroyController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/listAllCategory")
    public ResponseResult listAllCategories() {
        return categoryService.listAllCategories();
    }

    @GetMapping("/list")
    public ResponseResult pageList(CategoryListDto categoryListDto) {
        return categoryService.listByPage(categoryListDto);
    }
}
