package com.shop.betty.shopclient.util;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

public abstract class OkAsyncTaskLoader<T> extends AsyncTaskLoader<T> {
    public static final String TAG = OkAsyncTaskLoader.class.getSimpleName();

    public Exception loadingException;

    public OkAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    public T loadInBackground() {
        try {
            return tryLoadInBackground();
        } catch (Exception ex) {
            Log.w(TAG, "Exception", ex);
            loadingException = ex;
            return null;
        }
    }

    public abstract T tryLoadInBackground() throws Exception;
}
