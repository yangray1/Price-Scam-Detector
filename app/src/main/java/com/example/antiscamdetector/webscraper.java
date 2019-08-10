package com.example.antiscamdetector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class webscraper {
    public static void main(String[] args) throws IOException {
        /* Learned from https://www.youtube.com/watch?v=ZtXXvtI8jcs */

        String url = "https://www.amazon.com/s?k=yugioh&ref=nb_sb_noss_2";
        Document page = Jsoup.connect(url).userAgent("Jsoup Scraper").get();

//        String items = ".a-link-normal > .a-size-medium";
        String itemSelector = ".sg-col-inner > .s-include-content-margin";
        Elements items = page.select(itemSelector);

        // Store product objects containing the product's name, price image and url.
        // imporevment: store ONLY THE PRODUCST THAT CONTAIN <query name> in the product's name.
        ArrayList productTitles = new ArrayList();

        // Return the data.

//        for (Element span:itemElements){
//            productTitles.add(span.text());
//        }
        System.out.println(items);

    }
}
