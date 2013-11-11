package org.apdplat.demo.search;

public interface Searcher {
    public SearchResult search(String keyword);
    public SearchResult search(String keyword, int page);
}
