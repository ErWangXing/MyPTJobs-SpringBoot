package com.MyPTJobs.Class;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

@Entity
@Table(name = "administrator")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Administrator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adminID", columnDefinition = "int(11) NOT NULL")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int adminID;

    @Column(name = "name", columnDefinition = "text DEFAULT NULL")
    private String name;

    @Column(name = "username", columnDefinition = "text DEFAULT NULL")
    private String username;

    @Column(name = "email", columnDefinition = "text DEFAULT NULL")
    private String email;

    @Column(name = "password", columnDefinition = "text DEFAULT NULL")
    private String password;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kuala_Lumpur")
    @Column(name = "createdAt", columnDefinition = "datetime DEFAULT current_timestamp()")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "imageFile", columnDefinition = "varchar(255) DEFAULT NULL")
    private String imageFile;

    @Column(name = "token", columnDefinition = "text DEFAULT NULL")
    private String token;

    @Column(name = "verificationCode", columnDefinition = "Varchar(6) NOT NULL")
    private String verificationCode;

    public Administrator(int adminID, String name, String username, String email, String imageFile) {
        this.adminID = adminID;
        this.name = name;
        this.username = username;
        this.email = email;
        this.imageFile = imageFile;
    }

    public Administrator(int adminID, String password) {
        this.adminID = adminID;
        this.password = password;
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Administrator() {
    }

    public void generateToken() throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        AESUtil AESToken = new AESUtil();
        token = AESToken.encryptAES(adminID + AESToken.getAdminKey()+new function().getRandomNumberString());
    }


}
