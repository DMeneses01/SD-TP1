package server2;

public class Client_Class {

    public String username;
    public String password;
    public String department;
    public String telephone;
    public String address;
    public String cc_expiration;
    public String path;

    public Client_Class(String username, String password, String path, String department, String telephone, String address, String cc_expiration){
        this.username = username;
        this.password = password;
        this.path = path;
        this.department = department;
        this.telephone = telephone;
        this.address = address;
        this.cc_expiration = cc_expiration;
    }
}