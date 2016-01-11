/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.entity;

import java.util.List;

/**
 *
 * @author sonnh4
 */
public class News {
    
    private List<NewsDetail> news;

    public List<NewsDetail> getNews() {
        return news;
    }

    public void setNews(List<NewsDetail> news) {
        this.news = news;
    }
    
}
