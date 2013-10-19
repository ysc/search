package org.apdplat.demo.search;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tools {

    private static final Logger LOG = LoggerFactory.getLogger(Tools.class);

    public static String getHTMLContent(String url) {
        return getHTMLContent(url, "utf-8");
    }

    public static String getHTMLContent(String url, String encoding) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream(),encoding));
            StringBuilder html = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                html.append(line).append("\n");
                line = reader.readLine();
            }
            String content = TextExtract.parse(html.toString());
            return content;
        } catch (Exception e) {
            LOG.debug("解析URL失败：" + url, e);
        }
        return null;
    }

    public static byte[] readAll(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            for (int n; (n = in.read(buffer)) > 0;) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            LOG.error("读取失败", e);
        }
        return out.toByteArray();
    }
}