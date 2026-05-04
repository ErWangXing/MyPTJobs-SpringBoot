package com.MyPTJobs.Class;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "jobInterview")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobInterview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interviewID", columnDefinition = "int(11) NOT NULL")
    private int interviewID;
    @Column(name = "applicationID", columnDefinition = "int(11) default null")
    private int applicationID;
    @Column(name = "status", columnDefinition = "enum('Pending', 'Rejected','Completed','Accepted', 'User Reject', 'User Accept') default 'Pending'")
    private String status = "Pending";
    @Column(name = "location", columnDefinition = "Text default null")
    private String location = null;
    @Transient
    private JobApplication jobApplication;

    @Transient
    private Job job;

    @Transient
    private JobSeeker jobSeeker;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "interviewDate", columnDefinition = "date DEFAULT null")
    private String interviewDate;

    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Kuala_Lumpur")
    @Column(name = "interviewTime", columnDefinition = "time DEFAULT null")
    private LocalTime interviewTime;

    @Transient
    private int processOrNot;

    @Transient
    private String selectedDate;
    public JobInterview() {
    }

    public JobInterview(int applicationID) {
        this.applicationID = applicationID;
    }

    public JobInterview(int interviewID, String status, Job job, String interviewDate, LocalTime interviewTime, String location, int processOrNot, String selectedDate) {
        this.interviewID = interviewID;
        this.status = status;
        this.job = job;
        this.interviewDate =  interviewDate;
        this.interviewTime = interviewTime;
        this.location = location;
        this.processOrNot = processOrNot;
        this.selectedDate = selectedDate;
    }

    public JobInterview(int interviewID, String status, Job job, JobSeeker jobSeeker, String interviewDate, LocalTime interviewTime, JobApplication jobApplication, int processOrNot, String location) {
        this.interviewID = interviewID;
        this.status = status;
        this.job = job;
        this.jobApplication = jobApplication;
        this.jobSeeker = new JobSeeker(jobSeeker.getId(), jobSeeker.getName(),jobSeeker.getEmail(),jobSeeker.getVerification(), jobSeeker.getImageFile());
        this.interviewDate =  interviewDate;
        this.interviewTime = interviewTime;
        this.processOrNot  = processOrNot;
        this.location = location;
    }

    public int getInterviewID() {
        return interviewID;
    }

    public void setInterviewID(int interviewID) {
        this.interviewID = interviewID;
    }

    public int getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(int applicationID) {
        this.applicationID = applicationID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JobApplication getJobApplication() {
        return jobApplication;
    }

    public void setJobApplication(JobApplication jobApplication) {
        this.jobApplication = jobApplication;
    }

    public String getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(String interviewDate) {
        this.interviewDate = interviewDate;
    }

    public LocalTime getInterviewTime() {
        return interviewTime;
    }

    public void setInterviewTime(LocalTime interviewTime) {
        this.interviewTime = interviewTime;
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
    }

    public int getProcessOrNot() {
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
