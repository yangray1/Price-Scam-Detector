package com.example.antiscamdetector;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.api.CommonStatusCodes;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ProductListActivity<progressBar> extends AppCompatActivity {

    private ListView productList;
    private final static String PRODUCT_STORE_URL = "http://product-open-data.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /* got barcode, now we need to get the data. */
        String barcode = getBarcodeResult();
        final String url = PRODUCT_STORE_URL + "/api/gtin/" + parseBarcodeToGTIN(barcode) + "/?format=json";

        new barCodeHTTPRequest().execute(url);

    }

    private void itemClickListener(ArrayList<Product> productsArg){
        final ArrayList<Product> products = productsArg;

        // Set item click on list view.
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 0){
                    Intent browserIntent = new Intent(ProductListActivity.this, WebViewActivity.class);
                    browserIntent.putExtra("URL", products.get(position).getUrl());
                    ProductListActivity.this.startActivity(browserIntent);
                }

            }
        });
    }

    private String getBarcodeResult() {
        Intent intent = getIntent();
        String barcode = intent.getStringExtra("barcode");
        int resultCode = intent.getIntExtra("statusCode", 5);

        if (resultCode == CommonStatusCodes.SUCCESS) {
            if (barcode != null){
                Toast.makeText(ProductListActivity.this, barcode, Toast.LENGTH_SHORT).show();
                return barcode;
            }
        }else {
            Toast.makeText(ProductListActivity.this, "Scanning Not successful", Toast.LENGTH_SHORT).show();
            // Popup to Go back to Main Activity.
        }
        return "2";

    }

    private void getProducts(String title) {
        String titleInURLForm = titleToURLForm(title);
        Webscraper webscraper = new Webscraper();
        new WebScrape().execute(webscraper, title, titleInURLForm);
    }

    private String titleToURLForm(String name) {
        return name.replaceAll(" ", "+");
    }

    private String parseBarcodeToGTIN(String barcode) {
        String GTINcode = barcode;
        if (GTINcode.length() >= 13){
            return barcode;
        }
        while (GTINcode.length() != 13){
            GTINcode = '0' + GTINcode;
        }
        return GTINcode;
    }

    /* Menu Buttons */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }else if (id == R.id.goToScanActivity){
            startActivity(new Intent(this, ScanBarcodeActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    String scannedItemName = "";
    String scannedItemImage = "";
    String scannedItemLogo = "";

    private class barCodeHTTPRequest extends AsyncTask<String, Void, String> {
    /* Guide from https://stackoverflow.com/questions/9671546/asynctask-android-example */

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                result = HTTPRequests.getHTTP(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                // Throw cannot do get request.
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            // Check if API found any data.
            if (result.equals("")){
                System.out.println("RETURNING");
                Toast.makeText(ProductListActivity.this, "Could not find any data for this product. Please try again", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ProductListActivity.this, ScanBarcodeActivity.class);
                startActivity(intent);
                finish();
            }
            try {
                JSONObject scannedObj = new JSONObject(result);
                scannedItemName = scannedObj.getString("name");
                scannedItemImage = scannedObj.getString("img");
                scannedItemLogo = scannedObj.getJSONObject("BSIN").getString("img");

//                    System.out.println("");
//                    System.out.println("scannedItemName: " + scannedItemName);
//                    System.out.println("scannedItemImage: " + scannedItemImage);
//                    System.out.println("scannedItemLogo: " + scannedItemLogo);
//                    System.out.println("");

            } catch (Exception e) {
                e.printStackTrace();
                // Throw cannot scan object.
            }

//            ArrayList<Product> products = null;
            getProducts(scannedItemName);


//            for (Product eachProduct:products){
//                eachProduct.print();
//            }
//            /* Set up list view */
//            productList = findViewById(R.id.productList);
//            ProductListAdapter adapter = new ProductListAdapter(ProductListActivity.this, name, price, imageSrc, brandLogos, scannedItemName, scannedItemImage, scannedItemLogo);
//            productList.setAdapter(adapter);
//
//            itemClickListener();
        }
    }
    private class WebScrape extends AsyncTask<Object, Void, ArrayList<Product>> {
        @Override
        protected ArrayList<Product> doInBackground(Object... params) {
            Webscraper webscraper = (Webscraper) params[0];
            String title = (String) params[1];
            String titleInURLForm = (String) params[2];

            System.out.println("TITLE: " + title );
            System.out.println("TITLEINURLFORM: " + titleInURLForm );
            ArrayList<Product> masterProductsList = new ArrayList<>();
            ArrayList<Product> tempProductList;

            // First item is a dummy place holder to display our scanned item.
            masterProductsList.add(new Product("", "", "", "", 0));

            try {
                tempProductList = webscraper.scrapeEbay(titleInURLForm, title);
                masterProductsList.addAll(tempProductList);

                tempProductList = webscraper.scrapeAmazon(titleInURLForm, title);
                masterProductsList.addAll(tempProductList);

                tempProductList = webscraper.scrapeWalmart(titleInURLForm, title);
                masterProductsList.addAll(tempProductList);
            } catch (IOException e) {
                e.printStackTrace();
                // Web scraping errror.
            }
            return masterProductsList;
        }

        @Override
        protected void onPostExecute(ArrayList<Product> products) {
            System.out.println("PRODUCTS: " + products);
//            for (Product eachProduct:products){
//                eachProduct.print();
//            }

                                System.out.println("");
                    System.out.println("scannedItemName: " + scannedItemName);
                    System.out.println("scannedItemImage: " + scannedItemImage);
                    System.out.println("scannedItemLogo: " + scannedItemLogo);
                    System.out.println("");

            /* Set up list view */
            productList = findViewById(R.id.productList);
            ProductListAdapter adapter = new ProductListAdapter(ProductListActivity.this,
                    products, scannedItemName, scannedItemImage, scannedItemLogo);
            productList.setAdapter(adapter);
            itemClickListener(products);
//
        }
    }
}