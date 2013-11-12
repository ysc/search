/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apdplat.search.util.baidu;

import java.util.List;
import org.apdplat.demo.search.Webpage;

/**
 * @author JONE
 * @mail 858305351@qq.com
 * @time 2013-11-12
 * @description 百度搜索结果实体
 */
public class BaiduModel {
    // 第几页
    private int page;
    // 一页中所有的解析实体
    private List<Webpage> webpages;

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return the webpages
     */
    public List<Webpage> getWebpages() {
        return webpages;
    }

    /**
     * @param webpages the webpages to set
     */
    public void setWebpages(List<Webpage> webpages) {
        this.webpages = webpages;
    }
}
