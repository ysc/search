package org.apdplat.search.person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonCollector{
    private static final Logger LOG = LoggerFactory.getLogger(PersonCollector.class);
    private static final int PAGES = 298;

    public List<Person> collect() {
        List<Person> persons = new ArrayList<>();
        try {
            String url = "http://renwu.hexun.com/search.aspx?z=All&Filter=All&page=";
            //共298页
            for(int i=1; i<PAGES+1; i++){
                url += i;
                Document document = Jsoup.connect(url).get();
                String cssQuery = "html body div.wrap div.mainBox div.main div.contBox div.cont div.slistBox ul li a";
                LOG.debug("cssQuery: " + cssQuery);
                Elements elements = document.select(cssQuery);
                for(Element element : elements){
                    try{
                        String personName = element.text().replace(Jsoup.parse("&nbsp;").text(), " ").replace(Jsoup.parse("・").text(), "·");
                        LOG.debug("人物姓名："+personName);
                        String href = element.attr("href");
                        LOG.debug("人物链接："+href);
                        document = Jsoup.connect(href).get();
                        //基本信息
                        String basicInfoCSSQuery = "html body div.wrap div.mainBox div.main div.setBase div.right ul li";
                        LOG.debug("basicInfoCSSQuery: " + basicInfoCSSQuery);
                        Elements basicElements = document.select(basicInfoCSSQuery);
                        Map<String, String> basicInfos = new HashMap<>();
                        for(Element basicElement : basicElements){
                            String info = basicElement.text().replace(Jsoup.parse("&nbsp;").text(), " ").replace(Jsoup.parse("・").text(), "·");
                            if(info != null){
                                String[] attrs = info.split("：");
                                if(attrs != null && attrs.length == 2){
                                    basicInfos.put(attrs[0], attrs[1]);
                                }
                            }
                        }
                        String moreCSSQuery = "html body div.wrap div.mainBox div.main div.contBox";
                        LOG.debug("moreCSSQuery: " + moreCSSQuery);
                        Elements moreElements = document.select(moreCSSQuery);
                        //教育经历
                        List<String> educations = new ArrayList<>();
                        Elements educationElements = moreElements.get(0).select("div.cont p");
                        for(Element educationElement : educationElements){
                            String education = educationElement.text().replace(Jsoup.parse("&nbsp;").text(), " ").replace(Jsoup.parse("・").text(), "·");
                            if(education != null && !"".equals(education.trim())){
                                educations.add(education);
                            }
                        }                        
                        //工作经历
                        List<String> jobs = new ArrayList<>();
                        Elements jobElements = moreElements.get(1).select("div.cont p");
                        for(Element jobElement : jobElements){
                            String job = jobElement.text().replace(Jsoup.parse("&nbsp;").text(), " ").replace(Jsoup.parse("・").text(), "·");
                            if(job != null && !"".equals(job.trim())){
                                jobs.add(job);
                            }
                        }                        
                        //重要事件
                        List<String> importants = new ArrayList<>();
                        Elements importantElements = moreElements.get(4).select("div.cont p");
                        for(Element importantElement : importantElements){
                            String important = importantElement.text().replace(Jsoup.parse("&nbsp;").text(), " ").replace(Jsoup.parse("・").text(), "·");
                            if(important != null && !"".equals(important.trim())){
                                importants.add(important);
                            }
                        }

                        Person person = new Person();
                        person.setName(personName);
                        person.setBasicInfos(basicInfos);
                        person.setEducations(educations);
                        person.setJobs(jobs);
                        person.setImportants(importants);
                        persons.add(person);
                    }catch(IOException e){
                        LOG.error("采集出错",e);
                    }
                }
            }            
        } catch (IOException ex) {
            LOG.error("采集出错",ex);
        }
        return persons;
    }

    public static void main(String[] args) {
        PersonCollector personCollector = new PersonCollector();
        List<Person> persons = personCollector.collect();
        if (persons != null) {
            int i = 1;
            for (Person person : persons) {
                LOG.info("采集结果 " + (i++) + " "+person.getName()+ " ：");
                
                if(person.getBasicInfos() != null && person.getBasicInfos().size() > 0){        
                    LOG.info("基本信息************************************************************");
                    for(Entry<String, String> basicInfo : person.getBasicInfos().entrySet()){
                        LOG.info(basicInfo.getKey() +"：" + basicInfo.getValue());
                    }
                }
                if(person.getEducations() != null && person.getEducations().size() > 0){                    
                    LOG.info("");
                    LOG.info("教育经历************************************************************");
                    for(String education : person.getEducations()){
                        LOG.info(education);
                    }
                }
                if(person.getJobs() != null && person.getJobs().size() > 0){
                    LOG.info("");
                    LOG.info("工作经历************************************************************");
                    for(String job : person.getJobs()){
                        LOG.info(job);
                    }
                }
                if(person.getImportants() != null && person.getImportants().size() > 0){
                    LOG.info("");
                    LOG.info("重要事件************************************************************");
                    for(String important : person.getImportants()){
                        LOG.info(important.replace("\\?", " "));
                    }
                }
                LOG.info("");
                LOG.info("");
            }
        } else {
            LOG.error("没有采集到结果");
        }
    }
}