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
import java.util.Date;

@Entity
@Table(name = "employer")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employerID", columnDefinition = "int(11) NOT NULL")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int id;

    @Column(name = "token", columnDefinition = "text DEFAULT NULL")
    private String token;
    @Column(name = "employerName", columnDefinition = "varchar(255)  DEFAULT NULL")
    private String employerName;

    @Column(name = "companyName", columnDefinition = "varchar(255) DEFAULT NULL")
    private String companyName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", columnDefinition = "text NOT NULL")
    private String password;

    @Column(name = "IC", columnDefinition = "varchar(12) DEFAULT NULL")
    private String IC;

    @Column(name = "email", columnDefinition = "tinytext NOT NULL")
    private String email;

    @Column(name = "created_at", columnDefinition = "datetime DEFAULT current_timestamp()")
    private Timestamp created_at;

    @Column(name = "verification", columnDefinition = "enum('Pending', 'Failed', 'Verified','Unverified') DEFAULT NULL")
    private String verification;

    @Column(name = "phoneNumber", columnDefinition = "varchar(15) DEFAULT NULL")
    private String phoneNumber;
    @Column(name = "gender", columnDefinition = "enum('Male','Female') DEFAULT NULL")
    private String gender;

    @Column(name = "imageFile", columnDefinition = "varchar(255) DEFAULT NULL")
    private String imageFile;

    @Column(name = "rating", columnDefinition = "double NOT NULL")
    private double rating;

    @Column(name = "location", columnDefinition = "Text DEFAULT NULL")
    private String location;

    @Column(name = "operatingHours", columnDefinition = "Text DEFAULT NULL")
    private String operatingHours;

    @Column(name = "ssm", columnDefinition = "Varchar(12) DEFAULT NULL")
    private String ssm;

    @Column(name = "companyEmail", columnDefinition = "tinyText DEFAULT NULL")
    private String companyEmail;

    @Column(name = "companyDescription", columnDefinition = "tinyText DEFAULT NULL")
    private String companyDescription;

    @Column(name = "verificationCode", columnDefinition = "Varchar(6) NOT NULL")
    private String verificationCode;

    @Column(name = "SSMImage", columnDefinition = "varchar(255) DEFAULT NULL")
    private String SSMImage;

    @Column(name = "frontICImage", columnDefinition = "varchar(255) DEFAULT NULL")
    private String frontICImage;

    @Column(name = "selfieImage", columnDefinition = "varchar(255) DEFAULT NULL")
    private String selfieImage;

    @Column(name = "state", columnDefinition = "varchar(100) DEFAULT NULL")
    private String state;
    @Column(name = "city", columnDefinition = "varchar(100) DEFAULT NULL")
    private String city;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kuala_Lumpur")
    @Column(name = "verificationDate", columnDefinition = "datetime DEFAULT NULL")
    private Timestamp verificationDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kuala_Lumpur")
    @Column(name = "blockDate", columnDefinition = "datetime DEFAULT NULL")
    private Timestamp blockDate;

    @Column(name = "blockPermanent", columnDefinition = "int(1) NOT NULL DEFAULT 0 ")
    private int blockPermanent = 0;

    @Column(name = "notificationToken", columnDefinition = "text DEFAULT NULL")
    private String notificationToken ;


    @Transient
    private int employerID;


    public Employer(){}

    public Employer(String companyName) {
        this.companyName = companyName;
    }

    public Employer(Timestamp blockDate, int blockPermanent) {
        this.blockDate = blockDate;
        this.blockPermanent = blockPermanent;
    }

    public Employer(int id, String password) {
        this.id = id;
        this.password = password;
    }

    public Employer(String email, String password) {
        this.password = password;
        this.email = email;
    }

    public Employer(int id, String companyName, String email, String verification, String imageFile, double rating) {
        this.id = id;
        this.companyName = companyName;
        this.email = email;
        this.verification = verification;
        this.imageFile = imageFile;
        this.rating = rating;
    }

    public Employer(String companyName, String verification, String imageFile, double rating) {
        this.companyName = companyName;
        this.verification = verification;
        this.imageFile = imageFile;
        this.rating = rating;
    }

    public Employer(int employerID, String companyName, String imageFile, Date verificationDate) {
        this.employerID = employerID;
        this.companyName = companyName;
        this.imageFile = imageFile;
        this.verificationDate = new Timestamp(verificationDate.getTime());
    }

    public Employer(int employerID, String companyName, String imageFile, int blockPermanent) {
        this.employerID = employerID;
        this.companyName = companyName;
        this.imageFile = imageFile;
        this.blockPermanent = blockPermanent;
    }

    public Employer(String companyName, String companyEmail, String phoneNumber, String location, String ssm, String imageFile, int id, String verification) {
        this.companyName = companyName;
        this.companyEmail = companyEmail;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.ssm = ssm;
        this.imageFile = imageFile;
        this.id  = id;
        this.verification = verification;
    }

    public Employer(String companyName, String companyEmail, String phoneNumber, String location, String ssm, String imageFile, int id, String verification, String state, String city) {
        this.companyName = companyName;
        this.companyEmail = companyEmail;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.ssm = ssm;
        this.imageFile = imageFile;
        this.id  = id;
        this.verification = verification;
        this.state = state;
        this.city = city;
    }

    public Employer ( String companyDescription, int id){
        this.companyDescription = companyDescription;
        this.id = id;
    }

    public Employer(int id, String employerName, String companyName, String IC, String ssm, String SSMImage, String frontICImage, String selfieImage, Date verificationDate, String imageFile) {
        this.id = id;
        this.employerName = employerName;
        this.companyName = companyName;
        this.IC = IC;
        this.ssm = ssm;
        this.SSMImage = SSMImage;
        this.frontICImage = frontICImage;
        this.selfieImage = selfieImage;
        this.verificationDate = new Timestamp(verificationDate.getTime());
        this.imageFile = imageFile;
    }



    public Employer (int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }

    public String getSsm() {
        return ssm;
    }

    public void setSsm(String ssm) {
        this.ssm = ssm;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getSSMImage() {
        return SSMImage;
    }

    public void setSSMImage(String SSMImage) {
        this.SSMImage = SSMImage;
    }

    public String getFrontICImage() {
        return frontICImage;
    }

    public void setFrontICImage(String frontICImage) {
        this.frontICImage = frontICImage;
    }

    public String getSelfieImage() {
        return selfieImage;
    }

    public void setSelfieImage(String selfieImage) {
        this.selfieImage = selfieImage;
    }

    public String getIC() {
        return IC;
    }

    public void setIC(String IC) {
        this.IC = IC;
    }
    public Timestamp getVerificationDate() {
        return verificationDate;
    }

    public int getEmployerID() {
        return employerID;
    }

    public void setEmployerID(int employerID) {
        this.employerID = employerID;
    }

    public void setVerificationDate(Timestamp verificationDate) {
        this.verificationDate = verificationDate;
    }

    public Timestamp getBlockDate() {
        return blockDate;
    }

    public void setBlockDate(Timestamp blockDate) {
        this.blockDate = blockDate;
    }

    public int getBlockPermanent() {
        return blockPermanent;
    }

    public void setBlockPermanent(int blockPermanent) {
        this.blockPermanent = blockPermanent;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    public void generateToken() throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        AESUtil AESToken = new AESUtil();
        token = AESToken.encryptAES(id + AESToken.getEmployerKey()+new function().getRandomNumberString());
    }

}