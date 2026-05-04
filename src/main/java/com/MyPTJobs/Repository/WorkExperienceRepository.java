package com.MyPTJobs.Repository;

import com.MyPTJobs.Class.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Integer> {

    @Query("SELECT new WorkExperience(t.id, t.workExperienceTitle, t.workExperienceCompany, t.dateStartWork, t.dateEndWork) FROM WorkExperience t where t.jobSeekerID = ?1")
    List<WorkExperience> findAllByJobSeeker(int jobSeekerID);
    @Query("SELECT t FROM WorkExperience t where t.id = ?1")
    Optional<WorkExperience> getSelectedExperience(int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM WorkExperience t where t.id = ?1")
    void deleteWorkExperience( int jobID);
}