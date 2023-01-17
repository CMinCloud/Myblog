package com.cm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.constants.SystemConstants;
import com.cm.domain.dto.LinkDto;
import com.cm.domain.entity.Category;
import com.cm.domain.entity.Link;
import com.cm.domain.entity.SystemException;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.vo.CategoryVo;
import com.cm.domain.vo.PageVo;
import com.cm.domain.vo.ResponseResult;
import com.cm.domain.vo.LinkVo;
import com.cm.mapper.LinkMapper;
import com.cm.service.LinkService;
import com.cm.utils.BeanCopyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author makejava
 * @since 2023-01-02 22:03:02
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    /**
     * 前端查询所有友链信息
     *
     * @return
     */
    @Override
    public ResponseResult getAllLink() {
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<Link> list = list(queryWrapper);
        List<LinkVo> linkVoList = BeanCopyUtils.copyBeanList(list, LinkVo.class);

        return ResponseResult.okResult(linkVoList);
    }

    /**
     * 后台查询友链列表
     *
     * @param linkDto
     */
    @Override
    public ResponseResult pageList(LinkDto linkDto) {
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        String name = linkDto.getName();
        String status = linkDto.getStatus();
//       根据友链名称进行模糊查询，根据状态进行查询
        queryWrapper.like(StringUtils.hasText(name), Link::getName, name);
        queryWrapper.eq(StringUtils.hasText(status), Link::getStatus, status);
        queryWrapper.orderByAsc(Link::getId);
        Page<Link> linkPage = new Page<>(linkDto.getPageNum(), linkDto.getPageSize());
        Page<Link> page = page(linkPage, queryWrapper);
//        获取分页查询结果
        List<Link> records = page.getRecords();
        long total = page.getTotal();
        List<LinkVo> linkVoList = BeanCopyUtils.copyBeanList(records, LinkVo.class);
        PageVo pageVo = new PageVo(linkVoList, total);
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 新增友链
     * @param link
     * @return
     */
    @Override
    public ResponseResult newLink(Link link) {
        boolean isIllegal = checkKeyWords(link);
        if (isIllegal) {
            save(link);
        }
        return ResponseResult.okResult();
    }

    /**
     * 根据id获取友链详情
     *
     * @param linkId
     * @return
     */
    @Override
    public ResponseResult linkDetail(Long linkId) {
        Link link = getById(linkId);
        LinkVo linkVo = BeanCopyUtils.copyBean(link, LinkVo.class);
        return ResponseResult.okResult(linkVo);
    }

    /**
     * 更新友链信息
     *
     * @param linkDto
     * @return
     */
    @Override
    public ResponseResult update(Link linkDto) {
        boolean updated = updateById(linkDto);
        if (updated)
            return ResponseResult.okResult();
        else
//            系统错误：数据库更新失败
            return ResponseResult.errorResult(555, "友链更新失败");
    }

    private boolean checkKeyWords(Link link) {
        if (!StringUtils.hasText(link.getName())
                || !StringUtils.hasText(link.getDescription())
                || !StringUtils.hasText(link.getAddress())
                || !StringUtils.hasText(link.getLogo())
                || !StringUtils.hasText(link.getStatus())
        ) {
            throw new SystemException(AppHttpCodeEnum.KEYWORDS_NOT_NULL);
        }
        return true;
    }
}

