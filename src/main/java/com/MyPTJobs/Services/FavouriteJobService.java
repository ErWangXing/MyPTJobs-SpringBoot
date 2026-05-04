package com.MyPTJobs.Services;

import com.MyPTJobs.Class.FavouriteJob;
import com.MyPTJobs.Repository.FavouriteJobRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Optional;

@Service
@Component
public class FavouriteJobService {
    private Path rootLocation;
    @Autowired
    private FavouriteJobRespository respository;

    public boolean checkFavourite(String token, int jobID){
        Optional<FavouriteJob> checkFavouriteJob = respository.checkSelectedJob(token, jobID);
        return checkFavouriteJob.isPresent();
    }

    public void addFavourite(int id, int jobID){
        FavouriteJob newFavourite = new FavouriteJob();
        newFavourite.setJobSeekerID(id);
        newFavourite.setJobID(jobID);
        respository.save(newFavourite);

    }

    public void deleteFavourite(int id, int jobID){
        respository.deleteFavouriteJob(id, jobID);

    }





}