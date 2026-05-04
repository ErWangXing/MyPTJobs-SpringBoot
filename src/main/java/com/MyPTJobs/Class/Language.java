package com.MyPTJobs.Class;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "language")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int(11) NOT NULL")
    private int id;
    @Column(name = "language", columnDefinition = "varchar(255) default null")
    private String language;
    @Column(name = "rate", columnDefinition = "double default null")
    private double rate;


//    @OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "jobSeekerID")
//    private JobSeeker jobSeeker;
@Column(name = "jobSeekerID", columnDefinition = "int(11) NOT NULL")
private int jobSeekerID;

    public Language(int id, String language, double rate) {
        this.id = id;
        this.language = language;
        this.rate = rate;
    }

    public Language(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getJobSeekerID() {
        return jobSeekerID;
    }

    public void setJobSeekerID(int jobSeekerID) {
        this.jobSeekerID = jobSeekerID;
    }
}
