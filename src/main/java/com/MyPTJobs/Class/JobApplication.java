
package com.MyPTJobs.Class;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "jobApplication")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applicationID", columnDefinition = "int(11) NOT NULL")
    private int applicationID;
    @Column(name = "jobID", columnDefinition = "int(11) default null")
    private int jobID;
    @Column(name = "jobSeekerID", columnDefinition = "int(11) default null")
    private int jobSeekerID;

    @Column(name = "status", columnDefinition = "enum('Apply', 'Rejected','Completed','Accepted', 'User Reject', 'Interview','User Accept', 'Done', 'Offer Accepted') default 'Apply'")
    private String status;

    @Column(name = "selectedDate", columnDefinition = "text default null")
    private String selectedDate;

    @Transient
    private String location = null;

    @Transient
    private Job job;

    @Transient
    private JobSeeker jobSeeker;

    @Transient
    private int processOrNot;

    @Column(name = "applyDate", columnDefinition = "datetime DEFAULT current_timestamp()")
    private Timestamp applyDate;

    public JobApplication() {
    }

    public JobApplication(int jobSeekerID, int jobID, String selectedDate) {
        this.jobID = jobID;
        this.jobSeekerID = jobSeekerID;
        this.selectedDate = selectedDate;
    }

    public JobApplication(int jobID, int jobSeekerID, String status, Timestamp applyDate, String selectedDate) {
        this.jobID = jobID;
        this.jobSeekerID = jobSeekerID;
        this.status = status;
        this.applyDate = applyDate;
        this.selectedDate   = selectedDate;
    }

    public JobApplication(int applicationID, Job job, String status, Date applyDate, int processOrNot, String selectedDate) {
        this.applicationID = applicationID;
        this.job = job;
        this.status = status;
        this.applyDate = new Timestamp(applyDate.getTime());
        this.processOrNot = processOrNot;
        this.selectedDate = selectedDate;
    }


    public JobApplication(int applicationID, String status, Job job, JobSeeker jobSeeker, Date applyDate, String selectedDate) {
        this.applicationID = applicationID;
        this.status = status;
        this.job = job;
        this.jobSeeker = new JobSeeker(jobSeeker.getName(),jobSeeker.getEmail(),jobSeeker.getVerification(), jobSeeker.getImageFile());
        this.applyDate = new Timestamp(applyDate.getTime());
        this.selectedDate = selectedDate;
    }

    public JobApplication(int applicationID, String status, Job job, JobSeeker jobSeeker, Date applyDate, int jobSeekerID, String location, int processOrNot, String selectedDate) {
        this.applicationID = applicationID;
        this.status = status;
        this.job = job;
        this.jobSeeker = new JobSeeker(jobSeeker.getName(),jobSeeker.getEmail(),jobSeeker.getVerification(), jobSeeker.getImageFile());
        this.applyDate = new Timestamp(applyDate.getTime());
        this.jobSeekerID = jobSeekerID;
        this.location = location;
        this.processOrNot = processOrNot;
        this.selectedDate = selectedDate;
    }



    public int getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(int applicationID) {
        this.applicationID = applicationID;
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

    public Timestamp getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Timestamp applyDate) {
        this.applyDate = applyDate;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }public int getProcessOrNot() {
        return processOrNot;
    }

    public void setProcessOrNot(int processOrNot) {
        this.processOrNot = processOrNot;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }
}
