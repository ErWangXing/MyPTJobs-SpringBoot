package com.MyPTJobs.Repository;

import com.MyPTJobs.Class.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Integer> {

    @Query("SELECT Count(t) FROM Complaint t where t.status = 'pending'")
    int getNumberComplaint();

    @Override
    Optional<Complaint> findById(Integer integer);

    @Query("SELECT new Complaint(c.type, CASE " +
            "   WHEN c.type = 'Job' THEN c.jobID " +
            "   WHEN c.type = 'Employer' THEN c.employerID " +
            "   WHEN c.type = 'JobSeeker' THEN c.jobSeekerID " +
            "   END , MAX(c.createdAt))  " +
            "FROM Complaint c " +
            "WHERE c.status = 'Pending' " +
            "GROUP BY " +
            "CASE " +
            "  WHEN c.type = 'Job' THEN c.jobID " +
            "  WHEN c.type = 'Employer' THEN c.employerID " +
            "  WHEN c.type = 'JobSeeker' THEN c.jobSeekerID " +
            "END ,c.type")

    List<Complaint> getUnprocessComplaints();

    @Query("SELECT c FROM Complaint c WHERE c.status = 'Pending' AND (( c.employerID = ?1 AND c.type = ?2 ) OR ( c.jobID = ?1 AND c.type = ?2 ) OR ( c.jobSeekerID = ?1 AND c.type = ?2 ))" )
    List<Complaint> getComplaintsByIDandType(int id, String type);

    @Query("SELECT c FROM Complaint c WHERE c.status = 'Pending' AND (c.complaintID in (?1))" )
    List<Complaint> getManyCompaints(List<Integer> ids);

    @Query("SELECT c from Complaint c where c.status = 'Success' AND c.jobID = ?1 AND c.grouping != 0 AND c.type='Job' GROUP BY c.grouping, c order by c.createdAt desc")
    List<Complaint> getAllComplaintofJob(int jobID);
    @Query("SELECT c from Complaint c where c.status = 'Success' AND c.employerID = ?1 AND c.grouping != 0  AND c.type='Employer' GROUP BY c.grouping,  c  order by c.createdAt desc")
    List<Complaint> getAllComplaintofEmployer(int employerID);
    @Query("SELECT c from Complaint c where c.status = 'Success' AND c.jobSeekerID = ?1 AND c.grouping != 0 AND c.type='JobSeeker' GROUP BY c.grouping , c order by c.createdAt desc")
    List<Complaint> getAllComplaintofJobSeeker(int jobSeekerID);



    @Modifying(clearAutomatically = true)
    @Transactional
//    @Query("Update Complaint set status = ?2, action = ?3 where complaintID IN (?1) ")
    @Query("UPDATE Complaint a " +
            "SET a.status = ?2, a.action = ?3, a.grouping = ?4 WHERE a.complaintID IN (?1) ")
    void cancelComplaint( List<Integer> ids, String status, String action, int grouping);


}