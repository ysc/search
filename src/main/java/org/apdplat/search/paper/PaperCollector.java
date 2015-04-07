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
import java.util.Date;
import java.util.List;

/**
 *报纸采集器
 * @author 杨尚川
 */
public interface PaperCollector {
    /**
     * 下载当日报纸，一个文件对应一个版面
     * @return 报纸
     */
    List<File> collect();
    /**
     * 下载指定日期的报纸，一个文件对应一个版面
     * @param date 指定日期
     * @return 报纸
     */
    List<File> collect(Date date);
}
