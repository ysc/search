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
 * 信息时报
 * @author 杨尚川
 */
public class XXSBPaperCollector implements PaperCollector{
    private static final Logger LOG = LoggerFactory.getLogger(XXSBPaperCollector.class);
    private static final String paperName = "信息时报";
    private static final String host = "http://informationtimes.dayoo.com/";
    private static final String paperPath = host+"page/1019/";
    private static final String url = host+"html/";
    private static final String hrefPrefix = paperPath;
    private static final String start = "node_1019.htm";
    private static final String pdfCssQuery = "html body#content div.container div.leftcolumn div.leftcolumncontent div.pagebuttontwo div.con p.right span.dfive a";
    private static final String subCssQuery = "html body#listcontent div.container div.rightcolumn div.subcbga div.listcontent div#all_article_list.list h4 span.left a";
    private static final String contentCssQuery = "html body div.container div.leftcolumn div.tbga div.bbga div.cbga div.left div.pagepicture div map area";
    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM/dd/"); 
    
    @Override
    public List<File> collect(Date date) {
        List<String> hrefs = new ArrayList<>();
        try {
            LOG.debug("url: "+url);
            String paper = url + sf.format(date) + start;
            LOG.debug("paper: "+paper);
            Document document = Jsoup.connect(paper).get();
            
            //1、找到子报纸
            LOG.debug("subCssQuery: " + subCssQuery);
            Elements elements = document.select(subCssQuery);
            for(Element element : elements){
                String text = element.text();
                String href = element.attr("href");
                if(text != null && text.contains("：") && href != null && href.endsWith(".htm")){
                    String subPaperURL = url + sf.format(date) + href;
                    LOG.debug("子报纸文本："+text+" , "+href);
                    LOG.debug("subPaperURL："+subPaperURL);
                    //2、找到内容页面
                    LOG.debug("contentCssQuery: " + contentCssQuery);
                    Elements contentElements = Jsoup.connect(subPaperURL).get().select(contentCssQuery);
                    for(Element contentElement : contentElements){
                        String h = contentElement.attr("href");
                        if(h != null && h.startsWith("content_") && h.endsWith(".htm")){
                            String contentURL = url + sf.format(date) + h;
                            LOG.debug("contentURL："+contentURL);
                            //3、找PDF
                            LOG.debug("pdfCssQuery: " + pdfCssQuery);
                            Elements pdfElements = Jsoup.connect(contentURL).get().select(pdfCssQuery);
                            for(Element pdfElement : pdfElements){
                                String pdf = pdfElement.attr("href");
                                if(pdf != null && pdf.endsWith(".pdf")){
                                    LOG.debug("报纸链接："+pdf);
                                    pdf = pdf.replace("../../../", "");
                                    LOG.debug("报纸链接："+pdf);
                                    hrefs.add(host+pdf);
                                }else{
                                    LOG.debug("不是报纸链接："+pdf);
                                }
                            }
                            //有多个content，选择一个即可
                            break;
                        }
                    }
                }else{
                    LOG.debug("不是子报纸文本："+text+" , "+href);
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
        PaperCollector paperCollector = new XXSBPaperCollector();
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