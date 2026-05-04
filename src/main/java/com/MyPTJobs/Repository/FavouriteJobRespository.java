package com.MyPTJobs.Repository;

import com.MyPTJobs.Class.FavouriteJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FavouriteJobRespository extends JpaRepository<FavouriteJob, Integer> {

    @Query(value = "SELECT a FROM FavouriteJob a  Left JOIN JobSeeker b on (a.jobSeekerID = b.id) WHERE a.jobID = ?2 AND b.token = ?1 ")
    Optional<FavouriteJob> checkSelectedJob(String token, int jobID);

    @Modifying
    @Transactional
    @Query("DELETE FROM FavouriteJob u WHERE u.jobID = ?2 AND u.jobSeekerID = ?1")
    int deleteFavouriteJob(int id, int jobID);
}