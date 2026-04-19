package model;

public class Customer {
    private int customerID;
    private String name;
    private String email;
    private String phone;
    private boolean isAdmin;

    public Customer(int customerID, String name, String email, String phone, boolean isAdmin) {
        this.customerID = customerID;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.isAdmin = isAdmin;
    }

    public int getCustomerID() { return customerID; }
    public String getName()    { return name; }
    public String getEmail()   { return email; }
    public String getPhone()   { return phone; }
    public boolean isAdmin()   { return isAdmin; }

    @Override
    public String toString() {
        return "Customer{id=" + customerID + ", name=" + name + ", admin=" + isAdmin + "}";
    }
}
