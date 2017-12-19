package com.shop.betty.shopclient.service;

import android.content.Context;
import android.util.Log;

import com.shop.betty.shopclient.content.Product;
import com.shop.betty.shopclient.net.ProductRestClient;
import com.shop.betty.shopclient.util.OkAsyncTaskLoader;

import java.util.List;

public class ProductLoader extends OkAsyncTaskLoader<List<Product>> {
    private static final String TAG =ProductLoader.class.getSimpleName();
    private final ProductRestClient mProductRestClient;
    private List<Product> products;

    public ProductLoader(Context context, ProductRestClient productRestClient) {
        super(context);
        mProductRestClient = productRestClient;
    }

    @Override
    public List<Product> tryLoadInBackground() throws Exception {
        Log.d(TAG, "tryLoadInBackground");
        products = mProductRestClient.getAll();
        return products;
    }

    @Override
    protected void onStartLoading() {
        if (products != null) {
            Log.d(TAG, "onStartLoading - deliver result");
            deliverResult(products);
        }

        if (takeContentChanged() || products == null) {
            Log.d(TAG, "onStartLoading - force load");
            forceLoad();
        }
    }
}
