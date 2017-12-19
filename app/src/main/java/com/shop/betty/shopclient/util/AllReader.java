package com.shop.betty.shopclient.util;

import android.util.JsonReader;
import android.util.Log;

import com.shop.betty.shopclient.content.Product;
import com.shop.betty.shopclient.net.ProductReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Betty on 11/14/2017.
 */

public class AllReader implements ResourceReader<List<Product>> {
    public AllReader() {

    }

    @Override
    public List<Product> read(JsonReader reader) throws IOException {
        List<Product> entityList = new ArrayList<Product>();

        reader.beginObject();
        reader.nextName();//page
        int pageNr=reader.nextInt();
        Log.d("ResourceListReader", reader.nextName()); //products

        return new ResourceListReader<Product>(new ProductReader()).read(reader);

    }
}
