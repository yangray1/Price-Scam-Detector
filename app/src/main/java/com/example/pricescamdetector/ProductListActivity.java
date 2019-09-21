package com.example.pricescamdetector;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.api.CommonStatusCodes;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ProductListActivity<progressBar> extends AppCompatActivity {

    private ListView productList;
    private final static String PRODUCT_STORE_URL = "http://product-open-data.com";

    /**
     * ProgressDialog guide:
     * https://stackoverflow.com/questions/11752961/how-to-show-a-progress-spinner-in-android-when-doinbackground-is-being-execut
     */
    private ProgressDialog dialog;
    private CustomDialog okDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        okDialog = new CustomDialog(this);
        okDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
                    ProductListActivity.this.startActivityForResult(browserIntent, 0);
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
                return barcode;
            }
        }else {
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
            dialog.setMessage("Fetching details... May take up to 5 seconds.");
            dialog.show();
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
            if (result == null || result.equals("")){
                dialog.dismiss();
                okDialog.show();
                return;
            }

            try {
                JSONObject scannedObj = new JSONObject(result);
                scannedItemName = scannedObj.getString("name");
                scannedItemImage = scannedObj.getString("img");
                scannedItemLogo = scannedObj.getJSONObject("BSIN").getString("img");
            } catch (Exception e) {
                e.printStackTrace();
                // Throw cannot scan object.
            }
            getProducts(scannedItemName);
        }
    }
    private class WebScrape extends AsyncTask<Object, Void, ArrayList<Product>> {
        @Override
        protected ArrayList<Product> doInBackground(Object... params) {
            Webscraper webscraper = (Webscraper) params[0];
            String title = (String) params[1];
            String titleInURLForm = (String) params[2];

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
            /* Set up list view */
            productList = findViewById(R.id.productList);
            ProductListAdapter adapter = new ProductListAdapter(ProductListActivity.this,
                    products, scannedItemName, scannedItemImage, scannedItemLogo);
            dialog.dismiss();
            productList.setAdapter(adapter);

            itemClickListener(products);
        }
    }
}