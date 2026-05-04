package com.MyPTJobs.Services;

import com.MyPTJobs.Class.JobOffer;
import com.MyPTJobs.Class.function;
import com.MyPTJobs.Repository.JobOfferRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Component
public class JobOfferService {
    private Path rootLocation;
    @Autowired
    private JobOfferRespository respository;

    public boolean checkOffer(String token, int jobID){
        Optional<JobOffer> checkOfferJob = respository.checkSelectedJob(token, jobID);
        return checkOfferJob.isPresent();
    }

    public boolean checkExistingOffer( int offerID){
        Optional<JobOffer> checkOfferJob = respository.checkExistingOffer(offerID);
        return checkOfferJob.isPresent();
    }

    public boolean checkExistingOfferByJobIDAndJobSeeker(  int jobSeekerID, int jobID){
        Optional<JobOffer> checkOfferJob = respository.checkExistingOfferByJobIDAndJobSeeker(jobSeekerID, jobID);
        return checkOfferJob.isPresent();
    }

    public Optional<JobOffer> getExistingOffer( int offerID){
        Optional<JobOffer> checkOfferJob = respository.checkExistingOffer(offerID);
//        if ( checkOfferJob.isPresent() ){
            return checkOfferJob;
//        }
//        return new JobOffer();
    }

    public boolean addOffer(JobOffer newOffer){
//        newOffer.setJobSeekerID(id);
//        newOffer.setJobID(jobID);
        respository.save(newOffer);
        return true;
    }

    public boolean deleteOffer(int id, int jobID){
        if ( checkExistingOfferByJobIDAndJobSeeker(id, jobID) ){
            respository.deleteOfferedJob(id, jobID);
            return true;
        }
        return false;
    }

    public boolean updateOfferStatus(int id, String status){
        if ( checkExistingOffer(id)){
            respository.updateOfferStatus(id, status);
            return true;
        }else{
            return false;
        }


    }
    public List<JobOffer> getJobSeekerOffer(int value, int page, int limit){
        List<JobOffer> jobList = new ArrayList<JobOffer>();
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Format the current date to match the format of the jobDate field
        String currentDateFormatted = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        jobList = respository.getAllJobSeekerOfferedJob(value, new function().getCurrentDate());
        return jobList;
    }

    public List<JobOffer> getEmployerOffer(int value, int page, int limit){
        List<JobOffer> jobList = new ArrayList<JobOffer>();
        List<Integer> blockPermanent = new ArrayList<Integer>();
        blockPermanent.add(0);
        blockPermanent.add(1);

        jobList = respository.getAllEmployerOfferedJob(value, blockPermanent, new function().getCurrentDate());
        return jobList;
    }

    public int getNumberEmployerOffer(int value, int page, int limit){
        List<JobOffer> jobList = new ArrayList<JobOffer>();
        List<Integer> blockPermanent = new ArrayList<Integer>();
        blockPermanent.add(0);
//        blockPermanent.add(1);
        // Get the current date


        jobList = respository.getAllEmployerOfferedJob(value, blockPermanent, new function().getCurrentDate());
        return jobList.size();
    }
}