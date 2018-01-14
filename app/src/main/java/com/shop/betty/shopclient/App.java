package com.shop.betty.shopclient;

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.shop.betty.shopclient.net.ProductRestClient;
import com.shop.betty.shopclient.net.ProductSocketClient;
import com.shop.betty.shopclient.service.ProductManager;
import com.shop.betty.shopclient.util.DialogUtils;

public class App extends Application {
    public static final String TAG = App.class.getSimpleName();
    private ProductManager productManager;
    private ProductRestClient productRestClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        Stetho.initializeWithDefaults(this);
        productManager = new ProductManager(this);
        productRestClient = new ProductRestClient(this);
        productManager.setProductRestClient(productRestClient);
        productManager.setProductSocketClient(new ProductSocketClient(this));

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