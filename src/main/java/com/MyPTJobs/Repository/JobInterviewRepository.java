package com.MyPTJobs.Repository;

import com.MyPTJobs.Class.JobApplication;
import com.MyPTJobs.Class.JobInterview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobInterviewRepository extends JpaRepository<JobInterview, Integer> {

//    @Query(value = "SELECT a FROM JobInterview a  Left JOIN JobSeeker b on (a.jobSeekerID = b.id)  WHERE a.jobID = ?2 AND b.token = ?1 AND a.status = 'Apply'")
//    Optional<JobInterview> checkSelectedJob(String token, int jobID);

    @Query(value = "SELECT a FROM JobInterview a WHERE a.interviewID = ?1 ")
    Optional<JobInterview> checkExistingInterview(int applicationID);

    @Query(value = "SELECT b FROM JobInterview a LEFT JOIN JobApplication b on (a.applicationID = b.applicationID) WHERE a.interviewID = ?1 ")
    Optional<JobApplication> checkExistingApplication(int applicationID);

    @Modifying
    @Transactional
    @Query("DELETE FROM JobInterview u WHERE u.interviewID = ?1")
    int deleteInterview(int id);


    @Query(value = "SELECT new JobInterview(a.interviewID, a.status, d, c, a.interviewDate, a.interviewTime, b, c.blockPermanent, a.location) FROM JobInterview a LEFT JOIN JobApplication b on (a.applicationID = b.applicationID) LEFT JOIN JobSeeker c on (c.id = b.jobSeekerID) LEFT JOIN Job d on (d.id = b.jobID) WHERE d.deleted_at = null and d.employerID = ?1 and  c.blockPermanent in (?2) AND d.endDate >= ?3 AND a.interviewDate >= ?3 order by a.interviewDate desc, a.interviewTime desc")
    List<JobInterview> getAllEmployerInterview(int id, List<Integer> blockPermanent, String currentDate);

    @Query(value = "SELECT count(a.interviewID) FROM JobInterview a LEFT JOIN JobApplication b on (a.applicationID = b.applicationID) LEFT JOIN JobSeeker c on (c.id = b.jobSeekerID) LEFT JOIN Job d on (d.id = b.jobID) WHERE d.deleted_at = null and d.employerID = ?1 and  c.blockPermanent in (?2) AND a.status = 'Pending' AND d.endDate >= ?3 AND a.interviewDate >= ?3 order by a.interviewDate desc, a.interviewTime desc")
    int getNumberofAllEmployerInterview(int id, List<Integer> blockPermanent, String currentDate);
//    @Query(value = "SELECT new JobInterview(a.interviewID, a.status, d, a.interviewDate, a.interviewTime, a.location, CASE WHEN (e.blockDate IS NULL OR CURRENT_TIMESTAMP  > e.blockDate) AND e.blockPermanent = 0 THEN 0 ELSE 1 END ) FROM JobInterview a LEFT JOIN JobApplication b on (a.applicationID = b.applicationID) LEFT JOIN Job d on (d.id = b.jobID) LEFT JOIN Employer e on (d.employerID = e.id) WHERE d.deleted_at = null and b.jobSeekerID = ?1 order by a.interviewDate desc, a.interviewTime desc")
    @Query(value = "SELECT new JobInterview(a.interviewID, a.status, d, a.interviewDate, a.interviewTime, a.location, e.blockPermanent, b.selectedDate) FROM JobInterview a LEFT JOIN JobApplication b on (a.applicationID = b.applicationID) LEFT JOIN Job d on (d.id = b.jobID) LEFT JOIN Employer e on (d.employerID = e.id) WHERE d.deleted_at = null and b.jobSeekerID = ?1 AND a.interviewDate >= ?2 AND d.endDate >= ?2 order by a.interviewDate desc, a.interviewTime desc")
    List<JobInterview> getAllJobSeekerInterview(int id, String currentDate);

    @Modifying
    @Transactional
    @Query("Update JobInterview u set status = ?2 WHERE u.interviewID = ?1")
    int updateInterviewStatus(int id, String status);
}