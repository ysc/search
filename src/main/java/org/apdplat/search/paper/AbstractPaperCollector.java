/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.search.paper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apdplat.search.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 杨尚川
 */
public abstract class AbstractPaperCollector implements PaperCollector{
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public List<File> collect() {
        return collect(new Date());
    }
    /**
     * 根据下载链接提取文件夹名称
     * @param href 下载链接
     * @return 文件夹名称
     */
    protected abstract String getPath(String href);
    /**
     * 根据下载链接提取文件名称
     * @param href 下载链接
     * @return 文件名称
     */
    protected abstract String getFile(String href);
    protected List<File> downloadPaper(List<String> hrefs){
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
    protected File downloadPaper(String href){
        try{
            LOG.info("下载报纸："+href);
            String path = getPath(href);
            LOG.debug("报纸保存目录："+path);
            String file = getFile(href);
            LOG.debug("报纸保存文件："+file);
            File dir = new File(path);
            if(!dir.exists()){
                LOG.debug("创建目录："+dir.getAbsolutePath());
                dir.mkdirs();
            }
            File absoluteFile = new File(path, file);
            LOG.debug("报纸保存绝对路径："+absoluteFile.getAbsolutePath());
            Tools.copyFile(new URL(href).openStream(), absoluteFile);
            LOG.info("报纸下载成功："+href);
            LOG.info("报纸成功保存到："+absoluteFile.getAbsolutePath());
            return absoluteFile;
        }catch(IOException e){
            LOG.error("报纸下载失败："+e);
        }
        return null;
    }    
    protected void run() {
        //今天
        List<File> files = collect();
        int i = 1;
        for(File file : files){
            LOG.info((i++)+" : " + file.getAbsolutePath());
        }
        //昨天
        Date date = new Date();
        date.setTime(System.currentTimeMillis()-24*3600*1000);
        files = collect(date);
        i = 1;
        for(File file : files){
            LOG.info((i++)+" : " + file.getAbsolutePath());
        }
        //前天
        date = new Date();
        date.setTime(System.currentTimeMillis()-2*24*3600*1000);
        files = collect(date);
        i = 1;
        for(File file : files){
            LOG.info((i++)+" : " + file.getAbsolutePath());
        }
    }
}
