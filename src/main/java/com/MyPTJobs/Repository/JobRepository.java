package com.MyPTJobs.Repository;

import com.MyPTJobs.Class.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {
    List<Job> findByTitleContaining(String title);

    @Query(value = "SELECT new Job(a.jobStatus, a.id, a.title , a.description, a.salaryPerHours, a.latitude, a.longitude, a.date, a.startTime, a.endTime, a.location, a.employerID, a.image, a.endDate) FROM Job a LEFT Join Employer b on (a.employerID = b.id) WHERE b.token = ?1 and a.deleted_at = null order by a.id desc")
    List<Job> getPostedJobList(String token);
    @Query(value = "SELECT new Job(a.jobStatus, a.id, a.title , a.description, a.salaryPerHours, a.latitude, a.longitude, a.date, a.startTime, a.endTime, a.location, a.employerID, a.image, a.endDate) FROM Job a  WHERE a.title LikE %?1% and a.deleted_at = null order by a.id desc")
    List<Job> getAllPostedJob(String value);

    @Query(value = "SELECT new Job(a.id, a.title , a.description, a.salaryPerHours, a.latitude, a.longitude, a.date, a.startTime, a.endTime, a.location, a.employerID, a.image, a.endDate) FROM Job a Right Join FavouriteJob b on (a.id = b.jobID)  WHERE a.deleted_at = null and b.jobSeekerID = ?1 AND a.jobStatus = 'Active' order by a.id desc")
    List<Job> getAllFavouriteJob(int id);

    @Query(value = "SELECT COALESCE(b.notificationToken,'') FROM Job a LEFT Join Employer b on (a.employerID = b.id) WHERE a.id = ?1")
    String checkNotificationToken(int jobID);

    @Query(value = "SELECT a FROM Job a  WHERE a.id = ?1 ")
    Optional< Job > getSelectedJob(int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Job t set t.image = ?1 where t.id = ?2")
    void updateImage(String image, int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Job t set t.title = ?1, t.description = ?2, t.salaryPerHours = ?3, t.latitude = ?4,t.longitude = ?5,t.date = ?6, t.startTime = ?7, t.endTime = ?8, t.location = ?9, t.jobType = ?11, t.area = ?12, t.endDate = ?13 where t.id = ?10")
    void updateJob(String title, String description, double salaryPerHours,  String latitude, String longitude, String date, LocalTime startTime, LocalTime endTime, String location, int id, String jobType, String area, String endDate);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Job t set t.deleted_at = Now() where t.id = ?1")
    void deleteJob( int jobID);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Job t set t.jobStatus = ?2 where t.id = ?1")
    void updateJobStatus( int jobID, String status);

}