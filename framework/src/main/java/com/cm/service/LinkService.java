package com.cm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cm.domain.dto.LinkDto;
import com.cm.domain.entity.Link;
import com.cm.domain.vo.LinkVo;
import com.cm.domain.vo.ResponseResult;

/**
 * 友链(Link)表服务接口
 *
 * @author makejava
 * @since 2023-01-02 22:03:02
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();

    ResponseResult pageList(LinkDto linkDto);

    ResponseResult newLink(Link link);

    ResponseResult linkDetail(Long linkId);

    ResponseResult update(Link linkDto);
}

