package com.example.pricescamdetector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Webscraper {

    private final static int NUM_PRODUCTS_TO_SCRAPE = 5;

    /*
     * Learned from https://www.youtube.com/watch?v=ZtXXvtI8jcs
     * Documentation: https://aboullaite.me/jsoup-html-parser-tutorial-examples/
     **/
    public ArrayList<Product> scrapeAmazon(String targetTitleForUrl, String originalTargetTitle)
            throws IOException{

        // Search the first page of amazon search.
        ArrayList<Product> products = new ArrayList<>();
        String url = "https://www.amazon.com/s?k=" + targetTitleForUrl + "&ref=nb_sb_noss_2";
        String itemSelector = ".sg-col-inner > .s-include-content-margin"; // Individual Product boxes

        Document page = Jsoup.connect(url).userAgent("Jsoup Scraper").get();
        Elements items = page.select(itemSelector);

        String productTitle = "";
        String productPrice;
        String productUrl;
        String productImageSrc;

        String titleSelector = ".a-link-normal > span";
        String priceSelector = ".a-offscreen";
        String urlSelector = ".a-size-mini > .a-link-normal";
        String imageSrcSelector = "img";

        int counter = 0;

        for (Element item:items){
            if (counter >= NUM_PRODUCTS_TO_SCRAPE){
                break;
            }

            // Get the first name if there's multiple names.
            Elements temp = item.select(titleSelector);
            if (temp.text().length() > 0){
                productTitle = temp.first().text();
            }

            // Get the first price if there are multiple prices.
            productPrice = item.select(priceSelector).text();
            if (productPrice.contains(" ")){
                productPrice = productPrice.substring(0, productPrice.indexOf(" "));
            }

            // Do not include priceless items or items with names that does not include targetTitle.
            if (productPrice.equals("")){ //!productTitle.toLowerCase().contains(originalTargetTitle) || productPrice.equals("")){
                continue;
            }
            productUrl = "https://amazon.com" + item.select(urlSelector).attr("href");
            productImageSrc =  item.select(imageSrcSelector).attr("src");

            Product product = new Product(productTitle, productPrice, productUrl, productImageSrc,
                    R.drawable.amazon_logo);
            products.add(product);
            counter++;
        }
        return products;
    }

    public ArrayList<Product> scrapeEbay(String targetTitleForUrl, String originalTargetTitle)
            throws IOException{

        // Search the first page of ebay search.
        ArrayList<Product> products = new ArrayList<>();
        String url = "https://www.ebay.com/sch/i.html?_from=R40&_trksid=p2380057.m570.l1313.TR12." +
                "TRC2.A0.H0.Xdang.TRS0&_nkw=" + targetTitleForUrl + "&_sacat=0&_ipg=24";
        String itemSelector = ".s-item > .s-item__wrapper"; // Individual Product boxes

        Document page = Jsoup.connect(url).userAgent("Jsoup Scraper").get();
        Elements items = page.select(itemSelector);

        String productTitle;
        String productPrice;
        String productUrl;
        String productImageSrc;

        String titleSelector = ".s-item__title";
        String priceSelector = ".s-item__price";
        String urlSelector = ".s-item__link";
        String imageSrcSelector = "img";

        int counter = 0;

        for (Element item:items){
            if (counter >= NUM_PRODUCTS_TO_SCRAPE){
                break;
            }

            productTitle = item.select(titleSelector).text();
            if (productTitle.substring(0, 9).toLowerCase().equals("sponsored")){
                productTitle = productTitle.substring(9).trim();
            }
            if (productTitle.substring(0, 11).toLowerCase().equals("new listing")){
                productTitle = productTitle.substring(11).trim();
            }

            productPrice = item.select(priceSelector).text();

            // Do not include range priced items or items with names that does not include targetTitle.
//            if (!productTitle.toLowerCase().contains(originalTargetTitle) ||
            if (productPrice.contains("to")){
                continue;
            }
            productUrl = item.select(urlSelector).attr("href");

            // Some src are .gif which un-viewable, so we need to use it's data-src. Some don't have data-src so we use it's src.
            productImageSrc =  item.select(imageSrcSelector).attr("data-src");
            if (productImageSrc.equals("")) {
                productImageSrc = item.select("img").attr("src");
            }

            Product product = new Product(productTitle, productPrice, productUrl, productImageSrc,
                    R.drawable.ebay_logo);
            products.add(product);
            counter++;
        }
        return products;
    }

    public ArrayList<Product> scrapeWalmart(String targetTitleForUrl, String originalTargetTitle)
            throws IOException{

        // Search the first page of amazon search.
        ArrayList<Product> products = new ArrayList<>();
        String url ="https://www.walmart.com/search/?cat_id=0&query=" + targetTitleForUrl;
        String itemSelector = ".search-result-gridview-items > .Grid-col"; // Individual Product boxes

        Document page = Jsoup.connect(url).userAgent("Jsoup Scraper").get();
        Elements items = page.select(itemSelector);

        String productTitle;
        String productPrice = "";
        String productUrl;
        String productImageSrc;

        String titleSelector = ".product-title-link"; // title attr
        String priceSelector = ".price-main > .visuallyhidden";
        String urlSelector = ".product-title-link"; // href attr.
        String imageSrcSelector = "img";

        int counter = 0;

        for (Element item:items){
            if (counter >= NUM_PRODUCTS_TO_SCRAPE){
                break;
            }

            // Get the first name if there's multiple names.
            productTitle = item.select(titleSelector).attr("title");

            // Check if the price is a single price or a price range.
            Elements temp = item.select(priceSelector);
            if (temp.text().length() > 0){
                productPrice = temp.text();
                if (temp.size() == 2){
                    productPrice = temp.first().text() + " - " + temp.last().text();
                }
            }

            // Do not include priceless items or items with names that does not include targetTitle.
//            if (!productTitle.toLowerCase().contains(originalTargetTitle) || productPrice.equals("")){
            if (productPrice.equals("")){
                continue;
            }
            productUrl = "https://walmart.com" + item.select(urlSelector).attr("href");
            productImageSrc =  item.select(imageSrcSelector).attr("data-image-src");

            Product product = new Product(productTitle, productPrice, productUrl, productImageSrc,
                    R.drawable.walmart_logo);
            products.add(product);
            counter++;
        }
        return products;
    }
    public static void main(String[] args) throws IOException {

        String targetTitleForUrl = "league+of+legends";
        String originalTargetTitle = "league of legends";
        Webscraper Webscraper = new Webscraper();

        /* Tests for scraping Ebay */
//         ArrayList<Product> products = Webscraper.scrapeEbay( "league+of+legends", "league of legends");
//         ArrayList<Product> products = Webscraper.scrapeEbay("yu-gi-oh", "yu-gi-oh");
//         ArrayList<Product> products = Webscraper.scrapeEbay("FDW+CLEAR+SPRINGS+125+GMS", " ");

        /* Tests for scraping Amazon */
//         ArrayList<Product> products2 = Webscraper.scrapeAmazon("league+of+legends", "league of legends");
//         ArrayList<Product> products2 = Webscraper.scrapeAmazon("yu-gi-oh", "yu-gi-oh");
//         ArrayList<Product> products2 = Webscraper.scrapeAmazon("FDW+CLEAR+SPRINGS+125+GMS", " ");

        /* Tests for scraping Walmart */
//         ArrayList<Product> products3 = Webscraper.scrapeWalmart("league+of+legends", "league of legends");
//         ArrayList<Product> products3 = Webscraper.scrapeWalmart("yu-gi-oh", "yu-gi-oh");
//         ArrayList<Product> products3 = Webscraper.scrapeWalmart("FDW+CLEAR+SPRINGS+125+GMS", " ");

        ArrayList<Product> products = Webscraper.scrapeEbay(targetTitleForUrl, originalTargetTitle);
        ArrayList<Product> products2 = Webscraper.scrapeAmazon(targetTitleForUrl, originalTargetTitle);
        ArrayList<Product> products3 = Webscraper.scrapeWalmart(targetTitleForUrl, originalTargetTitle);

        System.out.println("size for EBAY: " +  products.size());
        for (Product item:products){
            item.print();
            System.out.println();
        }

        System.out.println("size for AMAZON: " +  products2.size());
        for (Product item:products2){
            item.print();
            System.out.println();
        }

        System.out.println("size for WALAMRT: " +  products3.size());
        for (Product item:products3){
            item.print();
            System.out.println();
        }
    }
}
