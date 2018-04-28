package ir.amozkade.hamed.webservicelib;


import java.sql.Timestamp;

public class User {
    private Integer userCode;
    private String userName;
    private String userPass;
    private Timestamp lastLogin;
    private String token;
    private String email;
    private String mobile;
    private String name;
    private String family;
    private Boolean paid;


    public Integer getUserCode() {
        return userCode;
    }

    public void setUserCode(Integer userCode) {
        this.userCode = userCode;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }


    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLoginDate) {
        this.lastLogin = lastLoginDate;
    }



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }


    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

}

