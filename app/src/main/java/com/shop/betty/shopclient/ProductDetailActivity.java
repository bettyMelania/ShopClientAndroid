package com.shop.betty.shopclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.EditText;

import com.shop.betty.shopclient.content.Product;
import com.shop.betty.shopclient.service.ProductManager;
import com.shop.betty.shopclient.util.Cancellable;
import com.shop.betty.shopclient.util.DialogUtils;
import com.shop.betty.shopclient.util.OnErrorListener;
import com.shop.betty.shopclient.util.OnSuccessListener;

import java.util.Timer;
import java.util.TimerTask;

public class ProductDetailActivity extends AppCompatActivity {
  private Cancellable mCancellable;
  private ProductManager mProductManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mProductManager = ((App) getApplication()).getProductManager();
    setContentView(R.layout.activity_product_detail);
    Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
    setSupportActionBar(toolbar);



    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    if (savedInstanceState == null) {
      Bundle arguments = new Bundle();
      arguments.putString(ProductDetailFragment.PRODUCT_ID,
          getIntent().getStringExtra(ProductDetailFragment.PRODUCT_ID));
      ProductDetailFragment fragment = new ProductDetailFragment();
      fragment.setArguments(arguments);
      getSupportFragmentManager().beginTransaction()
          .add(R.id.product_detail_container, fragment)
          .commit();
    }
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        update(view,ProductDetailFragment.PRODUCT_ID);
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      NavUtils.navigateUpTo(this, new Intent(this, ProductListActivity.class));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  public void update(final View view,String productId) {
    EditText productName = (EditText) findViewById(R.id.product_name);
    EditText productPrice = (EditText) findViewById(R.id.product_price);
    EditText productAmount = (EditText) findViewById(R.id.product_amount);
    Product product = ProductDetailFragment.mProduct;
    product.setName(productName.getText().toString());
    if(!validate(productPrice.getText().toString(),productAmount.getText().toString()))
      return;
    product.setPrice(productPrice.getText().toString());
    product.setAmount(productAmount.getText().toString());
    Log.d("aici",product.toString());
    mCancellable = mProductManager
            .saveProductAsync(
                    product,
                    new OnSuccessListener<Product>() {
                      @Override
                      public void onSuccess(Product p) {
                        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Information");
                            builder.setMessage("Product updated succesfully!");
                            builder.setCancelable(true);

                            final AlertDialog dlg = builder.create();

                            dlg.show();

                            final Timer t = new Timer();
                            t.schedule(new TimerTask() {
                              public void run() {
                                dlg.dismiss();
                                t.cancel();
                              }
                            }, 2000);

                            startProductListActivity();
                          }
                        });
                      }
                    }, new OnErrorListener() {
                      @Override
                      public void onError(final Exception e) {
                        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                            DialogUtils.showError(ProductDetailActivity.this, e);
                          }
                        });
                      }
                    });
  }

  private boolean validate(String price, String amount) {
    if (!price.matches("-?\\d+[.\\d+]?")) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          DialogUtils.showError(ProductDetailActivity.this, new Exception("invalid price"));
        }
      });
      return false;
    }
    if (!amount.matches("-?\\d+")) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          DialogUtils.showError(ProductDetailActivity.this, new Exception("invalid amount"));
        }
      });
      return false;
    }
    return true;
  }

  private void startProductListActivity() {
    startActivity(new Intent(this, ProductListActivity.class));
  }
}


