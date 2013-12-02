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
 * 今日早报
 * @author 杨尚川
 */
public class JRZBPaperCollector implements PaperCollector{
    private static final Logger LOG = LoggerFactory.getLogger(JRZBPaperCollector.class);
    private static final String paperName = "今日早报";
    private static final String paperPath = "http://jrzb.zjol.com.cn/";
    private static final String url = paperPath+"html/";
    private static final String hrefPrefix = paperPath+"images/";
    private static final String start = "node_142.htm";
    private static final String pdfCssQuery = "html body div.main div.main-ednav div.main-ednav-nav dl dd img";
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
                String href = element.attr("filepath");
                if(href != null && href.endsWith(".jpg")){
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
    private List<File> downloadPaper(List<String> hrefs){
        final List<File> files = new ArrayList<>();
        List<Thread> ts = new ArrayList<>();
        LOG.info("报纸有"+hrefs.size()+"个版面需要下载:");
        for(final String href : hrefs){   
            Thread t = new Thread(new Runnable(){
                @Override
                public void run() {
                    File file = downloadPaper(href);
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
    private File downloadPaper(String href){
        try{
            LOG.info("下载报纸："+href);
            LOG.debug("href："+href);
            String path = href.replace(hrefPrefix, "");
            LOG.debug("path："+path);
            String[] attrs = path.split("/");
            String pathPrefix = paperName+"/"+attrs[0]+"-"+attrs[1];
            LOG.debug("pathPrefix："+pathPrefix);
            path = pathPrefix+"/"+attrs[2]+".jpg";
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
        PaperCollector paperCollector = new JRZBPaperCollector();
        List<File> files = paperCollector.collect();
        int i = 1;
        for(File file : files){
            LOG.info((i++)+" : " + file.getAbsolutePath());
        }
        //昨天
        Date date = new Date();
        date.setTime(System.currentTimeMillis()-24*3600*1000);
        files = paperCollector.collect(date);
        i = 1;
        for(File file : files){
            LOG.info((i++)+" : " + file.getAbsolutePath());
        }
        //前天
        date = new Date();
        date.setTime(System.currentTimeMillis()-2*24*3600*1000);
        files = paperCollector.collect(date);
        i = 1;
        for(File file : files){
            LOG.info((i++)+" : " + file.getAbsolutePath());
        }
    }
}