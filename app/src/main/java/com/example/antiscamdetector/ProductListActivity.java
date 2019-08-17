package com.example.antiscamdetector;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProductListActivity extends AppCompatActivity {

    private ListView productList;
    private String name[] = { "iphone", "ipad", "ipod touch", "apple", "pig",  "dog", "cat", "meow", "china"};
    private String price[] = {"$999.99", "$888.99", "$1022.99", "$9.00", "$1.11", "$222.20", "$22.2", "$3.30", "$1.99"};
    private String url[] = {"http://google.com", "http://amazon.com", "http://walmart.com","http://google.com", "http://amazon.com", "http://walmart.com","http://google.com", "http://amazon.com", "http://walmart.com" };

    // For now
    private String src = "https://i5.walmartimages.com/asr/5fce9408-b2ca-439c-bc49-d32bebf2e9a1_1.089422a803b1fcd8bdf8ea2e04dda9de.jpeg?odnWidth=200&odnHeight=200&odnBg=ffffff";
    private String imageSrc[] = {src, src, src, src, src, src, src, src, src};

    // For now
    private int logo = R.drawable.ebay_logo;
    private int brandLogos[] = {logo, logo, logo, logo, logo, logo, logo, logo, logo};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productList = findViewById(R.id.productList);

        ProductListAdapter adapter = new ProductListAdapter(this, name, price, imageSrc, brandLogos, "I SCANNED THIS", R.drawable.amazon_logo, R.drawable.amazon_logo );
        productList.setAdapter(adapter);

        // Set item click on list view.
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 0){
                    Intent browserIntent = new Intent(ProductListActivity.this, WebViewActivity.class);
                    browserIntent.putExtra("URL", url[position - 1]);
                    ProductListActivity.this.startActivity(browserIntent);
                }

            }
        });
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
}
