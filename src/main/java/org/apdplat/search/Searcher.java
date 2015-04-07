package org.apdplat.search;

public interface Searcher {
    public SearchResult search(String keyword);
    public SearchResult search(String keyword, int page);
}
