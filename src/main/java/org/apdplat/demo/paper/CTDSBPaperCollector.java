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
 * 楚天都市报
 * @author 杨尚川
 */
public class CTDSBPaperCollector extends AbstractPaperCollector{
    private static final String paperName = "楚天都市报";
    private static final String host = "http://ctdsb.cnhubei.com/";
    private static final String paperPath = host+"ctdsb/";
    private static final String url = host+"html/ctdsb/";
    private static final String hrefPrefix = paperPath;
    private static final String start = "index.html";
    private static final String pdfCssQuery = "html body center table tbody tr td table tbody tr td table tbody tr td table tbody tr td div table tbody tr td.info3 a";
    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd/"); 
    
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
            int count=0;
            for(Element element : elements){
                String text = element.text();
                if(text != null && text.startsWith("第")){
                    LOG.debug("报纸文本："+text);
                    count++;
                }else{
                    LOG.debug("不是报纸文本："+text);
                }
            }
            //有的版面缺失，而文件名是顺序递增的
            for(int i=1; i<=count; i++){
                String seq = Integer.toString(i);
                if(i<10){
                    seq="0"+seq;
                }
                hrefs.add(paperPath + sf.format(date) + "page_"+seq+".jpg");
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
            .append(attrs[0].substring(0, 4))
            .append("-")
            .append(attrs[0].substring(4, 6))
            .append(File.separator)
            .append(attrs[0].substring(6, 8));
        return str.toString();
    }
    @Override
    protected String getFile(String href) {
        String path = href.replace(hrefPrefix, "");
        String[] attrs = path.split("/");
        String file = attrs[1].split("_")[1];
        return file;
    }
    public static void main(String[] args) {
        new CTDSBPaperCollector().run();
    }
}