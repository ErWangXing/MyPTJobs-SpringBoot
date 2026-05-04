
package com.MyPTJobs.Class;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "rating")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ratingID", columnDefinition = "int(11) NOT NULL")
    private int ratingID;
    @Column(name = "jobSeekerID", columnDefinition = "int(11) default null")
    private int jobSeekerID;

    @Column(name = "applicationID", columnDefinition = "int(11) default null")
    private int applicationID;

    @Column(name = "rating", columnDefinition = "text default null")
    private String rating;

    @Column(name = "averageRating", columnDefinition = "double default null")
    private Double averageRating;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kuala_Lumpur")
    @Column(name = "createdAt", columnDefinition = "datetime DEFAULT current_timestamp()")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "employerID", columnDefinition = "int(11) default null")
    private int employerID;

    @Transient
    private Job job;

    @Transient
    private JobSeeker jobSeeker;

    public Rating(int applicationID, String rating, Double averageRating, int jobSeekerID) {
        this.applicationID = applicationID;
        this.rating = rating;
        this.averageRating = averageRating;
        this.jobSeekerID = jobSeekerID;
    }

    public Rating(String rating, Double averageRating, Date createdAt, Job job) {
        this.rating = rating;
        this.averageRating = averageRating;
        this.createdAt =  new Timestamp(createdAt.getTime());
        this.job = new Job(job.getId(), job.getTitle());
    }

    public Rating(String rating, Double averageRating, Date createdAt, JobSeeker jobSeeker) {
        this.rating = rating;
        this.averageRating = averageRating;
        this.createdAt =  new Timestamp(createdAt.getTime());
        this.jobSeeker = new JobSeeker(jobSeeker.getName(), jobSeeker.getId());
    }

    public Rating(int applicationID, String rating, int employerID, Double averageRating) {
        this.applicationID = applicationID;
        this.rating = rating;
        this.averageRating = averageRating;
        this.employerID = employerID;
    }

    public Rating() {
    }

    public int getRatingID() {
        return ratingID;
    }

    public void setRatingID(int ratingID) {
        this.ratingID = ratingID;
    }

    public int getJobSeekerID() {
        return jobSeekerID;
    }

    public void setJobSeekerID(int jobSeekerID) {
        this.jobSeekerID = jobSeekerID;
    }

    public int getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(int applicationID) {
        this.applicationID = applicationID;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getEmployerID() {
        return employerID;
    }

    public void setEmployerID(int employerID) {
        this.employerID = employerID;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public JobSeeker getJobSeeker() {
        return jobSeeker;
    }

    public void setJobSeeker(JobSeeker jobSeeker) {
        this.jobSeeker = jobSeeker;
    }
}
