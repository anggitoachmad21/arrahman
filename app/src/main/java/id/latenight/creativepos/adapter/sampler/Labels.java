package id.latenight.creativepos.adapter.sampler;

public class Labels {
    private int id;
    private String name;
    private int sub_categories_id;
    private int label_id;

    public Labels(int id, String name, int sub_categories_id, int label_id) {
        this.id = id;
        this.name = name;
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
