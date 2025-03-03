package id.latenight.creativepos.adapter.sampler;

public class Employee {
    private int id;
    private String name;

    private int employee_id;
    private String detail_employee;

    public Employee(int id, String name, int employee_id, String detail_employee) {
        this.id = id;
        this.name = name;
        this.employee_id = employee_id;
        this.detail_employee = detail_employee;
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

    public int getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(int employee_id) {
        this.employee_id = employee_id;
    }

    public String getDetail_employee() {
        return detail_employee;
    }

    public void setDetail_employee(String detail_employee) {
        this.detail_employee = detail_employee;
    }
}
