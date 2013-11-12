/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.demo.search;

/**
 *
 * @author 杨尚川
 */
public abstract class AbstractBaiduSearcher implements BaiduSearcher {
    /**
     * 新闻搜索
     * @param keyword
     * @return 
     */
    @Override
    public SearchResult searchNews(String keyword){
        return searchNews(keyword, 1);
    }
    /**
     * 新闻搜索（分页）
     * @param keyword
     * @param page
     * @return 
     */
    @Override
    public SearchResult searchNews(String keyword, int page){
        throw new RuntimeException("未实现");
    }
    /**
     * 贴吧搜索
     * @param keyword
     * @return 
     */
    @Override
    public SearchResult searchTieba(String keyword){
        return searchTieba(keyword, 1);
    }
    /**
     * 贴吧搜索（分页）
     * @param keyword
     * @param page
     * @return 
     */
    @Override
    public SearchResult searchTieba(String keyword, int page){
        throw new RuntimeException("未实现");
    }
    /**
     * 知道搜素
     * @param keyword
     * @return 
     */
    @Override
    public SearchResult searchZhidao(String keyword){
        return searchZhidao(keyword, 1);
    }
    /**
     * 知道搜索（分页）
     * @param keyword
     * @param page
     * @return 
     */
    @Override
    public SearchResult searchZhidao(String keyword, int page){
        throw new RuntimeException("未实现");
    }
    /**
     * 文库搜索
     * @param keyword
     * @return 
     */
    @Override
    public SearchResult searchWenku(String keyword){
        return searchWenku(keyword, 1);
    }
    /**
     * 文库搜索（分页）
     * @param keyword
     * @param page
     * @return 
     */
    @Override
    public SearchResult searchWenku(String keyword, int page){
        throw new RuntimeException("未实现");
    }
}
