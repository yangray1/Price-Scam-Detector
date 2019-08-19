package com.example.pricescamdetector;

public class Product {
    private String name;
    private String price;
    private String url;
    private String imageSrc;
    private int brandLogoImage;

    Product(String name, String price, String url, String imageSrc, int brandLogo) {
        this.name = name;
        this.price = price;
        this.url = url;
        this.imageSrc = imageSrc;
        this.brandLogoImage = brandLogo;
    }

    public void print() {
        System.out.println("product name: " + name);
        System.out.println("product price: " + price);
        System.out.println("product url: " + url);
        System.out.println("product imageSrc: " + imageSrc);
    }
    public String getName(){
      return name;
    }

    public String getPrice(){
        return price;
    }

    public String getUrl(){
        return url;
    }

    public String getImageSrc(){
        return imageSrc;
    }

    public int getBrandLogoImage() {
        return this.brandLogoImage;
    }
}
