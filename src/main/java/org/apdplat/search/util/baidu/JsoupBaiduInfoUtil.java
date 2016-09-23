/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apdplat.search.util.baidu;

import org.apache.commons.lang3.StringUtils;
import org.apdplat.search.SearchResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author JONE
 * @mail 858305351@qq.com
 * @time 2013-11-11
 * @description 通过Jsoup 获取百度搜索结果的基本信息
 */
public class JsoupBaiduInfoUtil {
    private static final Logger LOG = LoggerFactory.getLogger(JsoupBaiduInfoUtil.class);
    private Document document = null;
    private SearchResult baiduModels = new SearchResult();
    private String url = "http://www.baidu.com/s";
    /**
     * 百度搜索结果：百度为您找到相关结果约13,100个
     */
    private static final String cssQuery = "div.nums";
    /**
     * 解析标题
     */
    String titleCssQuery = "html body div#out div#in div#wrapper div#container div#content_left table#" + "tableNum" + ".result tbody tr td.c-default h3.t a";
    /**
     * 解析简介
     */
    String summaryCssQuery = "html body div#out div#in div#wrapper div#container div#content_left table#" + "tableNum" + ".result tbody tr td.c-default div.c-abstract";
    /**
     * @author JONE
     * @param name 需要查询的字段
     * @param page
     * @throws java.io.IOException 
     * @time 2013-11-11
     * @description 构造器
     */
    public JsoupBaiduInfoUtil( String name,int page) throws IOException{
        if(StringUtils.isEmpty(StringUtils.trim(name)) || 0 >= page){
            throw new NullPointerException();
         }
        this.document = Jsoup.connect(url).data("wd", name).data("pn", String.valueOf((page-1)*10)).get();
    }
     /**
     * @author JONE
     * @return String
     * @time 2013-11-11
     * @description 获取百度搜索结果：13100
     */
    public String getResultsCount(){
       String resultsCountText = this.getResultsCountText();
       if(StringUtils.isEmpty(StringUtils.trim(resultsCountText))){
           return "";
       }
       String regEx="[^0-9]";   
       Pattern p = Pattern.compile(regEx);      
       Matcher m = p.matcher(resultsCountText); 
       String totalCount = m.replaceAll("").trim();
       baiduModels.setTotal(Integer.parseInt(totalCount));
       return totalCount;
    }
    
    /**
     * @author JONE
     * @return String
     * @time 2013-11-11
     * @description 获取百度搜索结果：百度为您找到相关结果约13,100个
     */
    public String getResultsCountText(){
        if(null == document){
            return "";
        }
         LOG.debug("total cssQuery: " + cssQuery);
         Element totalElement = document.select(cssQuery).first();
         String totalText = totalElement.text(); 
         LOG.info("搜索结果：" + totalText);
         return totalText;
    }
}

