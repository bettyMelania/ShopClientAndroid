package com.shop.betty.shopclient.net;

import android.content.Context;
import android.util.Log;

import com.shop.betty.shopclient.R;
import com.shop.betty.shopclient.content.Product;
import com.shop.betty.shopclient.net.mapping.IdJsonObjectReader;
import com.shop.betty.shopclient.net.mapping.ProductJsonObjectReader;

import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.shop.betty.shopclient.net.mapping.Api.Product.PRODUCT_CREATED;
import static com.shop.betty.shopclient.net.mapping.Api.Product.PRODUCT_DELETED;
import static com.shop.betty.shopclient.net.mapping.Api.Product.PRODUCT_UPDATED;

public class ProductSocketClient {
  private static final String TAG = ProductSocketClient.class.getSimpleName();
  private final Context mContext;
  private Socket mSocket;
  private ResourceChangeListener<Product> mResourceListener;

  public ProductSocketClient(Context context) {
    mContext = context;
    Log.d(TAG, "created");
  }

  public void subscribe(final ResourceChangeListener<Product> resourceListener) {
    Log.d(TAG, "subscribe");
    mResourceListener = resourceListener;
    try {
      mSocket = IO.socket(mContext.getString(R.string.api_url));
      mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
        @Override
        public void call(Object... args) {
          Log.d(TAG, "socket connected");
        }
      });
      mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
        @Override
        public void call(Object... args) {
          Log.d(TAG, "socket disconnected");
        }
      });
      mSocket.on(PRODUCT_CREATED, new Emitter.Listener() {
        @Override
        public void call(Object... args) {
          try {
            Product p = new ProductJsonObjectReader().read((JSONObject) args[0]);
            Log.d(TAG, String.format("product created %s", p.toString()));
            mResourceListener.onCreated(p);
          } catch (Exception e) {
            Log.w(TAG, "productproduct created", e);
            mResourceListener.onError(new ResourceException(e));
          }
        }
      });
      mSocket.on(PRODUCT_UPDATED, new Emitter.Listener() {
        @Override
        public void call(Object... args) {
          try {
            Product p = new ProductJsonObjectReader().read((JSONObject) args[0]);
            Log.d(TAG, String.format("product updated %s", p.toString()));
            mResourceListener.onUpdated(p);
          } catch (Exception e) {
            Log.w(TAG, "product updated", e);
            mResourceListener.onError(new ResourceException(e));
          }
        }
      });
      mSocket.on(PRODUCT_DELETED, new Emitter.Listener() {
        @Override
        public void call(Object... args) {
          try {
            String id = new IdJsonObjectReader().read((JSONObject) args[0]);
            Log.d(TAG, String.format("note deleted %s", id));
            mResourceListener.onDeleted(id);
          } catch (Exception e) {
            Log.w(TAG, "product deleted", e);
            mResourceListener.onError(new ResourceException(e));
          }
        }
      });
      mSocket.connect();
    } catch (Exception e) {
      Log.w(TAG, "socket error", e);
      mResourceListener.onError(new ResourceException(e));
    }
  }

  public void unsubscribe() {
    Log.d(TAG, "unsubscribe");
    if (mSocket != null) {
      mSocket.disconnect();
    }
    mResourceListener = null;
  }

}
