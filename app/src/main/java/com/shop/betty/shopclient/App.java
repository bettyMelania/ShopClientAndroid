package com.shop.betty.shopclient;

import android.app.Application;
import android.util.Log;

import com.shop.betty.shopclient.net.ProductRestClient;
import com.shop.betty.shopclient.service.ProductManager;

public class App extends Application {
    public static final String TAG = App.class.getSimpleName();
    private ProductManager productManager;
    private ProductRestClient productRestClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        productManager = new ProductManager(this);
        productRestClient = new ProductRestClient(this);
        productManager.setProductRestClient(productRestClient);
    }

    public ProductManager getProductManager() {
        return productManager;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate");
    }
}