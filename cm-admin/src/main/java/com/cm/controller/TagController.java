package com.cm.controller;


import com.cm.domain.dto.TagListDto;
import com.cm.domain.vo.ResponseResult;
import com.cm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/list")
//    这种直接接收参数的形式其实就是@Requestparam：  在url后接 ？xxx=&xxx=
//    这里前端和后端参数名相同，所以可以省略不写
    public ResponseResult list(Integer pageNum, Integer pageSize, TagListDto tagListDto) {
        return tagService.pageTagList(pageNum, pageSize, tagListDto);
    }

    @PostMapping
    public ResponseResult addTag(@RequestBody TagListDto tagListDto) {
        return tagService.addTag(tagListDto);
    }

    @DeleteMapping("/{id}")
//    支持批量删除
    public ResponseResult deleteTagById(@PathVariable("id") List<Long> ids) {
        return tagService.deleteTags(ids);
    }

    @GetMapping("/{id}")
    public ResponseResult getTagById(@PathVariable("id") Long id){
        return tagService.getTagById(id);
    }

    @PutMapping()
    public ResponseResult update(@RequestBody TagListDto tagListDto){
        return tagService.updateTagById(tagListDto);
    }

    @GetMapping("/listAllTag")
    public ResponseResult listAllTag(){
        return tagService.listAllTags();
    }

}
