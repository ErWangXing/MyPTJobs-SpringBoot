package com.MyPTJobs.Class;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "workExperience")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkExperience  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int(11) NOT NULL")
    private int id;
    @Column(name = "workExperienceTitle", columnDefinition = "varchar(255) default null")
    private String workExperienceTitle;
    @Column(name = "workExperienceCompany", columnDefinition = "varchar(255) default null")
    private String workExperienceCompany;
    @DateTimeFormat(pattern = "yyyy-MM")
    @JsonFormat(pattern = "yyyy-MM")
    @Column(name = "dateStartWork", columnDefinition = "datetime default null")
    private Date dateStartWork;

    @DateTimeFormat(pattern = "yyyy-MM")
    @JsonFormat(pattern = "yyyy-MM")
    @Column(name = "dateEndWork", columnDefinition = "datetime default null")
    private Date dateEndWork;

//    @OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "jobSeekerID")
//    private JobSeeker jobSeeker;
@Column(name = "jobSeekerID", columnDefinition = "int(11) NOT NULL")
private int jobSeekerID;

    public WorkExperience(int id, String workExperienceTitle, String workExperienceCompany, Date dateStartWork, Date dateEndWork, int jobSeekerID) {
        this.id = id;
        this.workExperienceTitle = workExperienceTitle;
        this.workExperienceCompany = workExperienceCompany;
        this.dateStartWork = dateStartWork;
        this.dateEndWork = dateEndWork;
        this.jobSeekerID = jobSeekerID;
    }

    public WorkExperience(int id, String workExperienceTitle, String workExperienceCompany, Date dateStartWork, Date dateEndWork) {
        this.id = id;
        this.workExperienceTitle = workExperienceTitle;
        this.workExperienceCompany = workExperienceCompany;
        this.dateStartWork = dateStartWork;
        this.dateEndWork = dateEndWork;
    }
    public WorkExperience(int id) {
        this.id = id;

    }
    public WorkExperience(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorkExperienceTitle() {
        return workExperienceTitle;
    }

    public void setWorkExperienceTitle(String workExperienceTitle) {
        this.workExperienceTitle = workExperienceTitle;
    }

    public String getWorkExperienceCompany() {
        return workExperienceCompany;
    }

    public void setWorkExperienceCompany(String workExperienceCompany) {
        this.workExperienceCompany = workExperienceCompany;
    }

    public Date getDateStartWork() {
        return dateStartWork;
    }

    public void setDateStartWork(Date dateStartWork) {
        this.dateStartWork = dateStartWork;
    }

    public Date getDateEndWork() {
        return dateEndWork;
    }

    public void setDateEndWork(Date dateEndWork) {
        this.dateEndWork = dateEndWork;
    }

    public int getJobSeeker() {
        return jobSeekerID;
    }

    public void setJobSeeker(int jobSeekerID) {
        this.jobSeekerID = jobSeekerID;
    }
}
