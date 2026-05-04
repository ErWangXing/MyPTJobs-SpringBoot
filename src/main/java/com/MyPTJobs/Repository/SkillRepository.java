package com.MyPTJobs.Repository;

import com.MyPTJobs.Class.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {

    @Query("SELECT new Skill(t.id, t.skill, t.rate) FROM Skill t where t.jobSeekerID = ?1")
    List<Skill> findAllByJobSeeker(int jobSeekerID);
    @Query("SELECT t FROM Skill t where t.id = ?1")
    Optional<Skill> getSelectedExperience(int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM Skill t where t.id = ?1")
    void deleteSkill( int jobID);
}