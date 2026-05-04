package com.MyPTJobs.Repository;

import com.MyPTJobs.Class.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {

    @Query("SELECT new Language(t.id, t.language, t.rate) FROM Language t where t.jobSeekerID = ?1")
    List<Language> findAllByJobSeeker(int jobSeekerID);
    @Query("SELECT t FROM Language t where t.id = ?1")
    Optional<Language> getSelectedLanguage(int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM Language t where t.id = ?1")
    void deleteLanguage( int jobID);
}