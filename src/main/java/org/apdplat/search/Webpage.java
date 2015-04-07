package org.apdplat.search;
/**
 * 网页解析实体
 */
public class Webpage {
        // 标题
	private String title;
        // 链接
	private String url;
        // 简介
	private String summary;
        // 正文内容
	private String content;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
