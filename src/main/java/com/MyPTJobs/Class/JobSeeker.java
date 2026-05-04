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
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "jobseeker")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@SqlResultSetMapping(
        name="JobSeekerMapping",
        classes={
                @ConstructorResult(
                        targetClass=com.MyPTJobs.Class.JobSeeker.class,
                        columns={
                                @ColumnResult(name="name"),
                                @ColumnResult(name="id", type=Integer.class),
                                @ColumnResult(name="verification"),
                                @ColumnResult(name="imageFile"),
                                @ColumnResult(name="status"),
                                @ColumnResult(name="isOffered"),
                                @ColumnResult(name="rating", type=Double.class)
                        }
                )
        }
)
@NamedNativeQuery(
        name="getRecommendationList",
        query="SELECT DISTINCT js.id, js.name, js.email, js.verification, js.imageFile, ja.status, " +
                "CASE WHEN jo.offerID IS NULL THEN '0' ELSE '1' END AS isOffered, js.rating " +
                "FROM job j JOIN jobseeker js " +
                "LEFT JOIN (SELECT * FROM jobOffer WHERE status IN ('Pending')) jo ON (j.jobID = jo.jobID AND js.id = jo.jobSeekerID) " +
                "LEFT JOIN (SELECT * FROM jobApplication ORDER BY applicationID DESC LIMIT 1) ja ON (j.jobID = ja.jobID AND js.id = ja.jobSeekerID) " +
                "WHERE (js.preferJobType = 'All' OR j.jobType = js.preferJobType) " +
                "AND j.salaryPerHours >= js.preferSalary " +
                "AND JSON_EXTRACT(js.preferDay, CONCAT('$.\"', DATE_FORMAT(j.jobDate, '%a'), '\"')) = true " +
                "AND (CASE WHEN js.preferStartTime > j.startTime " +
                "          THEN (js.preferStartTime <= j.startTime OR js.preferStartTime >= j.endTime) " +
                "          ELSE (js.preferStartTime <= j.startTime AND js.preferStartTime <= j.endTime) " +
                "     END) " +
                "AND (CASE WHEN js.preferEndTime > j.endTime " +
                "          THEN (js.preferEndTime <= j.startTime OR js.preferEndTime >= j.endTime) " +
                "          ELSE (js.preferEndTime <= j.startTime AND js.preferEndTime <= j.endTime) " +
                "     END) " +
                "AND (js.preferLocation = 'All' OR j.area = js.preferLocation) " +
                "AND j.jobID = ?1 AND js.jobSetting = 'On' AND js.verification = 'Verified' ",
        resultSetMapping="JobSeekerMapping"
)

public class JobSeeker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int(11) NOT NULL")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int id;

    @Column(name = "token", columnDefinition = "text DEFAULT NULL")
    private String token;
    @Column(name = "name", columnDefinition = "TINYTEXT DEFAULT NULL")
    private String name;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", columnDefinition = "text NOT NULL")
    private String password;

    @Column(name = "email", columnDefinition = "tinytext NOT NULL")
    private String email;

    @Column(name = "created_at", columnDefinition = "datetime DEFAULT current_timestamp()")
    private Timestamp created_at;

    @Column(name = "verification", columnDefinition = "enum('Pending', 'Failed', 'Verified','Unverified') DEFAULT NULL")
    private String verification;

    @Column(name = "jobSetting", columnDefinition = "enum('On','Off') DEFAULT NULL")
    private String jobSetting;
    @Column(name = "IC", columnDefinition = "varchar(12) DEFAULT NULL")
    private String IC;
    @Column(name = "phoneNumber", columnDefinition = "varchar(15) DEFAULT NULL")
    private String phoneNumber;
    @Column(name = "gender", columnDefinition = "enum('Male','Female') DEFAULT NULL")
    private String gender;
    @Column(name = "employmentStatus", columnDefinition = "enum('Employed','Unemployed') DEFAULT NULL")
    private String employmentStatus;
    @Column(name = "highestEducation", columnDefinition = "enum('UPSR','PMR','SPM','STPM','Polytechnics','Diploma','Degree')  DEFAULT NULL")
    private String highestEducation;
    @Column(name = "currentState", columnDefinition = "varchar(100) DEFAULT NULL")
    private String currentState;
    @Column(name = "currentCity", columnDefinition = "varchar(100) DEFAULT NULL")
    private String currentCity;

    @Column(name = "imageFile", columnDefinition = "varchar(255) DEFAULT NULL")
    private String imageFile;

    @Column(name = "rating", columnDefinition = "double NOT NULL")
    private double rating;

    @Column(name = "verificationCode", columnDefinition = "Varchar(6) NOT NULL")
    private String verificationCode;

