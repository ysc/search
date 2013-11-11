/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apdplat.demo.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author JONE
 * @mail 858305351@qq.com
 * @time 2013-11-11
 * @description 通过Jsoup 获取百度搜索结果的基本信息
 */
public class JsoupBaiduInfoUtil {
    private static final Logger LOG = LoggerFactory.getLogger(JsoupBaiduInfoUtil.class);
    private Document document = null;
    /**
     * 百度搜索结果：百度为您找到相关结果约13,100个
     */
    private static final String cssQuery = "html body div#out div#in div#wrapper div#container p#page span.nums";

    /**
     * @author JONE
     * @param url 
     * @throws java.io.IOException 
     * @time 2013-11-11
     * @description 构造器
     */
    public JsoupBaiduInfoUtil( String url) throws IOException{
        this.document = Jsoup.connect(url).get();
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
       return m.replaceAll("").trim();
    }
    /**
     * @author JONE
     * @param cssQuery
     * @return String
     * @time 2013-11-11
     * @description 获取百度搜索结果：13100
     */
    public String getResultsCount(String cssQuery){
       String resultsCountText = this.getResultsCountText(cssQuery);
       if(StringUtils.isEmpty(StringUtils.trim(resultsCountText))){
           return "";
       }
       String regEx="[^0-9]";   
       Pattern p = Pattern.compile(regEx);      
       Matcher m = p.matcher(resultsCountText);      
       return m.replaceAll("").trim();
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
    /**
     * @author JONE
     * @param cssQuery
     * @return String
     * @time 2013-11-11
     * @description 获取百度搜索结果：百度为您找到相关结果约13,100个
     */
    public String getResultsCountText(String cssQuery){
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

