package id.latenight.creativepos.adapter.sampler;

public class Tables {
    private int id;
    private String name;
    private int tables_id;

    public Tables(int id, String name, int tables_id) {
        this.id = id;
        this.name = name;
        this.tables_id = tables_id;
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

    public int getTables_id() {
        return tables_id;
    }

    public void setTables_id(int tables_id) {
        this.tables_id = tables_id;
    }
}
