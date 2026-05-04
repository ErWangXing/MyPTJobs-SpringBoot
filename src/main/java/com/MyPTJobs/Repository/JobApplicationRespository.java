package com.MyPTJobs.Repository;

import com.MyPTJobs.Class.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRespository extends JpaRepository<JobApplication, Integer> {

    @Query(value = "SELECT a FROM JobApplication a  Left JOIN JobSeeker b on (a.jobSeekerID = b.id) WHERE a.jobID = ?2 AND b.token = ?1 AND a.status NOT IN ('Offer Accepted', 'Done','Rejected','User Reject')")
    Optional<JobApplication> checkSelectedJob(String token, int jobID);

    @Query(value = "SELECT a FROM JobApplication a  WHERE a.applicationID = ?1 ")
    Optional<JobApplication> checkExistingApplication(int applicationID);

    @Modifying
    @Transactional
    @Query("DELETE FROM JobApplication u WHERE u.jobID = ?2 AND u.jobSeekerID = ?1")
    int deleteAppliedJob(int id, int jobID);


    @Query(value = "SELECT new JobApplication(b.applicationID, b.status, a, c, b.applyDate, b.jobSeekerID, e.location, c.blockPermanent, b.selectedDate) FROM Job a Right Join JobApplication b on (a.id = b.jobID) Left Join JobSeeker c on (c.id = b.jobSeekerID) LEFT JOIN JobInterview d ON (b.applicationID = d.applicationID)  Left Join Employer e on (a.employerID = e.id) WHERE a.deleted_at = null and a.employerID = ?1 and  c.blockPermanent in (?2) order by b.applyDate desc")
    List<JobApplication> getAllEmployerAppliedJob(int id, List<Integer> blockPermanent, String currentDate);
    @Query(value = "SELECT count(b.applicationID) FROM Job a Right Join JobApplication b on (a.id = b.jobID) Left Join JobSeeker c on (c.id = b.jobSeekerID) LEFT JOIN JobInterview d ON (b.applicationID = d.applicationID)  Left Join Employer e on (a.employerID = e.id) WHERE a.deleted_at = null and a.employerID = ?1 and  c.blockPermanent in (?2) AND b.status = 'Apply' order by b.applyDate desc")
    int getNumberofAllEmployerAppliedJob(int id, List<Integer> blockPermanent, String currentDate);

    @Query(value = "SELECT count(b.applicationID)  FROM Job a Right Join JobApplication b on (a.id = b.jobID) Left Join JobSeeker c on (c.id = b.jobSeekerID) LEFT JOIN JobInterview d ON (b.applicationID = d.applicationID) WHERE a.deleted_at = null and a.employerID = ?1 and b.status IN (?2) and  c.blockPermanent in (?2) order by b.applyDate desc")
    int getNumberofCompletedJob(int id, List<String> statuses, List<Integer> blockPermanent);


//    @Query(value = "SELECT new JobApplication(b.applicationID, a, b.status, b.applyDate, CASE WHEN (e.blockDate IS NULL OR CURRENT_TIMESTAMP  > e.blockDate) AND e.blockPermanent = 0 THEN 0 ELSE 1 END ) FROM Job a Right Join JobApplication b on (a.id = b.jobID) LEFT JOIN Employer e on (a.employerID = e.id)  WHERE a.deleted_at = null and b.jobSeekerID = ?1  order by b.applyDate desc")
    @Query(value = "SELECT new JobApplication(b.applicationID, a, b.status, b.applyDate, e.blockPermanent, b.selectedDate) FROM Job a Right Join JobApplication b on (a.id = b.jobID) LEFT JOIN Employer e on (a.employerID = e.id)  WHERE a.deleted_at = null and b.jobSeekerID = ?1  order by b.applyDate desc")
    List<JobApplication> getAllJobSeekerAppliedJob(int id);

    @Modifying
    @Transactional
    @Query("Update JobApplication u set status = ?2 WHERE u.applicationID = ?1")
    int updateApplicatonStatus(int id, String status);
}