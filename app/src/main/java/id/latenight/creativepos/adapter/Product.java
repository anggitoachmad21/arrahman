package id.latenight.creativepos.adapter;

public class Product {
    private String imageurl;
    private String title;
    private int price;
    private int reseller_price;
    private int outlet_price;
    private int partner_price;
    private int online_price;
    private int ingredient_stock;
    private int id;
    private String customer_name;

    public Product(int id, String imageurl, String title, int price, int reseller_price, int outlet_price, int partner_price, int online_price, int ingredient_stock, String customer_name) {
        this.id = id;
        this.imageurl = imageurl;
        this.title = title;
        this.price = price;
        this.reseller_price = reseller_price;
        this.outlet_price = outlet_price;
        this.partner_price = partner_price;
        this.online_price = online_price;
        this.ingredient_stock = ingredient_stock;
        this.customer_name = customer_name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getTitle() {
        return title;
    }

    public int getPrice() {
        return price;
    }

    public int getReseller_price() {
        return reseller_price;
    }

    public int getOutlet_price() {
        return outlet_price;
    }

    public int getPartner_price() {
        return partner_price;
    }

    public int getOnline_price() {
        return online_price;
    }

    public int getIngredient_stock() {
        return ingredient_stock;
    }

    public int getId() {
        return id;
    }

    public String getCustomer_name() {
        return customer_name;
    }
}
