package com.shop.betty.shopclient.net.mapping;

import android.util.JsonReader;
import android.util.Log;

import com.shop.betty.shopclient.content.Product;

import java.io.IOException;

public class ProductReader implements ResourceReader<Product,JsonReader> {
    private static final String TAG = ProductReader.class.getSimpleName();

    public static final String PRODUCT_ID = "_id";
    public static final String PRODUCTID = "id";
    public static final String PRODUCT_NAME = "name";
    public static final String PRODUCT_PRICE= "price";
    public static final String PRODUCT_AMOUNT= "amount";
    public static final String PRODUCT_UPDATED = "updated";
    public static final String PRODUCT_VERSION = "version";
    public static final String PRODUCT_USER = "user";

    @Override
    public Product read(JsonReader reader) throws IOException {
        Product p = new Product();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(PRODUCT_ID)) {
                p.set_id(reader.nextString());

            } else if (name.equals(PRODUCTID)) {
                p.setId(reader.nextString());
            } else if (name.equals(PRODUCT_NAME)) {
                p.setName(reader.nextString());
            } else if (name.equals(PRODUCT_PRICE)) {
                p.setPrice(reader.nextString());
            } else if (name.equals(PRODUCT_AMOUNT)) {
                p.setAmount(reader.nextString());
            } else if (name.equals(PRODUCT_UPDATED)) {
                p.setUpdated(reader.nextLong());
            } else if (name.equals(PRODUCT_VERSION)) {
                p.setVersion(reader.nextInt());
            }else if (name.equals(PRODUCT_USER)) {
                p.setUserId(reader.nextString());
            }
            else {
                reader.skipValue();
                Log.w(TAG, String.format("Product property '%s' ignored", name));
            }
        }
        reader.endObject();
        return p;
    }
}

