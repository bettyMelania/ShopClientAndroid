package com.shop.betty.shopclient.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

  public static final String TABLE_USERS = "users";
  public static final String TABLE_PRODUCTS = "product";

  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_TOKEN = "token";

  private static final String DATABASE_NAME = "products.db";
  private static final int DATABASE_VERSION = 1;
  private static final String CREATE_USERTABLE =" create table " + TABLE_USERS
          + " ( " + COLUMN_ID +" integer primary key autoincrement, " + COLUMN_TOKEN + " text not null);";
  private static final String CREATE_PRODUCTTABLE= " create table " + TABLE_PRODUCTS
                  + " ( id text primary key , _id text , name text not null, price text,  amount text, userId text , updated integer, version integer ); ";

  public Database(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(CREATE_USERTABLE);
    database.execSQL(CREATE_PRODUCTTABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
    onCreate(db);
  }

  public void saveUser(User user) {
    ContentValues cv = new ContentValues();
    cv.put(COLUMN_TOKEN, user.getToken());
    SQLiteDatabase db = getWritableDatabase();
    db.insert(TABLE_USERS, null, cv);
    db.close();
  }
  public void deleteUser(){
    SQLiteDatabase db = getWritableDatabase();
    db.delete(TABLE_USERS,"", null);
    db.close();
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  public User getCurrentUser() {
    Cursor c = getReadableDatabase().rawQuery("select token from " + TABLE_USERS, null, null);
    if (c.moveToFirst()) {
      return new User(null, null, c.getString(0));
    } else {
      return null;
    }
  }

  public List<Product> getProducts(){
    SQLiteDatabase db = getReadableDatabase();
    Cursor c = db.rawQuery("select id ,_id ,name,price,amount,userId,updated,version from " + TABLE_PRODUCTS, null, null);
    List<Product> products=new ArrayList<>();
    while (c.moveToNext()) {
      Product p=new Product();
      p.setId(c.getString(0));
      p.set_id(c.getString(1));
      p.setName(c.getString(2));
      p.setPrice(c.getString(3));
      p.setAmount(c.getString(4));
      p.setUserId(c.getString(5));
      p.setUpdated(c.getInt(6));
      p.setVersion(c.getInt(7));
      products.add(p);
    }
    db.close();
    return products;
  }

  public void saveProducts(List<Product> products){
    SQLiteDatabase db = getWritableDatabase();
    db.delete(TABLE_PRODUCTS,"", null);
    for (Product product:products){
      ContentValues cv = new ContentValues();
      cv.put("id", product.getId());
      cv.put("_id", product.get_id());
      cv.put("name", product.getName());
      cv.put("price", product.getPrice());
      cv.put("amount", product.getAmount());
      cv.put("userId", product.getUserId());
      cv.put("updated", product.getUpdated());
      cv.put("version", product.getVersion());
      db.insert(TABLE_PRODUCTS, null, cv);
    }
    db.close();
  }

  public Product getProduct(String id) {
    SQLiteDatabase db = getReadableDatabase();
    Cursor c = db.rawQuery("select id ,_id ,name,price,amount,userId,updated,version from " + TABLE_PRODUCTS+" where id= \""+id+"\"", null, null);
    if (c.moveToFirst()) {
      Product p=new Product();
      p.setId(c.getString(0));
      p.set_id(c.getString(1));
      p.setName(c.getString(2));
      p.setPrice(c.getString(3));
      p.setAmount(c.getString(4));
      p.setUserId(c.getString(5));
      p.setUpdated(c.getInt(6));
      p.setVersion(c.getInt(7));
      db.close();
      return p;
    } else {
      db.close();
      return null;
    }


  }

    public void updateProduct(Product product) {
      Log.d("Database","updateProduct");
      ContentValues cv = new ContentValues();
      cv.put("name", product.getName());
      cv.put("price", product.getPrice());
      cv.put("amount", product.getAmount());
      cv.put("updated", product.getUpdated());
      cv.put("version", product.getVersion());
      SQLiteDatabase db = getReadableDatabase();
      db.update(TABLE_PRODUCTS, cv, "id= \""+product.getId()+"\"", null);
      db.close();

    }
}
