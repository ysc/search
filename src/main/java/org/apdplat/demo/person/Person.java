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

package org.apdplat.demo.person;

import java.util.List;
import java.util.Map;

/**
 *
 * @author 杨尚川
 */
public class Person {
    // 姓名
    private String name;
    //基本信息
    private Map<String, String> basicInfos;
    //教育经历
    List<String> educations;
    //工作经历
    List<String> jobs;
    //重要事件
    List<String> importants;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Map<String, String> getBasicInfos() {
        return basicInfos;
    }
    public void setBasicInfos(Map<String, String> basicInfos) {
        this.basicInfos = basicInfos;
    }
    public List<String> getEducations() {
        return educations;
    }
    public void setEducations(List<String> educations) {
        this.educations = educations;
    }
    public List<String> getJobs() {
        return jobs;
    }
    public void setJobs(List<String> jobs) {
        this.jobs = jobs;
    }
    public List<String> getImportants() {
        return importants;
    }
    public void setImportants(List<String> importants) {
        this.importants = importants;
    }
}
