
package com.MyPTJobs.Class;

        import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

        import javax.persistence.*;

@Entity
@Table(name = "favouriteJob")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class FavouriteJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int(11) NOT NULL")
    private int id;
    @Column(name = "jobID", columnDefinition = "int(11) default null")
    private int jobID;
    @Column(name = "jobSeekerID", columnDefinition = "int(11) default null")
    private int jobSeekerID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
