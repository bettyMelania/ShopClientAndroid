package com.shop.betty.shopclient.net.mapping;

import android.util.JsonReader;

import org.json.JSONException;

import java.io.IOException;

public interface ResourceReader<E, Reader> {
    E read(Reader reader) throws IOException, JSONException, Exception;
}
