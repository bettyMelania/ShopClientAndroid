package com.shop.betty.shopclient.net;

import android.util.JsonReader;
import android.util.Log;

import com.shop.betty.shopclient.content.Product;
import com.shop.betty.shopclient.util.ResourceReader;

import java.io.IOException;

public class ProductReader implements ResourceReader<Product> {
    private static final String TAG = ProductReader.class.getSimpleName();

    public static final String PRODUCT_ID = "id";
    public static final String PRODUCT_NAME = "name";
    public static final String PRODUCT_PRICE= "price";
    public static final String PRODUCT_AMOUNT= "amount";
    public static final String PRODUCT_UPDATED = "updated";

    @Override
    public Product read(JsonReader reader) throws IOException {
        Product p = new Product();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(PRODUCT_ID)) {
                p.setId(reader.nextInt());
            } else if (name.equals(PRODUCT_NAME)) {
                p.setName(reader.nextString());
            } else if (name.equals(PRODUCT_PRICE)) {
                p.setPrice(reader.nextDouble());
            } else if (name.equals(PRODUCT_AMOUNT)) {
                p.setAmount(reader.nextInt());
            } else if (name.equals(PRODUCT_UPDATED)) {
                p.setUpdated(reader.nextLong());
            } else {
                reader.skipValue();
                Log.w(TAG, String.format("Product property '%s' ignored", name));
            }
        }
        reader.endObject();
        return p;
    }
}
