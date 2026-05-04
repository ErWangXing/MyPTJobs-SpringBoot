package com.MyPTJobs.Services;

import com.MyPTJobs.Class.Rating;
import com.MyPTJobs.Repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
@Component
public class RatingService {
    private Path rootLocation;
    @Autowired
    private RatingRepository respository;

    public void save(Rating newRate){
        respository.save(newRate);
    }

    public double calcJobSeekerAverageRating(int jobSeekerID){
        return respository.getJobSeekerAvgRating(jobSeekerID);
    }

    public void calcTotalJobSeekerAverageRating(){
        respository.updateJobSeekerRating();
    }

    public void calcTotalEmployerAverageRating(){
        respository.updateEmployerRating();
    }

    public List<Rating> getRatingList(int id, String type){
        if ( type.equals("Job Seeker") ){
            return respository.getJobSeekerRating(id);
        }else{
            return respository.getEmployerRating(id);

        }

    }


}