package com.MyPTJobs.Repository;

import com.MyPTJobs.Class.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    @Query("SELECT AVG(COALESCE(a.averageRating,0)) FROM Rating a WHERE a.jobSeekerID = ?1")
    double getJobSeekerAvgRating(int jobSeekerID);

    @Query("SELECT new Rating (a.rating, a.averageRating, a.createdAt, c) FROM Rating a Left Join JobApplication b on (a.applicationID = b.applicationID) Left Join Job c on (b.jobID = c.id) WHERE a.jobSeekerID = ?1 order by a.createdAt desc")
    List<Rating> getJobSeekerRating(int jobSeekerID);


    @Query("SELECT new Rating (a.rating, a.averageRating, a.createdAt, c) FROM Rating a Left Join JobApplication b on (a.applicationID = b.applicationID) Left Join JobSeeker c on (b.jobSeekerID = c.id) WHERE a.employerID = ?1 order by a.createdAt desc")
    List<Rating> getEmployerRating(int jobSeekerID);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query( value = "UPDATE jobseeker r\nINNER JOIN (\n  SELECT jobSeekerID, AVG(averageRating) AS avgRating\n  FROM rating\n  GROUP BY jobSeekerID\n) sub ON r.id = sub.jobSeekerID\nSET r.rating = sub.avgRating;", nativeQuery = true)
    void updateJobSeekerRating();

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query( value = "UPDATE employer r\nINNER JOIN (\n  SELECT employerID, AVG(averageRating) AS avgRating\n  FROM rating\n  GROUP BY employerID\n) sub ON r.employerID = sub.employerID\nSET r.rating = sub.avgRating;", nativeQuery = true)
    void updateEmployerRating();

}