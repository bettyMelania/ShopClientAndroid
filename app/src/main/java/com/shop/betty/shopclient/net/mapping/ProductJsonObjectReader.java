package com.shop.betty.shopclient.net.mapping;

import com.shop.betty.shopclient.content.Product;

import org.json.JSONObject;

import static com.shop.betty.shopclient.net.mapping.Api.Product.NAME;
import static com.shop.betty.shopclient.net.mapping.Api.Product.STATUS;
import static com.shop.betty.shopclient.net.mapping.Api.Product.UPDATED;
import static com.shop.betty.shopclient.net.mapping.Api.Product.USER_ID;
import static com.shop.betty.shopclient.net.mapping.Api.Product.VERSION;
import static com.shop.betty.shopclient.net.mapping.Api.Product._ID;

public class ProductJsonObjectReader implements ResourceReader<Product, JSONObject> {
  private static final String TAG = ProductJsonObjectReader.class.getSimpleName();

  @Override
  public Product read(JSONObject obj) throws Exception {
    Product note = new Product();
    note.setId(obj.getString(_ID));
    note.setName(obj.getString(NAME));
    note.setUpdated(obj.getLong(UPDATED));
    note.setStatus(Product.Status.valueOf(obj.getString(STATUS)));
    note.setUserId(obj.getString(USER_ID));
    note.setVersion(obj.getInt(VERSION));
    return note;
  }
}
