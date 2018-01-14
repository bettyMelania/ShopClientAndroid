package com.shop.betty.shopclient;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.shop.betty.shopclient.content.Product;
import com.shop.betty.shopclient.util.Cancellable;
import com.shop.betty.shopclient.util.DialogUtils;
import com.shop.betty.shopclient.util.Network;
import com.shop.betty.shopclient.util.OnErrorListener;
import com.shop.betty.shopclient.util.OnSuccessListener;

public class ProductDetailFragment extends Fragment {
    public static final String TAG = ProductDetailFragment.class.getSimpleName();
    public static final String PRODUCT_ID = "product_id";
    public static Product mProduct;
    private App mApp;
    private Cancellable mFetchProductAsync;
    private EditText mProductName;
    private EditText mProductPrice;
    private EditText mProductAmount;
    private CollapsingToolbarLayout mAppBarLayout;

    public ProductDetailFragment() {
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        mApp = (App) context.getApplicationContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(PRODUCT_ID)) {
            Activity activity = this.getActivity();
            mAppBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.product_detail, container, false);

        mProductName = (EditText) rootView.findViewById(R.id.product_name);
        mProductPrice = (EditText) rootView.findViewById(R.id.product_price);
        mProductAmount = (EditText) rootView.findViewById(R.id.product_amount);
        if(Network.isNetworkConnected(mProductName.getContext())) {
            fetchProductAsync();
        }
        else{
            Log.d("aici",getArguments().getString(PRODUCT_ID) );
            mProduct=mApp.getProductManager().getProductFromDatabase(getArguments().getString(PRODUCT_ID));
            fillProductDetails();
        }
        return rootView;
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    private void fetchProductAsync() {
        mFetchProductAsync = mApp.getProductManager().getProductAsync(
                getArguments().getString(PRODUCT_ID),
                new OnSuccessListener<Product>() {

                    @Override
                    public void onSuccess(final Product product) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProduct = product;
                                fillProductDetails();
                            }
                        });
                    }
                }, new OnErrorListener() {

                    @Override
                    public void onError(final Exception e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.showError(getActivity(), e);
                            }
                        });
                    }
                });
    }

    private void fillProductDetails() {
        if (mProduct != null) {
            if (mAppBarLayout != null) {
                mAppBarLayout.setTitle(mProduct.getName());
            }
            mProductName.setText(mProduct.getName());
            mProductPrice.setText(mProduct.getPrice());
            mProductAmount.setText(mProduct.getAmount());
        }
    }
}