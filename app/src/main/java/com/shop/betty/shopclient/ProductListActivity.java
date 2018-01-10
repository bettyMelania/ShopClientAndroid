package com.shop.betty.shopclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shop.betty.shopclient.content.Product;
import com.shop.betty.shopclient.util.Cancellable;
import com.shop.betty.shopclient.util.DialogUtils;
import com.shop.betty.shopclient.util.OnErrorListener;
import com.shop.betty.shopclient.util.OnSuccessListener;

import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    public static final String TAG = ProductListActivity.class.getSimpleName();
    private boolean mProductsLoaded;
    private boolean mTwoPane;
    private App mApp;

    private Cancellable mGetProductsAsyncCall;
    private View mContentLoadingView;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mApp = (App) getApplication();
        setContentView(R.layout.activity_product_list);
        setupToolbar();
        setupFloatingActionBar();
        setupRecyclerView();
        checkTwoPaneMode();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        startGetProductsAsyncCall();
        mApp.getProductManager().subscribeChangeListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        ensureGetProductsAsyncCallCancelled();
        mApp.getProductManager().unsubscribeChangeListener();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
    }

    private void setupFloatingActionBar() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toLoginPage();

                Snackbar.make(view, "LOGOUT & Going to login page", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Action", null).show();
            }
        });
    }
    private void toLoginPage(){
        mApp.getProductManager().setCurrentUser(null);
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void setupRecyclerView() {
        mContentLoadingView = findViewById(R.id.content_loading);
        mRecyclerView = (RecyclerView) findViewById(R.id.product_list);
    }

    private void checkTwoPaneMode() {
        if (findViewById(R.id.product_detail_container) != null) {
            mTwoPane = true;
        }
    }


    private void startGetProductsAsyncCall() {
        if (mProductsLoaded) {
            Log.d(TAG, "start getProductsAsyncCall - content already loaded, return");
            return;
        }
        showLoadingIndicator();
        mGetProductsAsyncCall = mApp.getProductManager().getProductsAsync(
                new OnSuccessListener<List<Product>>() {
                    @Override
                    public void onSuccess(final List<Product> Products) {
                        Log.d(TAG, "getProductsAsyncCall - success");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showContent(Products);
                            }
                        });
                    }
                }, new OnErrorListener() {
                    @Override
                    public void onError(final Exception e) {
                        Log.d(TAG, "getProductsAsyncCall - error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showError(e);
                            }
                        });
                    }
                }
        );
    }

    private void showError(Exception e) {
        Log.e(TAG, "showError", e);
        if (mContentLoadingView.getVisibility() == View.VISIBLE) {
            mContentLoadingView.setVisibility(View.GONE);
        }
        DialogUtils.showError(this, e);
    }

    private void showLoadingIndicator() {
        Log.d(TAG, "showLoadingIndicator");
        mRecyclerView.setVisibility(View.GONE);
        mContentLoadingView.setVisibility(View.VISIBLE);
    }

    private void showContent(final List<Product> products) {
        Log.d(TAG, "showContent");
        mRecyclerView.setAdapter(new ProductRecyclerViewAdapter(products));
        mContentLoadingView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ensureGetProductsAsyncCallCancelled() {
        if (mGetProductsAsyncCall != null) {
            Log.d(TAG, "ensureGetNotesAsyncCallCancelled - cancelling the task");
            mGetProductsAsyncCall.cancel();
        }
    }

    public class ProductRecyclerViewAdapter
            extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder> {

        private final List<Product> mValues;

        public ProductRecyclerViewAdapter(List<Product> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mNameView.setText(mValues.get(position).getName());
            holder.mPriceView.setText("price: "+ mValues.get(position).getPrice());
            holder.mAmountView.setText("amount: "+ mValues.get(position).getAmount());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ProductDetailFragment.PRODUCT_ID, holder.mItem.getId());
                        ProductDetailFragment fragment = new ProductDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.product_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ProductDetailActivity.class);
                        Log.d("aici",holder.mItem.toString());
                        intent.putExtra(ProductDetailFragment.PRODUCT_ID, holder.mItem.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mNameView;
            public final TextView mPriceView;
            public final TextView mAmountView;
            public Product mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mNameView = (TextView) view.findViewById(R.id.product_name);
                mPriceView = (TextView) view.findViewById(R.id.product_price);
                mAmountView = (TextView) view.findViewById(R.id.product_amount);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mNameView.getText() + "'";
            }
        }
    }
}