package org.apdplat.search.paper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * 扬子晚报（未提供PDF下载）
 * @author 杨尚川
 */
public class YZWBPaperCollector extends AbstractPaperCollector{
    private static final String paperName = "扬子晚报";
    private static final String paperPath = "http://epaper.yzwb.net/";
    private static final String url = paperPath+"html_t/";
    private static final String hrefPrefix = paperPath+"images/";
    private static final String start = "node_1.htm";
    private static final String typeCssQuery = "html body div.middiv1 div#layer2 div.layer div#layer4 div#layer43 div#wrap div#navigation.smartmenu ul li ul li a";
    private static final String pdfCssQuery = "html body div#bmdh table tbody tr td a";
    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM/dd/"); 
    
    @Override
    public List<File> collect(Date date) {
        List<File> files = new ArrayList<>();
        try {
            LOG.debug("url: "+url);
            String paper = url + sf.format(date) + start;
            LOG.debug("paper: "+paper);
            Document document = Jsoup.connect(paper).get();
            
            LOG.debug("typeCssQuery: " + typeCssQuery);
            Elements elements = document.select(typeCssQuery);
            int i = 1;
            for(Element element : elements){
                LOG.debug("处理子报"+(i++));
                String href = element.attr("href");
                LOG.debug("type href："+href);
                if(href != null && href.endsWith(".htm")){
                    String type = element.text();
                    LOG.debug("type："+type);
                    href = href.replace("./", "");
                    href = url + sf.format(date) + href;
                    LOG.debug("type href："+href);
                    //不同的子报的pdfCssQuery都一样
                    List<String> hrefs = collect(href, pdfCssQuery);
                    files.addAll(downloadPaper(hrefs));
                }
            }        
        } catch (IOException ex) {
            LOG.error("采集出错",ex);
        }
        return files;
    }
    private List<String> collect(String url, String pdfCssQuery) {  
        List<String> hrefs = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            LOG.debug("pdfCssQuery: " + pdfCssQuery);
            Elements elements = document.select(pdfCssQuery);
            for(Element element : elements){
                String href = element.attr("href");                
                if(href != null && href.endsWith(".pdf")){
                    LOG.debug("报纸链接："+href);
                    href = href.replace("../../../", "");
                    LOG.debug("报纸链接: " + href);
                    hrefs.add(paperPath+href);
                }else{
                    LOG.debug("不是报纸链接："+href);
                }
            }    
        } catch (IOException ex) {
            LOG.error("采集出错",ex);
        }
        return hrefs;
    }
    @Override
    protected String getPath(String href) {
        String path = href.replace(hrefPrefix, "");
        String[] attrs = path.split("/");
        StringBuilder str = new StringBuilder();
        str.append(paperName)
            .append(File.separator)
            .append(attrs[0])
            .append(File.separator)
            .append(attrs[1]);
        return str.toString();
    }
    @Override
    protected String getFile(String href) {
        String path = href.replace(hrefPrefix, "");
        String[] attrs = path.split("/");
        String file = attrs[2]+".pdf";
        return file;
    }
    public static void main(String[] args) {
        new YZWBPaperCollector().run();
    }
}