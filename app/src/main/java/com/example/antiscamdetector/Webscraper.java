package com.example.antiscamdetector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Webscraper {

    /* Learned from https://www.youtube.com/watch?v=ZtXXvtI8jcs */
    public ArrayList<Product> scrapeAmazon(String targetTitle) throws IOException{

        // Search the first page of amazon search.
        ArrayList<Product> products = new ArrayList<>();
        String url = "https://www.amazon.com/s?k=" + targetTitle + "&ref=nb_sb_noss_2";
        String itemSelector = ".sg-col-inner > .s-include-content-margin"; // Individual Product boxes

        Document page = Jsoup.connect(url).userAgent("Jsoup Scraper").get();
        Elements items = page.select(itemSelector);

        String productTitle;
        String productPrice;
        String productUrl;
        String productImageSrc;

        String titleSelector = ".a-link-normal > .a-size-medium";
        String priceSelector = ".a-offscreen";
        String urlSelector = ".a-size-mini > .a-link-normal";
        String imageSrcSelector = "img";

        for (Element item:items){
            productTitle = item.select(titleSelector).text();
            productPrice = item.select(priceSelector).text();

            // Do not include priceless items or items with names that does not include targetTitle.
            if (!productTitle.toLowerCase().contains(targetTitle) || productPrice.equals("")){
                continue;
            }
            productUrl = "https://amazon.com" + item.select(urlSelector).attr("href");
            productImageSrc =  item.select(imageSrcSelector).attr("src");

            Product product = new Product(productTitle, productPrice, productUrl, productImageSrc);
            products.add(product);
        }
        return products;
    }
    public static void main(String[] args) {

//        String targetTitle = "yu-gi-oh";
//        Webscraper Webscraper = new Webscraper();
//        ArrayList<Product> products = Webscraper.scrapeAmazon(targetTitle);
//
//        for (Product item:products){
//            item.print();
//        }
    }
}
