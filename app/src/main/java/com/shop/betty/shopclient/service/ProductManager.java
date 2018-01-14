package com.shop.betty.shopclient.service;

import android.content.Context;
import android.util.Log;

import com.shop.betty.shopclient.content.Database;
import com.shop.betty.shopclient.content.Product;
import com.shop.betty.shopclient.content.User;
import com.shop.betty.shopclient.net.LastModifiedList;
import com.shop.betty.shopclient.net.ProductRestClient;
import com.shop.betty.shopclient.net.ProductSocketClient;
import com.shop.betty.shopclient.net.ResourceChangeListener;
import com.shop.betty.shopclient.net.ResourceException;
import com.shop.betty.shopclient.util.Cancellable;
import com.shop.betty.shopclient.util.CancellableCallable;
import com.shop.betty.shopclient.util.OnErrorListener;
import com.shop.betty.shopclient.util.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProductManager extends Observable {
  private static final String TAG = ProductManager.class.getSimpleName();
  private final Database mKD;

  private ConcurrentMap<String, Product> mProducts = new ConcurrentHashMap<String, Product>();
  private String mProductsLastUpdate;

  private final Context mContext;
  private ProductRestClient mProductRestClient;
  private ProductSocketClient mProductSocketClient;
  private String mToken;
  private User mCurrentUser;

  public ProductManager(Context context) {
    mContext = context;
    mKD = new Database(context);

  }

  public CancellableCallable<LastModifiedList<Product>> getProductsCall() {
    Log.d(TAG, "getProductsCall");
    return mProductRestClient.search(mProductsLastUpdate);
  }

  public List<Product> executeProductsCall(CancellableCallable<LastModifiedList<Product>> getProductsCall) throws Exception {
    Log.d(TAG, "execute getProducts...");
    LastModifiedList<Product> result = getProductsCall.call();
    List<Product> Products = result.getList();
    if (Products != null) {
      mProductsLastUpdate = result.getLastModified();
      updateCachedProducts(Products);
      notifyObservers();
    }
    return cachedProductsByUpdated();
  }

  public ProductLoader getProductLoader() {
    Log.d(TAG, "getProductLoader...");
    return new ProductLoader(mContext, this);
  }

  public void setProductRestClient(ProductRestClient ProductRestClient) {
    mProductRestClient = ProductRestClient;
  }

  public Cancellable getProductsAsync(final OnSuccessListener<List<Product>> successListener, OnErrorListener errorListener) {
    Log.d(TAG, "getProductsAsync...");
    return mProductRestClient.searchAsync(mProductsLastUpdate, new OnSuccessListener<LastModifiedList<Product>>() {

      @Override
      public void onSuccess(LastModifiedList<Product> result) {
        Log.d(TAG, "getProductsAsync succeeded");
        List<Product> Products = result.getList();
        if (Products != null) {
          mProductsLastUpdate = result.getLastModified();
          updateCachedProducts(Products);
          mKD.saveProducts(Products);
        }
        successListener.onSuccess(cachedProductsByUpdated());
        notifyObservers();
      }
    }, errorListener);
  }

  public Cancellable getProductAsync(
          final String ProductId,
          final OnSuccessListener<Product> successListener,
          final OnErrorListener errorListener) {
    Log.d(TAG, "getProductAsync...");
    return mProductRestClient.readAsync(ProductId, new OnSuccessListener<Product>() {

      @Override
      public void onSuccess(Product Product) {
        Log.d(TAG, "getProductAsync succeeded");
        if (Product == null) {
          setChanged();
          mProducts.remove(ProductId);
        } else {
          if (!Product.equals(mProducts.get(Product.getId()))) {
            setChanged();
            mProducts.put(ProductId, Product);
          }
        }
        successListener.onSuccess(Product);
        notifyObservers();
      }
    }, errorListener);
  }

  public Cancellable saveProductAsync(
          final Product Product,
          final OnSuccessListener<Product> successListener,
          final OnErrorListener errorListener) {
    Log.d(TAG, "saveProductAsync...");
    return mProductRestClient.updateAsync(Product, new OnSuccessListener<Product>() {

      @Override
      public void onSuccess(Product Product) {
        Log.d(TAG, "saveProductAsync succeeded");
        mProducts.put(Product.getId(), Product);
        successListener.onSuccess(Product);
        setChanged();
        notifyObservers();
      }
    }, errorListener);
  }

  public void subscribeChangeListener() {
    mProductSocketClient.subscribe(new ResourceChangeListener<Product>() {
      @Override
      public void onCreated(Product Product) {
        Log.d(TAG, "changeListener, onCreated");
        ensureProductCached(Product);
      }

      @Override
      public void onUpdated(Product Product) {
        Log.d(TAG, "changeListener, onUpdated");
        ensureProductCached(Product);
      }

      @Override
      public void onDeleted(String ProductId) {
        Log.d(TAG, "changeListener, onDeleted");
        if (mProducts.remove(ProductId) != null) {
          setChanged();
          notifyObservers();
        }
      }

      private void ensureProductCached(Product product) {
        if (!product.equals(mProducts.get(product.getId()))) {
          Log.d(TAG, "changeListener, cache updated");
          mProducts.put(product.getId(), product);
          mKD.updateProduct(product);
          setChanged();
          notifyObservers();
          Log.d(TAG, "notified");
        }
      }
      @Override()
      public void notifyObservers() {
        mProductSocketClient.notify();
      }


      @Override
      public void onError(Throwable t) {
        Log.e(TAG, "changeListener, error", t);
      }
    },this);
  }

  public void unsubscribeChangeListener() {
    mProductSocketClient.unsubscribe();
  }

  public void setProductSocketClient(ProductSocketClient ProductSocketClient) {
    mProductSocketClient = ProductSocketClient;
  }

  private void updateCachedProducts(List<Product> Products) {
    Log.d(TAG, "updateCachedProducts");
    for (Product Product : Products) {
      mProducts.put(Product.getId(), Product);
    }
    setChanged();
  }

  private List<Product> cachedProductsByUpdated() {
    ArrayList<Product> Products = new ArrayList<>(mProducts.values());
    Collections.sort(Products, new ProductByUpdatedComparator());
    return Products;
  }

  public List<Product> getCachedProducts() {
    return cachedProductsByUpdated();
  }

  public Cancellable loginAsync(
          String username, String password,
          final OnSuccessListener<String> successListener,
          final OnErrorListener errorListener) {
    final User user = new User(username, password);
    return mProductRestClient.getToken(
            user, new OnSuccessListener<String>() {

              @Override
              public void onSuccess(String token) {
                mToken = token;
                if (mToken != null) {
                  user.setToken(mToken);
                  setCurrentUser(user);
                  mKD.saveUser(user);
                  successListener.onSuccess(mToken);
                } else {
                  errorListener.onError(new ResourceException(new IllegalArgumentException("Invalid credentials")));
                }
              }
            }, errorListener);
  }

  public void setCurrentUser(User currentUser) {
    mCurrentUser = currentUser;
    mProductRestClient.setUser(currentUser);
  }

  public User getCurrentUser() {
    return mKD.getCurrentUser();
  }

  public List<Product> getProductsFromDatabase() {
    return mKD.getProducts();
  }

  public void logout() {
    mKD.deleteUser();
  }

  public Product getProductFromDatabase(String string) {
    return mKD.getProduct(string);
  }

  private class ProductByUpdatedComparator implements java.util.Comparator<Product> {
    @Override
    public int compare(Product n1, Product n2) {
      return (int) (n1.getUpdated() - n2.getUpdated());
    }
  }
}