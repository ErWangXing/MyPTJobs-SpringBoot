package com.MyPTJobs.Repository;

import com.MyPTJobs.Class.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobOfferRespository extends JpaRepository<JobOffer, Integer> {

    @Query(value = "SELECT a FROM JobOffer a  Left JOIN JobSeeker b on (a.jobSeekerID = b.id) WHERE a.jobID = ?2 AND b.token = ?1 AND a.status = 'Apply'")
    Optional<JobOffer> checkSelectedJob(String token, int jobID);

    @Query(value = "SELECT a FROM JobOffer a  WHERE a.offerID = ?1 ")
    Optional<JobOffer> checkExistingOffer(int offerID);

    @Query(value = "SELECT a FROM JobOffer a  WHERE  a.jobID = ?2 AND a.jobSeekerID = ?1 AND a.status = 'Pending'")
    Optional<JobOffer> checkExistingOfferByJobIDAndJobSeeker(int id, int jobID);

    @Modifying
    @Transactional
    @Query("DELETE FROM JobOffer u WHERE u.jobID = ?2 AND u.jobSeekerID = ?1 AND u.status = 'Pending'")
    int deleteOfferedJob(int id, int jobID);

    @Query(value = "SELECT new JobOffer(b.offerID, b.status, a, c, b.offerDate, b.jobSeekerID, c.blockPermanent ) FROM Job a Right Join JobOffer b on (a.id = b.jobID) Left Join JobSeeker c on (c.id = b.jobSeekerID) WHERE a.deleted_at = null and a.employerID = ?1 and  c.blockPermanent in (?2) AND a.endDate >= ?3 order by b.offerDate desc")
    List<JobOffer> getAllEmployerOfferedJob(int id, List<Integer> blockPermanent, String currentDate);


//    @Query(value = "SELECT new JobOffer(b.offerID, a, b.status, b.offerDate, CASE WHEN (e.blockDate IS NULL OR CURRENT_TIMESTAMP  > e.blockDate) AND e.blockPermanent = 0 THEN 0 ELSE 1 END ) FROM Job a RIGHT JOIN JobOffer b ON (a.id = b.jobID) LEFT JOIN Employer e ON (a.employerID = e.id) WHERE a.deleted_at IS NULL AND b.jobSeekerID = ?1 ORDER BY b.offerDate DESC")
    @Query(value = "SELECT new JobOffer(b.offerID, a, b.status, b.offerDate, e.blockPermanent, e) FROM Job a RIGHT JOIN JobOffer b ON (a.id = b.jobID) LEFT JOIN Employer e ON (a.employerID = e.id) WHERE a.deleted_at IS NULL AND b.jobSeekerID = ?1 And a.endDate > ?2 ORDER BY b.offerDate DESC")
    List<JobOffer> getAllJobSeekerOfferedJob(int id, String currentDate);

//    @Query(value = "SELECT new JobOffer(b.offerID, a, b.status, b.offerDate, e.blockPermanent, e) FROM Job a RIGHT JOIN JobOffer b ON (a.id = b.jobID) LEFT JOIN Employer e ON (a.employerID = e.id) WHERE a.deleted_at IS NULL AND b.jobSeekerID = ?1 AND a.jobDate >= ?2 ORDER BY b.offerDate DESC")
//    List<JobOffer> getAllJobSeekerOfferedJob(int id, String currentDate);


    @Modifying
    @Transactional
    @Query("Update JobOffer u set status = ?2 WHERE u.offerID = ?1")
    int updateOfferStatus(int id, String status);
}