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

package org.apdplat.search;

import java.util.List;

/**
 *搜索结果
 * @author 杨尚川
 */
public class SearchResult {
    //总的搜索结果数
    private int total;
    //第几页
    private int page;
    //页面数据
    private List<Webpage> webpages;
    
    public int getPageSize(){
        return webpages.size();
    }

    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public List<Webpage> getWebpages() {
        return webpages;
    }
    public void setWebpages(List<Webpage> webpages) {
        this.webpages = webpages;
    }
}
