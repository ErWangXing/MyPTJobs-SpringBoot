
package com.MyPTJobs.Class;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "jobOffer")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offerID", columnDefinition = "int(11) NOT NULL")
    private int offerID;
    @Column(name = "jobID", columnDefinition = "int(11) default null")
    private int jobID;
    @Column(name = "jobSeekerID", columnDefinition = "int(11) default null")
    private int jobSeekerID;

    @Column(name = "status", columnDefinition = "enum('Pending', 'Completed','Accepted', 'Rejected') default 'Pending'")
    private String status = "Pending";

    @Transient
    private int processOrNot;

    @Transient
    private Job job;

    @Transient
    private JobSeeker jobSeeker;

    @Transient
    private Employer employer;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kuala_Lumpur")
    @Column(name = "offerDate", columnDefinition = "datetime DEFAULT current_timestamp()")
    private Timestamp offerDate = new Timestamp(System.currentTimeMillis());

    public JobOffer() {
    }

    public JobOffer(int jobSeekerID, int jobID) {
        this.jobID = jobID;
        this.jobSeekerID = jobSeekerID;
    }


    public JobOffer(int offerID, Job job, String status, Date offerDate, int processOrNot, Employer employer) {
        this.offerID = offerID;
        this.job = job;
        this.status = status;
        this.offerDate = new Timestamp(offerDate.getTime());
        this.processOrNot = processOrNot;
        this.employer =  new Employer(employer.getCompanyName());
    }


    public JobOffer(int offerID, String status, Job job, JobSeeker jobSeeker, Date offerDate) {
        this.offerID = offerID;
        this.status = status;
        this.job = job;
        this.jobSeeker = new JobSeeker(jobSeeker.getName(),jobSeeker.getEmail(),jobSeeker.getVerification(), jobSeeker.getImageFile());
        this.offerDate = new Timestamp(offerDate.getTime());
    }

    public JobOffer(int offerID, String status, Job job, JobSeeker jobSeeker, Date offerDate, int jobSeekerID, int processOrNot) {
        this.offerID = offerID;
        this.status = status;
        this.job = job;
        this.jobSeeker = new JobSeeker(jobSeeker.getName(),jobSeeker.getEmail(),jobSeeker.getVerification(), jobSeeker.getImageFile());
        this.offerDate = new Timestamp(offerDate.getTime());
        this.jobSeekerID = jobSeekerID;
        this.processOrNot = processOrNot;
    }



    public int getOfferID() {
        return offerID;
    }

    public void setOfferID(int offerID) {
        this.offerID = offerID;
    }

    public int getJobID() {
        return jobID;
    }

    public void setJobID(int jobID) {
        this.jobID = jobID;
    }

    public int getJobSeekerID() {
        return jobSeekerID;
    }

    public void setJobSeekerID(int jobSeekerID) {
        this.jobSeekerID = jobSeekerID;
    }

    public Timestamp getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(Timestamp offerDate) {
        this.offerDate = offerDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
