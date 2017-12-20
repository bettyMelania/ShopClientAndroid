package com.shop.betty.shopclient.net.mapping;

import android.util.JsonWriter;


import com.shop.betty.shopclient.content.User;

import java.io.IOException;

import static com.shop.betty.shopclient.net.mapping.Api.Auth.PASSWORD;
import static com.shop.betty.shopclient.net.mapping.Api.Auth.USERNAME;

public class CredentialsWriter implements ResourceWriter<User, JsonWriter> {
  @Override
  public void write(User user, JsonWriter writer) throws IOException {
    writer.beginObject();
    {
      writer.name(USERNAME).value(user.getUsername());
      writer.name(PASSWORD).value(user.getPassword());
    }
    writer.endObject();
  }
}