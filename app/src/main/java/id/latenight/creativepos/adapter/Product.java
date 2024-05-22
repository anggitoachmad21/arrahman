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

    public Product(int id, String imageurl, String title, int price, int reseller_price, int outlet_price, int partner_price, int online_price, int ingredient_stock) {
        this.id = id;
        this.imageurl = imageurl;
        this.title = title;
        this.price = price;
        this.reseller_price = reseller_price;
        this.outlet_price = outlet_price;
        this.partner_price = partner_price;
        this.online_price = online_price;
        this.ingredient_stock = ingredient_stock;
    }

    public int getId() {
        return id;
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
    public int getResellerPrice() {
        return reseller_price;
    }
    public int getOutletPrice() {
        return outlet_price;
    }
    public int getPartnerPrice() {
        return partner_price;
    }
    public int getOnlinePrice() {
        return online_price;
    }
    public int getIngredientStock() {
        return ingredient_stock;
    }
}
