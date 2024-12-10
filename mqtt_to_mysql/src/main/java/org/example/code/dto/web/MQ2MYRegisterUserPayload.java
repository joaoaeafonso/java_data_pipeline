package org.example.code.dto.web;

public class MQ2MYRegisterUserPayload {

    private String username;
    private String name;
    private String telefone;
    private String password;
    private String userType;


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getUserType() {
        return userType;
    }

}
