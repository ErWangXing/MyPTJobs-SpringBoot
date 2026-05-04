package com.MyPTJobs.Class;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "job")
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jobID", columnDefinition = "int(11) NOT NULL")
    private int id;

    @Column(name = "title", columnDefinition = "varchar(255) DEFAULT NULL")
    private String title;
    @Column(name = "description", columnDefinition = "Text  DEFAULT NULL")
    private String description;

    @Column(name = "salaryPerHours", columnDefinition = "decimal(10,2) DEFAULT 0")
    private double salaryPerHours;


    @Column(name = "image", columnDefinition = "varchar(255)  NULL")
    private String image;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Kuala_Lumpur")
    @Column(name = "created_at", columnDefinition = "datetime DEFAULT current_timestamp()")
    private Timestamp created_at;

    @Column(name = "latitude", columnDefinition = "varchar(255) DEFAULT NULL")
    private String latitude;

    @Column(name = "longitude", columnDefinition = "varchar(255) DEFAULT NULL")
    private String longitude;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "jobDate", columnDefinition = "date DEFAULT null")
    private String date;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "jobEndDate", columnDefinition = "date DEFAULT null")
    private String endDate;

    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Kuala_Lumpur")
    @Column(name = "startTime", columnDefinition = "time DEFAULT null")
    private LocalTime startTime;
    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Kuala_Lumpur")
    @Column(name = "endTime", columnDefinition = "time DEFAULT null")
    private LocalTime endTime;

    @Column(name = "location", columnDefinition = "Text DEFAULT NULL")
    private String location;

    @Column(name = "employerID", columnDefinition = "int(11) DEFAULT NULL")
    private int employerID;

    @Column(name = "deleted_at", columnDefinition = "datetime DEFAULT NULL")
    private LocalTime deleted_at;

    @Column(name = "jobType", columnDefinition = "Enum('Accounting/Finance', 'Administrative/Clerical', 'Creative/Design', 'Beauty/Wellness/Fitness', 'Building/Construction', 'Call Centres/Telemarketing', 'Cleaning/Housekeeping', 'Customer Service/Receptionists', 'Drivers/Riders/Delivery', 'General Production/Operators', 'Hospitality/F&B', 'Human Resources', 'IT/Technical/Engineers', 'Nursing/Health Care', 'Others', 'Sales/Retail/Marketing', 'Secretaries/Personal Assistants', 'Security', 'Temporary/Events', 'Education/Training', 'Warehousing & Logistics', 'Manufacturing') DEFAULT NULL DEFAULT NULL")
    private String jobType;

    @Column(name = "area", columnDefinition = "tinyText DEFAULT NULL")
    private String area;

    @Column(name = "jobStatus", columnDefinition = "Enum('Active', 'Inactive')  DEFAULT 'Active'")
    private String jobStatus;

    @Transient
    private String isFavourite;

    @Transient
    private String isApplied;

    @Transient
    private String status;

    @Transient
    private int processOrNot;

    @Transient
    private Employer employer;

    public Job(){}

    public Job(int id, String title, String description, double salaryPerHours, String image, Timestamp created_at, String latitude, String longitude, String date, LocalTime startTime, LocalTime endTime, String location, int employerID, String endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.salaryPerHours = salaryPerHours;
        this.image = image;
        this.created_at = created_at;
        this.latitude = latitude;
        this.longitude= longitude;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.employerID = employerID;
        this.endDate = endDate;
    }


    public Job(int id,String title, String description, double salaryPerHours, String latitude, String longitude, String date, LocalTime  startTime, LocalTime  endTime, String location, int employerID, String image, String status, String endDate) {
        this.id = id;this.title = title;
        this.description = description;
        this.salaryPerHours = salaryPerHours;
        this.latitude = latitude;
        this.longitude= longitude;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.employerID = employerID;
        this.status = status;
        this.endDate = endDate;
    }

    public Job(String title, String description, double salaryPerHours, String latitude, String longitude, String date, LocalTime  startTime, LocalTime  endTime, String location, String endDate) {
        this.title = title;
        this.description = description;
        this.salaryPerHours = salaryPerHours;
        this.latitude = latitude;
        this.longitude= longitude;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.endDate = endDate;
    }
    public Job(int id,String title, String description, double salaryPerHours, String latitude, String longitude, String date, LocalTime  startTime, LocalTime  endTime, String location, int employerID, String image, String area, Date created_at, String endDate, Employer employer) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.salaryPerHours = salaryPerHours;
        this.latitude = latitude;
        this.longitude= longitude;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.employerID = employerID;
        this.image = image;
        this.area = area;
        this.created_at = new Timestamp(created_at.getTime());
        this.employer = employer;
        this.endDate = endDate;
    }

    public Job(String jobStatus, int id,String title, String description, double salaryPerHours, String latitude, String longitude, String date, LocalTime  startTime, LocalTime  endTime, String location, int employerID, String image, String endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.salaryPerHours = salaryPerHours;
        this.latitude = latitude;
        this.longitude= longitude;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.employerID = employerID;
        this.image = image;
        this.endDate = endDate;
        this.jobStatus = jobStatus;
    }

    public Job(int id,String title, String description, double salaryPerHours, String latitude, String longitude, String date, LocalTime  startTime, LocalTime  endTime, String location, int employerID, String image, String endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.salaryPerHours = salaryPerHours;
        this.latitude = latitude;
        this.longitude= longitude;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.employerID = employerID;
        this.image = image;
        this.endDate = endDate;
    }

    public Job(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getSalaryPerHours() {
        return salaryPerHours;
    }

    public void setSalaryPerHours(double salaryPerHours) {
        this.salaryPerHours = salaryPerHours;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude= longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LocalTime  getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime  startTime) {
        this.startTime = startTime;
    }

    public LocalTime  getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime  endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getEmployerID() {
        return employerID;
    }

    public void setEmployerID(int employerID) {
        this.employerID = employerID;
    }

    public LocalTime getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(LocalTime deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(String isFavourite) {
        this.isFavourite = isFavourite;
    }

    public String getIsApplied() {
        return isApplied;
    }

    public void setIsApplied(String isApplied) {
        this.isApplied = isApplied;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getProcessOrNot() {
        return processOrNot;
    }

    public void setProcessOrNot(int processOrNot) {
        this.processOrNot = processOrNot;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public List<String> getDatesBetween() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<String> dates = new ArrayList<>();

        try {
            Date startDate = dateFormat.parse(this.date);
            Date endDate = dateFormat.parse(this.endDate);

            long startTime = startDate.getTime();
            long endTime = endDate.getTime();

            long dayInMillis = 24 * 60 * 60 * 1000; // 1 day in milliseconds

            for (long time = startTime; time <= endTime; time += dayInMillis) {
                Date date = new Date(time);
                String formattedDate = dateFormat.format(date);
                dates.add(formattedDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dates;
    }
}
