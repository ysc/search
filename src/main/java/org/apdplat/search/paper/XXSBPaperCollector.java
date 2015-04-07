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
 * 信息时报
 * @author 杨尚川
 */
public class XXSBPaperCollector extends AbstractPaperCollector{
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
        new XXSBPaperCollector().run();
    }
}