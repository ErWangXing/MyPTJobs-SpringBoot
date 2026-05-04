package com.MyPTJobs.Class;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "skill")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int(11) NOT NULL")
    private int id;
    @Column(name = "skill", columnDefinition = "varchar(255) default null")
    private String skill;
    @Column(name = "rate", columnDefinition = "double default null")
    private double rate;


//    @OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "jobSeekerID")
//    private JobSeeker jobSeeker;
@Column(name = "jobSeekerID", columnDefinition = "int(11) NOT NULL")
private int jobSeekerID;

    public Skill(int id, String skill, double rate) {
        this.id = id;
        this.skill = skill;
        this.rate = rate;
    }

    public Skill(String skill, double rate, int id) {
        this.id = id;
        this.skill = skill;
        this.rate = rate;
    }

    public Skill(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
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
