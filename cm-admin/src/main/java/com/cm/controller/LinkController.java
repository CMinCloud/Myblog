package com.cm.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cm.domain.dto.LinkDto;
import com.cm.domain.entity.Link;
import com.cm.domain.vo.LinkVo;
import com.cm.domain.vo.ResponseResult;
import com.cm.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/content/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @GetMapping("/list")
    public ResponseResult pageList(LinkDto linkDto) {
        return linkService.pageList(linkDto);
    }

    @PostMapping
    public ResponseResult addLink(@RequestBody Link link) {
        return linkService.newLink(link);
    }

    @GetMapping("/{id}")
    public ResponseResult linkDetail(@PathVariable("id") Long linkId) {
        return linkService.linkDetail(linkId);
    }

    @PutMapping
    public ResponseResult update(@RequestBody Link linkDto) {
        return linkService.update(linkDto);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteLink(@PathVariable("id") Long linkId) {
        boolean isRemoved = linkService.removeById(linkId);
        if (isRemoved)
            return ResponseResult.okResult();
        else
            return ResponseResult.errorResult(555, "友链删除失败");
    }


    @PutMapping("/{id}")
    public ResponseResult changeLinkStatus(@RequestBody LinkVo linkVo) {
        UpdateWrapper<Link> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", linkVo.getId())
                .set("status", linkVo.getStatus());
        boolean updated = linkService.update(updateWrapper);
        if (updated)
            return ResponseResult.okResult();
        else
            return ResponseResult.errorResult(555, "审核状态修改失败");
    }
}
