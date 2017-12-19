package com.shop.betty.shopclient.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shop.betty.shopclient.R;
import com.shop.betty.shopclient.content.Product;

import java.util.List;

public class ProductListAdapter extends BaseAdapter {
    public static final String TAG = ProductListAdapter.class.getSimpleName();
    private final Context mContext;
    private List<Product> mProducts;

    public ProductListAdapter(Context context, List<Product> products) {
        mContext = context;
        mProducts = products;
    }

    @Override
    public int getCount() {
        return mProducts.size();
    }

    @Override
    public Object getItem(int position) {
        return mProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View productLayout = LayoutInflater.from(mContext).inflate(R.layout.product_list_content, null);
        ((TextView) productLayout.findViewById(R.id.product_name)).setText(mProducts.get(position).getName());
        ((TextView) productLayout.findViewById(R.id.product_price)).setText("Price:"+ String.valueOf(mProducts.get(position).getPrice()));
        ((TextView) productLayout.findViewById(R.id.product_amount)).setText("Amount:"+ String.valueOf(mProducts.get(position).getAmount()));
        Log.d(TAG, "getView " + position);
        return productLayout;
    }

    public void refresh() {
        notifyDataSetChanged();
    }
}
