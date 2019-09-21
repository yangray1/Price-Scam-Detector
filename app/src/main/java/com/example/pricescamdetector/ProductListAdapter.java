package com.example.pricescamdetector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProductListAdapter extends ArrayAdapter<Product> {
    /* Help from: https://www.youtube.com/watch?v=_YF6ocdPaBg and
        https://www.youtube.com/watch?v=5Tm--PHhbJo */

    private Context context;
    private ArrayList<Product> products;

    private String scannedProductName;
    private String scannedProductImage;
    private String scannedProductLogo;

        public ProductListAdapter(Context context, ArrayList<Product> products,
                                  String scannedProductName, String scannedProductImage,
                                  String scannedProductLogo) {
        // super third argument must be same type as extends ArrayAdapter<this_class>
        super(context, R.layout.activity_row_layout, products);

        this.context = context;
        this.products = products;

        this.scannedProductName = scannedProductName;
        this.scannedProductImage = scannedProductImage;
        this.scannedProductLogo = scannedProductLogo;
    }

    @NonNull
    @Override
    /* convertView is the old view to reuse, if possible. If not possible to convert this view to
       display the proper data, create a new one. */
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
            loadImageFromUrl(scannedProductImage, viewHolder.image);
            loadImageFromUrl(scannedProductLogo, viewHolder.productBrandLogo);
        }else {
            loadImageFromUrl(products.get(position).getImageSrc(), viewHolder.image);
            viewHolder.title.setText(products.get(position).getName());
            viewHolder.price.setText(products.get(position).getPrice());
            viewHolder.productStoreLogo.setImageResource(products.get(position).getBrandLogoImage());
            viewHolder.rightArrow.setImageResource(R.drawable.right_arrow);
        }
        return row;
    }

    private void loadImageFromUrl(String imageSrc,   ImageView image) {
        /* Tutorial from https://www.youtube.com/watch?v=sLEqq9rX-3k */

        Picasso.with(context).load(imageSrc).placeholder(R.drawable.no_image_available)
                .error(R.drawable.no_image_available)
                .into(image, new com.squareup.picasso.Callback(){

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
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
