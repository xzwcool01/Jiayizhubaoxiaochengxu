package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.entity.CmsGuideArticle;
import com.jiayi.mapper.CmsGuideArticleMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guide-article")
public class CmsGuideArticleController {

    private final CmsGuideArticleMapper articleMapper;

    public CmsGuideArticleController(CmsGuideArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @GetMapping("/list")
    public R<List<CmsGuideArticle>> list() {
        List<CmsGuideArticle> list = articleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CmsGuideArticle>()
                        .eq(CmsGuideArticle::getStatus, 1)
                        .orderByDesc(CmsGuideArticle::getIsHero)
                        .orderByDesc(CmsGuideArticle::getSortOrder)
                        .orderByDesc(CmsGuideArticle::getCreateTime));
        return R.ok(list);
    }

    @GetMapping("/detail")
    public R<CmsGuideArticle> detail(@RequestParam Long id) {
        CmsGuideArticle article = articleMapper.selectById(id);
        if (article == null) return R.error("文章不存在");
        articleMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CmsGuideArticle>()
                .eq(CmsGuideArticle::getId, id)
                .setSql("views = views + 1"));
        article.setViews(article.getViews() + 1);
        return R.ok(article);
    }
}
