package com.zbw.fame.controller.admin;

import com.github.pagehelper.Page;
import com.zbw.fame.controller.BaseController;
import com.zbw.fame.model.domain.Article;
import com.zbw.fame.model.dto.Pagination;
import com.zbw.fame.model.domain.User;
import com.zbw.fame.service.ArticleService;
import com.zbw.fame.service.LogService;
import com.zbw.fame.util.FameConsts;
import com.zbw.fame.util.FameUtil;
import com.zbw.fame.util.RestResponse;
import com.zbw.fame.util.Types;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 自定义页面管理 Controller
 *
 * @author zbw
 * @since 2017/10/17 12:28
 */
@RestController
@RequestMapping("/api/admin/page")
public class PageController extends BaseController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private LogService logService;

    /**
     * 自定义页面列表
     *
     * @param page  第几页
     * @param limit 每页数量
     * @return {@see Pagination<Article>}
     */
    @GetMapping
    public RestResponse index(@RequestParam(required = false, defaultValue = "1") Integer page,
                              @RequestParam(required = false, defaultValue = FameConsts.PAGE_SIZE) Integer limit) {
        Page<Article> pages = articleService.getAdminPages(page, limit);
        return RestResponse.ok(new Pagination<Article>(pages));
    }

    /**
     * 获取自定义页面信息
     *
     * @param id 自定义页面id
     * @return {@see Article}
     */
    @GetMapping("{id}")
    public RestResponse showPage(@PathVariable Integer id) {
        Article page = articleService.getAdminPage(id);
        if (null == page) {
            return this.error404();
        }
        return RestResponse.ok(page);
    }

    /**
     * 新建或修改自定义页面
     *
     * @param id      自定义页面id
     * @param title   标题
     * @param content 内容
     * @param status  {@link Types#DRAFT},{@link Types#PUBLISH}
     * @return {@see String}
     */
    @PostMapping
    public RestResponse savePage(@RequestParam(value = "id", required = false) Integer id,
                                 @RequestParam(value = "title") String title,
                                 @RequestParam(value = "content") String content,
                                 @RequestParam(value = "status", defaultValue = Types.DRAFT) String status) {
        User user = this.user();
        Article page = new Article();
        if (!StringUtils.isEmpty(id)) {
            page.setId(id);
        }
        page.setTitle(title);
        page.setContent(content);
        page.setStatus(status);
        page.setAuthorId(user.getId());
        articleService.savePage(page);
        return RestResponse.ok("保存文章成功");
    }

    /**
     * 删除自定义页面
     *
     * @param id 自定义页面id
     * @return {@see String}
     */
    @DeleteMapping("{id}")
    public RestResponse deletePage(@PathVariable Integer id) {
        if (articleService.deletePage(id)) {
            logService.save(Types.LOG_ACTION_DELETE, "id:" + id, Types.LOG_MESSAGE_DELETE_PAGE, Types.LOG_TYPE_OPERATE, FameUtil.getIp());
            return RestResponse.ok("删除自定义页面成功");
        } else {
            return RestResponse.fail();
        }
    }
}
