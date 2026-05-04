
package com.MyPTJobs.Class;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "complaint")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaintID", columnDefinition = "int(11) NOT NULL")
    private int complaintID;
    @Column(name = "jobSeekerID", columnDefinition = "int(11) default null")
    private int jobSeekerID;

    @Column(name = "jobID", columnDefinition = "int(11) default null")
    private int jobID;

    @Column(name = "reason", columnDefinition = "text default null")
    private String reason;

    @Column(name = "others", columnDefinition = "text default null")
    private String others;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kuala_Lumpur")
    @Column(name = "createdAt", columnDefinition = "datetime DEFAULT current_timestamp()")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "employerID", columnDefinition = "int(11) default null")
    private int employerID;

    @Column(name = "type", columnDefinition = "enum('Job','Employer', 'JobSeeker') DEFAULT NULL default null")
    private String type;

    @Column(name = "status", columnDefinition = "enum('Pending','Success', 'Failed') DEFAULT NULL default 'Pending'")
    private String status = "Pending";

    @Column(name = "grouping", columnDefinition = "int(11) default 0")
    private int grouping = 0;

    @Column(name = "action", columnDefinition = "Varchar(100) DEFAULT NULL ")
    private String action;

    @Transient
    private int id;


    public int getComplaintID() {
        return complaintID;
    }

    public void setComplaintID(int complaintID) {
        this.complaintID = complaintID;
    }

    public int getJobSeekerID() {
        return jobSeekerID;
    }

    public void setJobSeekerID(int jobSeekerID) {
        this.jobSeekerID = jobSeekerID;
    }

    public int getJobID() {
        return jobID;
    }

    public void setJobID(int jobID) {
        this.jobID = jobID;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
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

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGrouping() {
        return grouping;
    }

    public void setGrouping(int grouping) {
        this.grouping = grouping;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Complaint() {
    }

    public Complaint(String type, int id, Date createdAt) {
        this.type = type;
        this.id = id;
        this.createdAt = new Timestamp(createdAt.getTime());
    }

    public String getComplaintType(){
        if ( jobSeekerID > 0 ){
            return "jobSeeker";
        }
        if ( jobID > 0 ){
            return "job";
        }
        if ( employerID > 0 ){
            return "employer";
        }
        return "";
    }

    public String showAction(int number){
        if ( jobSeekerID > 0 ){
            switch (number) {
                case 0:
                    // send warning
                    return "Send Warning";
                case 1:
                default:
                    // block a day
                    return "This job seeker will be blocked for a day";
            }
        }
        if ( jobID > 0 ){
            switch (number) {
                case 0:
                    // send warning
                    return "Send Warning";
                case 1:
                default:
                    // block a day
                    return "This job seeker will be deleted";
            }
        }
        if ( employerID > 0 ){
            switch (number) {
                case 0:
                    // send warning
                    return "Send Warning";
                case 1:
                default:
                    // block a day
                    return "This employer will be blocked for a day";
            }
        }
        return "";
    }

    public Complaint(int complaintID, String reason, Date createdAt) {
        this.complaintID = complaintID;
        this.reason = reason;
        this.createdAt = new Timestamp(createdAt.getTime());
    }
}
