package com.shop.betty.shopclient.net;

public interface ResourceChangeListener<E> {
    void onCreated(E e);
    void onUpdated(E e);
    void onDeleted(String id);

    void notifyObservers();

    void onError(Throwable t);
}
