package org.apdplat.demo.search;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xpath.internal.XPathAPI;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

public class NekoHTMLBaiduSearcher implements Searcher{
    private static final Logger LOG = LoggerFactory.getLogger(NekoHTMLBaiduSearcher.class);

    public List<String> parse(String url, String xpathExpression) {
        InputStream in = null;
        try {
            in = new URL(url).openStream();
            return parse(in, xpathExpression);
        } catch (Exception e) {
            LOG.error("错误", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOG.error("错误", e);
                }
            }
        }
        return null;
    }

    public List<String> parse(InputStream in, String xpathExpression) {
        return parse(in, xpathExpression, "UTF-8");
    }

    public List<Map<String, String>> parseMore(InputStream in, String xpathExpression) {
        return parseMore(in, xpathExpression, "UTF-8");
    }

    public List<Map<String, String>> parseMore(InputStream in, String xpathExpression, String encoding) {
        DOMParser parser = new DOMParser();
        List<Map<String, String>> list = new ArrayList<>();
        try {
            // 设置网页的默认编码
            parser.setProperty(
                    "http://cyberneko.org/html/properties/default-encoding",
                    encoding);
            /*
             * The Xerces HTML DOM implementation does not support namespaces
             * and cannot represent XHTML documents with namespace information.
             * Therefore, in order to use the default HTML DOM implementation
             * with NekoHTML's DOMParser to parse XHTML documents, you must turn
             * off namespace processing.
             */
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.parse(new InputSource(new BufferedReader(new InputStreamReader(in, encoding))));
            Document doc = parser.getDocument();
            NodeList products = XPathAPI.selectNodeList(doc, xpathExpression.toUpperCase());
            for (int i = 0; i < products.getLength(); i++) {
                Node node = products.item(i);
                String title = node.getTextContent();
                Map<String, String> map = new HashMap<>();
                map.put("title", title);
                try {
                    String href = node.getAttributes().getNamedItem("href").getTextContent();
                    map.put("href", href);
                } catch (Exception e) {
                    LOG.error("提取链接失败",e);
                }
                list.add(map);
            }
        } catch (SAXException | IOException | TransformerException | DOMException e) {
            LOG.error("错误", e);
        }
        return list;
    }

    public List<String> parse(InputStream in, String xpathExpression, String encoding) {
        DOMParser parser = new DOMParser();
        List<String> list = new ArrayList<>();
        try {
            // 设置网页的默认编码
            parser.setProperty(
                    "http://cyberneko.org/html/properties/default-encoding",
                    encoding);
            /*
             * The Xerces HTML DOM implementation does not support namespaces
             * and cannot represent XHTML documents with namespace information.
             * Therefore, in order to use the default HTML DOM implementation
             * with NekoHTML's DOMParser to parse XHTML documents, you must turn
             * off namespace processing.
             */
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.parse(new InputSource(new BufferedReader(new InputStreamReader(in, encoding))));
            Document doc = parser.getDocument();
            NodeList products = XPathAPI.selectNodeList(doc, xpathExpression.toUpperCase());
            for (int i = 0; i < products.getLength(); i++) {
                Node node = products.item(i);
                list.add(node.getTextContent());
            }
        } catch (SAXException | IOException | TransformerException | DOMException e) {
            LOG.error("错误", e);
        }
        return list;
    }

    @Override
    public List<Webpage> search(String url) {
        InputStream in = null;
        try {
            in = new URL(url).openStream();
            return search(in);
        } catch (Exception e) {
            LOG.error("错误", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOG.error("错误", e);
                }
            }
        }
        return null;
    }

    public List<Webpage> search(InputStream in) {
        //保证只读一次
        byte[] datas = Tools.readAll(in);
        if (LOG.isDebugEnabled()) {
            try {
                LOG.debug("内容：" + new String(datas, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                LOG.error("错误", e);
            }
        }

        in = new ByteArrayInputStream(datas);

        String totalXpathExpression = "//html/body/div/div/div/div[3]/p/span";
        List<String> totals = parse(in, totalXpathExpression);
        int total;
        int len = 10;
        if (totals != null && totals.size() == 1) {
            String str = totals.get(0);
            int start = 10;
            if (str.indexOf("约") != -1) {
                start = 11;
            }
            total = Integer.parseInt(str.substring(start).replace(",", "").replace("个", ""));
            LOG.info("搜索结果数：" + total);
        } else {
            return null;
        }
        if (total < 1) {
            return null;
        }
        if (total < 10) {
            len = total;
        }
        List<Webpage> webpages = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            String content = "";
            String url = "";
            String titleXpathExpression = "//html/body/div/div/div/div[3]/div[2]/table[" + (i + 1) + "]/tbody/tr/td/h3/a";
            String summaryXpathExpression = "//html/body/div/div/div/div[3]/div[2]/table[" + (i + 1) + "]/tbody/tr/td/div[1]";
            LOG.debug("titleXpathExpression:" + titleXpathExpression);
            LOG.debug("summaryXpathExpression:" + summaryXpathExpression);
            //重新构造输入流
            in = new ByteArrayInputStream(datas);
            List<String> titles = parse(in, titleXpathExpression);

            //重新构造输入流
            in = new ByteArrayInputStream(datas);
            List<Map<String, String>> titleWithHrefs = parseMore(in, titleXpathExpression);
            for (Map<String, String> titleWithHref : titleWithHrefs) {
                String title = titleWithHref.get("title");
                String href = titleWithHref.get("href");
                LOG.debug(title + " " + titleWithHref.get("href"));
                if (href != null) {
                    content = Tools.getHTMLContent(href);
                    url = href;
                } else {
                    LOG.info("页面正确提取失败");
                }
            }

            //重新构造输入流
            in = new ByteArrayInputStream(datas);
            List<String> summaries = parse(in, summaryXpathExpression);
            //处理百度知道1
            if (titles != null && titles.size() == 1 && (summaries == null || summaries.isEmpty())) {
                //重新构造输入流
                in = new ByteArrayInputStream(datas);
                String baiduZhidao1XpathExpression = "//html/body/div/div/div/div[3]/div[2]/table[" + (i + 1) + "]/tbody/tr/td/font[2]/div/div/p[2]";
                LOG.debug("baiduZhidao1XpathExpression:" + baiduZhidao1XpathExpression);
                summaries = parse(in, baiduZhidao1XpathExpression);
            }
            //处理百度知道2
            if (titles != null && titles.size() == 1 && (summaries == null || summaries.isEmpty())) {
                //重新构造输入流
                in = new ByteArrayInputStream(datas);
                String baiduZhidao2XpathExpression = "//html/body/div/div/div/div[3]/div[2]/table[" + (i + 1) + "]/tbody/tr/td/font[2]";
                LOG.debug("baiduZhidao2XpathExpression:" + baiduZhidao2XpathExpression);
                summaries = parse(in, baiduZhidao2XpathExpression);
            }
            //处理百度文库
            if (titles != null && titles.size() == 1 && (summaries == null || summaries.isEmpty())) {
                //重新构造输入流
                in = new ByteArrayInputStream(datas);
                String baiduWenkuXpathExpression = "//html/body/div/div/div/div[3]/div[2]/table[" + (i + 1) + "]/tbody/tr/td/font[1]";
                LOG.debug("baiduWenkuXpathExpression:" + baiduWenkuXpathExpression);
                summaries = parse(in, baiduWenkuXpathExpression);
            }

            if (titles != null && titles.size() == 1 && summaries != null && summaries.size() == 1) {
                Webpage webpage = new Webpage();
                webpage.setTitle(titles.get(0));
                webpage.setUrl(url);
                webpage.setSummary(summaries.get(0));
                webpage.setContent(content);
                webpages.add(webpage);
            } else {
                LOG.error("获取搜索结果列表项出错:" + titles + " - " + summaries);
            }
        }
        if(webpages.size() < 10){            
            //处理百度百科
            String titleXpathExpression = "//html/body/div/div/div/div[3]/div[2]/div/h3/a";
            String summaryXpathExpression = "//html/body/div/div/div/div[3]/div[2]/div/div/p";
            LOG.debug("处理百度百科 titleXpathExpression:" + titleXpathExpression);
            LOG.debug("处理百度百科 summaryXpathExpression:" + summaryXpathExpression);
            //重新构造输入流
            in = new ByteArrayInputStream(datas);
            List<String> titles = parse(in, titleXpathExpression);
            //重新构造输入流
            in = new ByteArrayInputStream(datas);
            List<Map<String, String>> titleWithHrefs = parseMore(in, titleXpathExpression);
            String content = "";
            String url = "";
            for (Map<String, String> titleWithHref : titleWithHrefs) {
                String title = titleWithHref.get("title");
                String href = titleWithHref.get("href");
                LOG.debug(title + " " + titleWithHref.get("href"));
                if (href != null) {
                    content = Tools.getHTMLContent(href);
                    url = href;
                } else {
                    LOG.info("页面正确提取失败");
                }
            }
            //重新构造输入流
            in = new ByteArrayInputStream(datas);
            List<String> summaries = parse(in, summaryXpathExpression);
            if (titles != null && titles.size() == 1 && summaries != null && summaries.size() == 1) {
                Webpage webpage = new Webpage();
                webpage.setTitle(titles.get(0));
                webpage.setUrl(url);
                webpage.setSummary(summaries.get(0));
                webpage.setContent(content);
                webpages.add(webpage);
            } else {
                LOG.error("获取搜索结果列表项出错:" + titles + " - " + summaries);
            }
        }
        if (webpages.isEmpty()) {
            return null;
        }
        return webpages;
    }

    public static void main(String[] args) {
        String url = "http://www.baidu.com/s?pn=0&wd=杨尚川";
        
        Searcher searcher = new NekoHTMLBaiduSearcher();
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