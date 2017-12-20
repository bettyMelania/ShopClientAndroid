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
import android.widget.TextView;

import com.shop.betty.shopclient.content.Product;
import com.shop.betty.shopclient.util.Cancellable;
import com.shop.betty.shopclient.util.OnErrorListener;
import com.shop.betty.shopclient.util.OnSuccessListener;

public class ProductDetailFragment extends Fragment {
    public static final String TAG = ProductDetailFragment.class.getSimpleName();

    /**
     * The fragment argument representing the item ID that this fragment represents.
     */
    public static final String PRODUCT_ID = "product_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Product mProduct;

    private App mApp;

    private Cancellable mFetchNoteAsync;
    private TextView mNoteTextView;
    private CollapsingToolbarLayout mAppBarLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
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
            // In a real-world scenario, use a Loader
            // to load content from a content provider.
            Activity activity = this.getActivity();
            mAppBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.product_detail, container, false);
        mNoteTextView = (TextView) rootView.findViewById(R.id.product_name);
        fillNoteDetails();
        fetchNoteAsync();
        return rootView;
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    private void fetchNoteAsync() {
        mFetchNoteAsync = mApp.getProductManager().getProductAsync(
                getArguments().getString(PRODUCT_ID),
                new OnSuccessListener<Product>() {

                    @Override
                    public void onSuccess(final Product note) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProduct = note;
                                fillNoteDetails();
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

    private void fillNoteDetails() {
        if (mProduct != null) {
            if (mAppBarLayout != null) {
                mAppBarLayout.setTitle(mProduct.getName());
            }
            mNoteTextView.setText(mProduct.getName());
        }
    }
}
