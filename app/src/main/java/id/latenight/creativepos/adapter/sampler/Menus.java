package id.latenight.creativepos.adapter.sampler;

public class Menus {
    private int id;
    private String name;
    private String photo;
    private int sale_price;
    private int online_price;
    private int outlet_price;
    private int reseller_price;
    private int partner_price;
    private int ingredient_stock;
    private int menu_id;
    private int categories_id;
    private int sub_categories_id;
    private int label_id;

    public Menus(int id, String name, String photo, int sale_price, int online_price, int outlet_price,
                 int reseller_price, int partner_price, int ingredient_stock, int menu_id, int categories_id,
                 int sub_categories_id, int label_id) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.sale_price = sale_price;
        this.online_price = online_price;
        this.outlet_price = outlet_price;
        this.reseller_price = reseller_price;
        this.partner_price = partner_price;
        this.ingredient_stock = ingredient_stock;
        this.menu_id = menu_id;
        this.categories_id = categories_id;
        this.sub_categories_id = sub_categories_id;
        this.label_id = label_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getSale_price() {
        return sale_price;
    }

    public void setSale_price(int sale_price) {
        this.sale_price = sale_price;
    }

    public int getOnline_price() {
        return online_price;
    }

    public void setOnline_price(int online_price) {
        this.online_price = online_price;
    }

    public int getOutlet_price() {
        return outlet_price;
    }

    public void setOutlet_price(int outlet_price) {
        this.outlet_price = outlet_price;
    }

    public int getReseller_price() {
        return reseller_price;
    }

    public void setReseller_price(int reseller_price) {
        this.reseller_price = reseller_price;
    }

    public int getPartner_price() {
        return partner_price;
    }

    public void setPartner_price(int partner_price) {
        this.partner_price = partner_price;
    }

    public int getIngredient_stock() {
        return ingredient_stock;
    }

    public void setIngredient_stock(int ingredient_stock) {
        this.ingredient_stock = ingredient_stock;
    }

    public int getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(int menu_id) {
        this.menu_id = menu_id;
    }

    public int getCategories_id() {
        return categories_id;
    }

    public void setCategories_id(int categories_id) {
        this.categories_id = categories_id;
    }

    public int getSub_categories_id() {
        return sub_categories_id;
    }

    public void setSub_categories_id(int sub_categories_id) {
        this.sub_categories_id = sub_categories_id;
    }

    public int getLabel_id() {
        return label_id;
    }

    public void setLabel_id(int label_id) {
        this.label_id = label_id;
    }
}
