package com.shop.betty.shopclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shop.betty.shopclient.content.User;
import com.shop.betty.shopclient.service.ProductManager;
import com.shop.betty.shopclient.util.Cancellable;
import com.shop.betty.shopclient.util.DialogUtils;
import com.shop.betty.shopclient.util.OnErrorListener;
import com.shop.betty.shopclient.util.OnSuccessListener;


public class LoginActivity extends AppCompatActivity {
   private Cancellable mCancellable;
   private ProductManager mProductManager;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);
      mProductManager = ((App) getApplication()).getProductManager();
      User user = mProductManager.getCurrentUser();
      if (user != null) {
         mProductManager.setCurrentUser(user);
         startProductListActivity();
      }
      setupToolbar();
      Button b = (Button) findViewById(R.id.button);
      b.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            login();
            Snackbar.make(view, "Authenticating, please wait", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null).show();
         }
      });

   }
   @Override
   protected void onStop() {
      super.onStop();
      if (mCancellable != null) {
         mCancellable.cancel();
      }
   }

   private void setupToolbar() {
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
      fab.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            login();
            Snackbar.make(view, "Authenticating, please wait", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null).show();
         }
      });
   }

   private void startProductListActivity() {
      startActivity(new Intent(this, ProductListActivity.class));
   }


   private void login() {
      EditText usernameEditText = (EditText) findViewById(R.id.username);
      EditText passwordEditText = (EditText) findViewById(R.id.password);
      mCancellable = mProductManager
              .loginAsync(
                      usernameEditText.getText().toString(), passwordEditText.getText().toString(),
                      new OnSuccessListener<String>() {
                         @Override
                         public void onSuccess(String s) {
                            runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
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
                                  DialogUtils.showError(LoginActivity.this, e);
                               }
                            });
                         }
                      });
   }
}