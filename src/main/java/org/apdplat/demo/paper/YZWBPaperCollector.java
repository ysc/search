package org.apdplat.demo.paper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apdplat.demo.search.Tools;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 扬子晚报（未提供PDF下载）
 * @author 杨尚川
 */
public class YZWBPaperCollector implements PaperCollector{
    private static final Logger LOG = LoggerFactory.getLogger(YZWBPaperCollector.class);
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
                    href = url+href;
                    LOG.debug("type href："+href);
                    //不同的子报的pdfCssQuery都一样
                    List<String> hrefs = collect(href, pdfCssQuery);
                    files.addAll(downloadPaper(hrefs, type));
                }
            }        
        } catch (IOException ex) {
            LOG.error("采集出错",ex);
        }
        return files;
    }
    public List<String> collect(String url, String pdfCssQuery) {  
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
    private List<File> downloadPaper(List<String> hrefs, final String type){
        final List<File> files = new ArrayList<>();
        List<Thread> ts = new ArrayList<>();
        LOG.info("报纸有"+hrefs.size()+"个版面需要下载:");
        for(final String href : hrefs){   
            Thread t = new Thread(new Runnable(){
                @Override
                public void run() {
                    File file = downloadPaper(href, type);
                    if(file != null){
                        files.add(file);
                    }
                }                
            });
            t.start();
            ts.add(t);
        }
        for(Thread t : ts){
            try {
                t.join();
            } catch (InterruptedException ex) {
                LOG.error("下载报纸出错：",ex);
            }
        }
        return files;
    }
    private File downloadPaper(String href, String type){
        try{
            LOG.info("下载报纸："+href);
            LOG.debug("href："+href);
            String path = href.replace(hrefPrefix, "");
            LOG.debug("path："+path);
            String[] attrs = path.split("/");
            String pathPrefix = paperName+"/"+attrs[0]+"-"+attrs[1];
            LOG.debug("pathPrefix："+pathPrefix);
            path = pathPrefix+"/"+attrs[2]+".pdf";
            LOG.debug("path："+path);
            File dir = new File(pathPrefix);
            if(!dir.exists()){
                dir.mkdirs();
            }
            File file = new File(path);
            Tools.copyFile(new URL(href).openStream(), file);
            LOG.info("报纸下载成功："+href);
            LOG.info("报纸保存到："+file.getAbsolutePath());
            return file;
        }catch(IOException e){
            LOG.error("报纸下载失败："+href);
        }
        return null;
    }
    @Override
    public List<File> collect() {
        return collect(new Date());
    }
    public static void main(String[] args) {
        PaperCollector paperCollector = new YZWBPaperCollector();
        List<File> files = paperCollector.collect();
        int i = 1;
        for(File file : files){
            LOG.info((i++)+" : " + file.getAbsolutePath());
        }
    }
}