package com.shop.betty.shopclient.net.mapping;

import android.util.JsonWriter;

import com.shop.betty.shopclient.content.Product;

import java.io.IOException;

import static com.shop.betty.shopclient.net.mapping.Api.Product.NAME;
import static com.shop.betty.shopclient.net.mapping.Api.Product.STATUS;
import static com.shop.betty.shopclient.net.mapping.Api.Product.UPDATED;
import static com.shop.betty.shopclient.net.mapping.Api.Product.USER_ID;
import static com.shop.betty.shopclient.net.mapping.Api.Product.VERSION;
import static com.shop.betty.shopclient.net.mapping.Api.Product._ID;

public class ProductWriter implements ResourceWriter<Product, JsonWriter>{
  @Override
  public void write(Product product, JsonWriter writer) throws IOException {
    writer.beginObject();
    {
      if (product.getId() != null) {
        writer.name(_ID).value(product.getId());
      }
      writer.name(NAME).value(product.getName());
      writer.name(STATUS).value(product.getStatus().name());
      if (product.getUpdated() > 0) {
        writer.name(UPDATED).value(product.getUpdated());
      }
      if (product.getUserId() != null) {
        writer.name(USER_ID).value(product.getUserId());
      }
      if (product.getVersion() > 0) {
        writer.name(VERSION).value(product.getVersion());
      }
    }
    writer.endObject();
  }
}
