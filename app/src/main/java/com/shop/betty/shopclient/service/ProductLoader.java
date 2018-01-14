package com.shop.betty.shopclient.service;


import android.content.Context;
import android.util.Log;

import com.shop.betty.shopclient.content.Product;
import com.shop.betty.shopclient.net.LastModifiedList;
import com.shop.betty.shopclient.util.CancellableCallable;
import com.shop.betty.shopclient.util.OkAsyncTaskLoader;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ProductLoader extends OkAsyncTaskLoader<List<Product>> implements Observer {
    private static final String TAG = ProductLoader.class.getSimpleName();
    private final ProductManager mProductManager;
    private List<Product> mCachedProducts;
    private CancellableCallable<LastModifiedList<Product>> mCancellableCall;

    public ProductLoader(Context context, ProductManager ProductManager) {
        super(context);
        mProductManager = ProductManager;
    }

    @Override
    public List<Product> tryLoadInBackground() throws Exception {
        // This method is called on a background thread and should generate a
        // new set of data to be delivered back to the client
        Log.d(TAG, "tryLoadInBackground");
        mCancellableCall = mProductManager.getProductsCall();
        mCachedProducts = mProductManager.executeProductsCall(mCancellableCall);
        return mCachedProducts;
    }

    @Override
    public void deliverResult(List<Product> data) {
        Log.d(TAG, "deliverResult");
        if (isReset()) {
            Log.d(TAG, "deliverResult isReset");
            // The Loader has been reset; ignore the result and invalidate the data.
            return;
        }
        mCachedProducts = data;
        if (isStarted()) {
            Log.d(TAG, "deliverResult isStarted");
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading");
        if (mCachedProducts != null) {
            Log.d(TAG, "onStartLoading cached not null");
            // Deliver any previously loaded data immediately.
            deliverResult(mCachedProducts);
        }
        // Begin monitoring the underlying data source.
        //mProductManager.addObserver(this);
        if (takeContentChanged() || mCachedProducts == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            Log.d(TAG, "onStartLoading cached null force reload");
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        Log.d(TAG, "onStopLoading");
        cancelLoad();
        // Product that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        Log.d(TAG, "onReset");
        onStopLoading();
        // At this point we can release the resources associated with 'mData'.
        if (mCachedProducts != null) {
            mCachedProducts = null;
        }
        // The Loader is being reset, so we should stop monitoring for changes.
       // mProductManager.deleteObserver(this);
    }

    @Override
    public void onCanceled(List<Product> data) {
        // Attempt to cancel the current asynchronous load.
        Log.d(TAG, "onCanceled");
        super.onCanceled(data);
    }

    @Override
    public void update(Observable o, Object arg) {
        mCachedProducts = mProductManager.getCachedProducts();
    }
}
