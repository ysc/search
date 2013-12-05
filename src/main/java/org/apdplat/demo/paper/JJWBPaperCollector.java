package org.apdplat.demo.paper;

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
 * 京九晚报
 * @author 杨尚川
 */
public class JJWBPaperCollector extends AbstractPaperCollector{
    private static final String paperName = "京九晚报";
    private static final String paperPath = "http://epaper.cnsq.com.cn/jjwb/";
    private static final String url = paperPath+"html/";
    private static final String hrefPrefix = paperPath+"page/10/";
    private static final String start = "node_11.htm";
    private static final String pdfCssQuery = "html body table tbody tr td table tbody tr td table tbody tr td table tbody tr td div table tbody tr td a";
    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM/dd/"); 
    
    @Override
    public List<File> collect(Date date) {
        List<String> hrefs = new ArrayList<>();
        try {
            LOG.debug("url: "+url);
            String paper = url + sf.format(date) + start;
            LOG.debug("paper: "+paper);
            Document document = Jsoup.connect(paper).get();
            
            LOG.debug("pdfCssQuery: " + pdfCssQuery);
            Elements elements = document.select(pdfCssQuery);
            for(Element element : elements){
                String href = element.attr("href");
                if(href != null && href.endsWith(".pdf")){
                    LOG.debug("报纸链接："+href);
                    href = href.replace("../../../", "");
                    LOG.debug("报纸链接："+href);
                    hrefs.add(paperPath+href);
                }else{
                    LOG.debug("不是报纸链接："+href);
                }
            }            
        } catch (IOException ex) {
            LOG.error("采集出错",ex);
        }
        return downloadPaper(hrefs);
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
        new JJWBPaperCollector().run();
    }
}