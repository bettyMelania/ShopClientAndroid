package com.shop.betty.shopclient;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shop.betty.shopclient.content.Product;
import com.shop.betty.shopclient.util.OkAsyncTask;
import com.shop.betty.shopclient.util.OkCancellableCall;
import com.shop.betty.shopclient.util.OnErrorListener;
import com.shop.betty.shopclient.util.OnSuccessListener;
import com.shop.betty.shopclient.widget.ProductListAdapter;

import java.util.List;

public class ProductListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Product>> {

    public static final String TAG = ProductListFragment.class.getSimpleName();
    private static final int PRODUCT_LOADER_ID = 1;
    private App mApp;
    private ProductListAdapter mProductListAdapter;
    private AsyncTask<String, Void, List<Product>> mGetProductsAsyncTask;
    private ListView mProductListView;
    private View mContentLoadingView;
    private boolean mContentLoaded = false;
    private OkCancellableCall mGetProductsAsyncCall;

    public ProductListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        mApp = (App) context.getApplicationContext();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View layout = inflater.inflate(R.layout.activity_product_list, container, false);
        mProductListView = (ListView) layout.findViewById(R.id.product_list);
        mContentLoadingView = layout.findViewById(R.id.content_loading);
        showLoadingIndicator();
        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        startGetProductsAsyncCall();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        ensureGetProductsAsyncCallCancelled();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private void startGetProductsAsyncTask() {
        if (mContentLoaded) {
            Log.d(TAG, "startGetProductsAsyncTask - content already loaded, return");
            return;
        }
        mGetProductsAsyncTask = new OkAsyncTask<String, Void, List<Product>>() {

            @Override
            protected void onPreExecute() {
                showLoadingIndicator();
                Log.d(TAG, "GetProductsAsyncTask - showLoadingIndicator");
            }

            @Override
            protected List<Product> tryInBackground(String... params) throws Exception {
                Log.d(TAG, "GetProductsAsyncTask - tryInBackground");
                return mApp.getProductManager().getProducts();
            }

            @Override
            protected void onPostExecute(List<Product> products) {
                Log.d(TAG, "GetProductsAsyncTask - onPostExecute");
                if (backgroundException != null) {
                    Log.e(TAG, "Get Products failed");
                    showError(backgroundException);
                } else {
                    showContent(products);
                }
            }
        }.execute();
    }

    private void ensureGetProductsAsyncTaskCancelled() {
        if (mGetProductsAsyncTask != null && !mGetProductsAsyncTask.isCancelled()) {
            Log.d(TAG, "ensureGetProductsAsyncTaskCancelled - cancelling the task");
            mGetProductsAsyncTask.cancel(true);
        } else {
            Log.d(TAG, "ensureGetProductsAsyncTaskCancelled - task already completed or cancelled");
        }
    }

    @Override
    public Loader<List<Product>> onCreateLoader(int id, Bundle args) {
        showLoadingIndicator();
        return mApp.getProductManager().getProductLoader();
    }

    @Override
    public void onLoadFinished(Loader<List<Product>> loader, List<Product> products) {
        if (loader instanceof OkAsyncTaskLoader) {
            Exception loadingException = ((OkAsyncTaskLoader) loader).loadingException;
            if (loadingException != null) {
                Log.e(TAG, "Get Products failed");
                showError(loadingException);
                return;
            }
            showContent(products);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Product>> loader) {
        // not used
    }

    private void startGetProductsAsyncCall() {
        if (mContentLoaded) {
            Log.d(TAG, "startGetProductsAsyncCall - content already loaded, return");
            return;
        }
        mGetProductsAsyncCall = mApp.getProductManager().getProductsAsync(
                new OnSuccessListener<List<Product>>() {
                    @Override
                    public void onSuccess(final List<Product> products) {
                        Log.d(TAG, "startGetProductsAsyncCall - success");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showContent(products);
                            }
                        });
                    }
                }, new OnErrorListener() {
                    @Override
                    public void onError(final Exception e) {
                        Log.d(TAG, "startGetProductsAsyncCall - error");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showError(e);
                            }
                        });
                    }
                }
        );
    }

    private void ensureGetProductsAsyncCallCancelled() {
        if (mGetProductsAsyncCall != null) {
            Log.d(TAG, "ensureGetProductsAsyncCallCancelled - cancelling the task");
            mGetProductsAsyncCall.cancel();
        }
    }


    private void showError(Exception e) {
        Log.e(TAG, "showError", e);

        new AlertDialog.Builder(getActivity())
                .setTitle("Error")
                .setMessage(e.getMessage())
                .setCancelable(true)
                .create()
                .show();

    }

    private void showLoadingIndicator() {
        Log.d(TAG, "showLoadingIndicator");
        mProductListView.setVisibility(View.GONE);
        mContentLoadingView.setVisibility(View.VISIBLE);
    }

    private void showContent(final List<Product> products) {
        Log.d(TAG, "showContent");
        mProductListAdapter = new ProductListAdapter(this.getContext(), products);
        mProductListView.setAdapter(mProductListAdapter);
        mContentLoadingView.setVisibility(View.GONE);
        mProductListView.setVisibility(View.VISIBLE);
    }

}
