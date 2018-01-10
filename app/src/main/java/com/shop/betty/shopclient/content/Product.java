package com.shop.betty.shopclient.content;

/**
 * Created by Betty on 11/14/2017.
 */

public class Product {
    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", amount='" + amount + '\'' +
                ", id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", status=" + status +
                ", updated=" + updated +
                ", version=" + version +
                '}';
    }

    public enum Status {
        active,
        archived;
    }

    private String name;
    private String price;
    private String amount;

    private String _id;
    private String id;
    private String userId;
    private Status status = Status.active;
    private long updated;
    private int version;


    public Product(){}


    public Product(String name, String price, String amount, String id,String _id, String userId, Status status, long updated, int version) {
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.id = id;
        this._id=_id;
        this.userId = userId;
        this.status = status;
        this.updated = updated;
        this.version = version;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
