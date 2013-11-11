package org.apdplat.demo.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSoupBaiduSearcher implements Searcher{
    private static final Logger LOG = LoggerFactory.getLogger(JSoupBaiduSearcher.class);

    @Override
    public List<Webpage> search(String url) {
        List<Webpage> webpages = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            
            //获取搜索结果数目
            int total = getBaiduSearchResultCount(document);
            int len = 10;
            if (total < 1) {
                return null;
            }
            //如果搜索到的结果不足一页
            if (total < 10) {
                len = total;
            }
            for (int i = 0; i < len; i++) {
                String titleCssQuery = "html body div#out div#in div#wrapper div#container div#content_left table#" + (i + 1) + ".result tbody tr td.c-default h3.t a";
                String summaryCssQuery = "html body div#out div#in div#wrapper div#container div#content_left table#" + (i + 1) + ".result tbody tr td.c-default div.c-abstract";
                LOG.debug("titleCssQuery:" + titleCssQuery);
                LOG.debug("summaryCssQuery:" + summaryCssQuery);
                Element titleElement = document.select(titleCssQuery).first();
                String href = "";
                String titleText = "";
                if(titleElement != null){
                    titleText = titleElement.text();
                    href = titleElement.attr("href");
                }else{
                    //处理百度百科
                    titleCssQuery = "html body div#out div#in div#wrapper div#container div#content_left div#1.result-op h3.t a";
                    summaryCssQuery = "html body div#out div#in div#wrapper div#container div#content_left div#1.result-op div p";
                    LOG.debug("处理百度百科 titleCssQuery:" + titleCssQuery);
                    LOG.debug("处理百度百科 summaryCssQuery:" + summaryCssQuery);
                    titleElement = document.select(titleCssQuery).first();
                    if(titleElement != null){
                        titleText = titleElement.text();
                        href = titleElement.attr("href");
                    }
                }
                LOG.debug(titleText);
                Element summaryElement = document.select(summaryCssQuery).first();
                //处理百度知道
                if(summaryElement == null){
                    summaryCssQuery = summaryCssQuery.replace("div.c-abstract","font");
                    LOG.debug("处理百度知道 summaryCssQuery:" + summaryCssQuery);
                    summaryElement = document.select(summaryCssQuery).first();
                }
                String summaryText = "";
                if(summaryElement != null){
                    summaryText = summaryElement.text(); 
                }
                LOG.debug(summaryText);                
                
                if (titleText != null && !"".equals(titleText.trim()) && summaryText != null && !"".equals(summaryText.trim())) {
                    Webpage webpage = new Webpage();
                    webpage.setTitle(titleText);
                    webpage.setUrl(href);
                    webpage.setSummary(summaryText);
                    if (href != null) {
                        String content = Tools.getHTMLContent(href);
                        webpage.setContent(content);
                    } else {
                        LOG.info("页面正确提取失败");
                    }
                    webpages.add(webpage);
                } else {
                    LOG.error("获取搜索结果列表项出错:" + titleText + " - " + summaryText);
                }
            }
            
            
        } catch (IOException ex) {
            LOG.error("搜索出错",ex);
        }
        return webpages;
    }
    /**
     * 获取百度搜索结果数
     * 获取如下文本并解析数字：
     * 百度为您找到相关结果约13,200个
     * @param document 文档
     * @return 结果数
     */
    private int getBaiduSearchResultCount(Document document){
        String cssQuery = "html body div#out div#in div#wrapper div#container p#page span.nums";
        LOG.debug("total cssQuery: " + cssQuery);
        Element totalElement = document.select(cssQuery).first();
        String totalText = totalElement.text(); 
        LOG.info("搜索结果文本：" + totalText);
        
        String regEx="[^0-9]";   
        Pattern pattern = Pattern.compile(regEx);      
        Matcher matcher = pattern.matcher(totalText);
        totalText = matcher.replaceAll("");
        int total = Integer.parseInt(totalText);
        LOG.info("搜索结果数：" + total);
        return total;
    }

    public static void main(String[] args) {
        String url = "http://www.baidu.com/s?pn=0&wd=杨尚川";
        
        Searcher searcher = new JSoupBaiduSearcher();
        List<Webpage> webpages = searcher.search(url);
        if (webpages != null) {
            int i = 1;
            for (Webpage webpage : webpages) {
                LOG.info("搜索结果 " + (i++) + " ：");
                LOG.info("标题：" + webpage.getTitle());
                LOG.info("URL：" + webpage.getUrl());
                LOG.info("摘要：" + webpage.getSummary());
                LOG.info("正文：" + webpage.getContent());
                LOG.info("");
            }
        } else {
            LOG.error("没有搜索到结果");
        }
    }
}