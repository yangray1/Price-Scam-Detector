package com.example.antiscamdetector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class ProductListAdapter extends ArrayAdapter<String> {
    /* Help from: https://www.youtube.com/watch?v=_YF6ocdPaBg and
        https://www.youtube.com/watch?v=5Tm--PHhbJo */

    private Context context;
    private String productNames[];
    private String prices[];
    private String urls[];
    private int imageSrcs[];

    private String scannedProductName;
    private int scannedProductImage;
    private int scannedProductLogo;

    public ProductListAdapter(Context context, String[] name, String[] price, String[] url,
                              int[] imageSrc, String scannedProductName, int scannedProductImage,
                              int scannedProductLogo) {
        super(context, R.layout.activity_row_layout, name);

        this.productNames = name;
        this.prices = price;
        this.urls = url;
        this.imageSrcs = imageSrc;
        this.context = context;

        this.scannedProductName = scannedProductName;
        this.scannedProductImage = scannedProductImage;
        this.scannedProductLogo = scannedProductLogo;

    }

    @NonNull
    @Override
    // convertView is the old view to reuse, if possible. If not possible to convert this view to display the proper data, create a new one.
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        boolean is_first_row = (position == 0);

        View row = convertView;
        ViewHolder viewHolder = null;

        if (row == null || !canConvertView(row, is_first_row)){
            LayoutInflater layoutInflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int layout;
            layout = is_first_row ? R.layout.activity_row_for_scanned_product_layout :
                    R.layout.activity_row_layout;
            row = layoutInflater.inflate(layout, null, true);

            viewHolder = new ViewHolder(row, is_first_row);
            row.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) row.getTag();
        }

        // Set our resources on views
        if (is_first_row){
            viewHolder.title.setText(scannedProductName);
            viewHolder.image.setImageResource(scannedProductImage);
            viewHolder.productBrandLogo.setImageResource(scannedProductLogo);
        }else {
            viewHolder.image.setImageResource((imageSrcs[position]));
            viewHolder.title.setText(productNames[position]);
            viewHolder.price.setText(prices[position]);
            viewHolder.productStoreLogo.setImageResource(R.drawable.amazon_logo); // For now.
            viewHolder.rightArrow.setImageResource(R.drawable.right_arrow);
        }
        return row;
    }

    /* Check if convertView can be reused. */
    private boolean canConvertView(View convertView, boolean is_first_row){
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        if (viewHolder.image == null || viewHolder.title == null){
            return false;
        }

        if (is_first_row){
            return viewHolder.productBrandLogo != null;
        }else{
            return viewHolder.price != null && viewHolder.productStoreLogo != null &&
                    viewHolder.rightArrow != null;
        }
    }

    class ViewHolder{
        ImageView image;
        TextView title;

        /* for first row */
        ImageView productBrandLogo;

        /* for rest of the rows */
        TextView price;
        ImageView productStoreLogo;
        ImageView rightArrow;

        public ViewHolder(View v, boolean is_first_row){
            image = v.findViewById(R.id.productImage);
            title = v.findViewById(R.id.productTitle);

            if (is_first_row){
                productBrandLogo = v.findViewById(R.id.productBrandLogo);
            }else {
                price = v.findViewById(R.id.productPrice);
                productStoreLogo = v.findViewById(R.id.productStoreLogo);
                rightArrow = v.findViewById(R.id.rightArrow);
            }
        }
    }
}