//    @OneToMany(mappedBy = "jobSeekerID", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "workExperience", referencedColumnName = "id")
//    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="jobSeeker")
//    private List<WorkExperience> y = new ArrayList<WorkExperience>();

    //job setting
    @Column(name = "preferJobType", columnDefinition = "Enum('All','Accounting/Finance', 'Administrative/Clerical', 'Creative/Design', 'Beauty/Wellness/Fitness', 'Building/Construction', 'Call Centres/Telemarketing', 'Cleaning/Housekeeping', 'Customer Service/Receptionists', 'Drivers/Riders/Delivery', 'General Production/Operators', 'Hospitality/F&B', 'Human Resources', 'IT/Technical/Engineers', 'Nursing/Health Care', 'Others', 'Sales/Retail/Marketing', 'Secretaries/Personal Assistants', 'Security', 'Temporary/Events', 'Education/Training', 'Warehousing & Logistics', 'Manufacturing') DEFAULT NULL")
    private String preferJobType;

    @Column(name = "preferSalary", columnDefinition = "Decimal(10,2) DEFAULT 0")
    private double preferSalary;

    @Column(name = "preferLocation", columnDefinition = "tinyText DEFAULT NULL")
    private String preferLocation;

    @Column(name = "preferDistance", columnDefinition = "double DEFAULT 0")
    private double preferDistance;

    @Column(name = "preferDay", columnDefinition = "text DEFAULT NULL")
    private String preferDay;

    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Kuala_Lumpur")
    @Column(name = "preferStartTime", columnDefinition = "time DEFAULT NULL")
    private LocalTime preferStartTime;

    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Kuala_Lumpur")
    @Column(name = "preferEndTime", columnDefinition = "time DEFAULT NULL")
    private LocalTime preferEndTime ;

    @Column(name = "frontICImage", columnDefinition = "varchar(255) DEFAULT NULL")
    private String frontICImage = null;

    @Column(name = "selfieImage", columnDefinition = "varchar(255) DEFAULT NULL")
    private String selfieImage = null;
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
    private List<WorkExperience> workExperienceList;
    @Transient
    private List<Skill> skillList;
    @Transient
    private List<Language> languageList;

    @Transient
    private int jobSeekerID;

    @Transient
    private String isOffered;

    @Transient
    private String status;

    public JobSeeker(Timestamp blockDate, int blockPermanent) {
        this.blockDate = blockDate;
        this.blockPermanent = blockPermanent;
    }

    public JobSeeker(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public JobSeeker(String name, int jobSeekerID) {
        this.name = name;
        this.jobSeekerID = jobSeekerID;
    }

    public JobSeeker(String name, String email, String verification, String imageFile) {
        this.name = name;
        this.email = email;
        this.verification = verification;
        this.imageFile = imageFile;
    }

    public JobSeeker(String name, int id,  String verification, String imageFile, String status, String isOffered,  double rating) {
        this.jobSeekerID = id;
        this.name = name;
        this.verification = verification;
        this.imageFile = imageFile;
        this.isOffered = isOffered;
        this.rating = rating;
        this.status = status;
    }

    public JobSeeker(int id, String name, String email, String verification, String imageFile) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.verification = verification;
        this.imageFile = imageFile;
    }

    public JobSeeker(int id, String password) {
        this.id = id;
        this.password = password;
    }

    public JobSeeker(String preferJobType, double preferSalary, String preferLocation, double preferDistance, String preferDay, LocalTime preferStartTime, LocalTime preferEndTime) {
        this.preferJobType = preferJobType;
        this.preferSalary = preferSalary;
        this.preferLocation = preferLocation;
        this.preferDistance = preferDistance;
        this.preferDay = preferDay;
        this.preferStartTime = preferStartTime;
        this.preferEndTime = preferEndTime;
    }

    public JobSeeker(int id, String name, String email, String verification, String jobSetting, String imageFile, double rating) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.verification = verification;
        this.jobSetting = jobSetting;
        this.imageFile = imageFile;
        this.rating = rating;
    }

    public JobSeeker(int id, String name, String email, String verification, String jobSetting, String imageFile, String token, double rating) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.verification = verification;
        this.jobSetting = jobSetting;
        this.imageFile = imageFile;
        this.token = token;
        this.rating = rating;
    }

    public JobSeeker(int id, String name, String IC, String phoneNumber, String gender, String employmentStatus, String highestEducation, String imageFile, String verification) {
        this.id = id;
        this.name = name;
        this.IC = IC;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.employmentStatus = employmentStatus;
        this.highestEducation = highestEducation;
        this.imageFile = imageFile;
        this.verification = verification;
    }
    public JobSeeker() {
        this.token = null;
        this.name = null;
        this.password = null;
        this.email = null;
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.verification = "Unverified";
        this.jobSetting = "Off";
        this.IC = null;
        this.phoneNumber = null;
        this.gender = null;
        this.employmentStatus = null;
        this.highestEducation = null;
        this.currentState = null;
        this.currentCity = null;
        this.imageFile = null;
        this.rating = 0;
        this.isOffered = "0";
        this.notificationToken = null;
    }

    public JobSeeker(int id, String currentState, String currentCity) {
        this.id = id;
        this.currentState = currentState;
        this.currentCity = currentCity;
    }

    public JobSeeker(String name, String imageFile, Date verificationDate, int jobSeekerID) {
        this.name = name;
        this.imageFile = imageFile;
        this.verificationDate = new Timestamp(verificationDate.getTime());
        this.jobSeekerID = jobSeekerID;
    }

    public JobSeeker(String name, String IC, String frontICImage, String selfieImage, Date verificationDate, int jobSeekerID) {
        this.name = name;
        this.IC = IC;
        this.frontICImage = frontICImage;
        this.selfieImage = selfieImage;
        this.verificationDate = new Timestamp(verificationDate.getTime());
        this.jobSeekerID = jobSeekerID;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getIC() {
        return IC;
    }

    public void setIC(String IC) {
        this.IC = IC;
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

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public String getHighestEducation() {
        return highestEducation;
    }

    public void setHighestEducation(String highestEducation) {
        this.highestEducation = highestEducation;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getJobSetting() {
        return jobSetting;
    }

    public void setJobSetting(String jobSetting) {
        this.jobSetting = jobSetting;
    }

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }

    public void login() {

    }

    public void signIn() {
//        jobSeekerRepository.save()
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

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public List<WorkExperience> getWorkExperienceList() {
        return workExperienceList;
    }

    public void setWorkExperienceList(List<WorkExperience> workExperienceList) {
        this.workExperienceList = workExperienceList;
    }

    public List<Skill> getSkillList() {
        return skillList;
    }

    public void setSkillList(List<Skill> skillList) {
        this.skillList = skillList;
    }

    public List<Language> getLanguageList() {
        return languageList;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public void setLanguageList(List<Language> languageList) {
        this.languageList = languageList;
    }

    public void generateToken() throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        AESUtil AESToken = new AESUtil();
        token = AESToken.encryptAES(id + AESToken.getJobSeekerKey()+ new function().getRandomNumberString());
    }

    public String getPreferJobType() {
        return preferJobType;
    }

    public void setPreferJobType(String preferJobType) {
        this.preferJobType = preferJobType;
    }

    public double getPreferSalary() {
        return preferSalary;
    }

    public void setPreferSalary(double preferSalary) {
        this.preferSalary = preferSalary;
    }

    public String getPreferLocation() {
        return preferLocation;
    }

    public void setPreferLocation(String preferLocation) {
        this.preferLocation = preferLocation;
    }

    public double getPreferDistance() {
        return preferDistance;
    }

    public void setPreferDistance(double preferDistance) {
        this.preferDistance = preferDistance;
    }

    public String getPreferDay() {
        return preferDay;
    }

    public void setPreferDay(String preferDay) {
        this.preferDay = preferDay;
    }

    public LocalTime getPreferStartTime() {
        return preferStartTime;
    }

    public void setPreferStartTime(LocalTime preferStartTime) {
        this.preferStartTime = preferStartTime;
    }

    public LocalTime getPreferEndTime() {
        return preferEndTime;
    }

    public void setPreferEndTime(LocalTime preferEndTime) {
        this.preferEndTime = preferEndTime;
    }

    public int getJobSeekerID() {
        return jobSeekerID;
    }

    public void setJobSeekerID(int jobSeekerID) {
        this.jobSeekerID = jobSeekerID;
    }

    public String getIsOffered() {
        return isOffered;
    }

    public void setIsOffered(String isOffered) {
        this.isOffered = isOffered;
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

    public Timestamp getVerificationDate() {
        return verificationDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }
}
