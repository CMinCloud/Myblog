package com.cm.controller;


import com.cm.domain.dto.CategoryListDto;
import com.cm.domain.entity.Category;
import com.cm.domain.vo.CategoryVo;
import com.cm.domain.vo.ResponseResult;
import com.cm.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseResult getCategoryById(@PathVariable("id") Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    @PutMapping
    public ResponseResult update(@RequestBody Category category) {
        return categoryService.update(category);
    }

    @PostMapping
    public ResponseResult addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    @DeleteMapping("/{ids}")
    public ResponseResult deleteById(@PathVariable("ids") List<Long> ids){
        return categoryService.deleteById(ids);
    }

    @GetMapping("/export")
    public ResponseResult exportCategories2Excel(HttpServletResponse response){
        return categoryService.export(response);
    }
}
