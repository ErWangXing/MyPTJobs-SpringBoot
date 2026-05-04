package com.MyPTJobs.Services;

import com.MyPTJobs.Class.JobApplication;
import com.MyPTJobs.Class.function;
import com.MyPTJobs.Repository.JobApplicationRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Component
public class JobApplicationService {
    private Path rootLocation;
    @Autowired
    private JobApplicationRespository respository;

    public boolean checkApplication(String token, int jobID){
        Optional<JobApplication> checkApplicationJob = respository.checkSelectedJob(token, jobID);
        return checkApplicationJob.isPresent();
    }

    public boolean checkExistingApplication( int applicationID){
        Optional<JobApplication> checkApplicationJob = respository.checkExistingApplication(applicationID);
        return checkApplicationJob.isPresent();
    }

    public Optional<JobApplication> getExistingApplication( int applicationID){
        Optional<JobApplication> checkApplicationJob = respository.checkExistingApplication(applicationID);
//        if ( checkApplicationJob.isPresent() ){
            return checkApplicationJob;
//        }
//        return new JobApplication();
    }

    public void addApplication(int id, int jobID, String selectedDate){
        JobApplication newApplication = new JobApplication(id, jobID, selectedDate);
        newApplication.setStatus("Apply");
        newApplication.setApplyDate(new Timestamp(System.currentTimeMillis()));
//        newApplication.setJobSeekerID(id);
//        newApplication.setJobID(jobID);
        respository.save(newApplication);
    }

    public void saveNew(JobApplication newApplication){
        respository.save(newApplication);
    }

    public void deleteApplication(int id, int jobID){
        respository.deleteAppliedJob(id, jobID);

    }

    public boolean updateApplicationStatus(int id, String status){
        if ( checkExistingApplication(id)){
            respository.updateApplicatonStatus(id, status);
            return true;
        }else{
            return false;
        }


    }
    public List<JobApplication> getJobSeekerAppliedJob(int value, int page, int limit){
        List<JobApplication> jobList = new ArrayList<JobApplication>();
        jobList = respository.getAllJobSeekerAppliedJob(value);
        return jobList;
    }

    public List<JobApplication> getEmployerAppliedJob(int value, int page, int limit){
        List<JobApplication> jobList = new ArrayList<JobApplication>();
        List<Integer> blockPermanent = new ArrayList<Integer>();
        blockPermanent.add(0);
        blockPermanent.add(1);
        jobList = respository.getAllEmployerAppliedJob(value, blockPermanent, new function().getCurrentDate());
        return jobList;
    }
    public int getNumberEmployerAppliedJob(int value, int page, int limit){
        int numberOfJobList = 0;
        List<Integer> blockPermanent = new ArrayList<Integer>();
        blockPermanent.add(0);
//        blockPermanent.add(1);
        numberOfJobList = respository.getNumberofAllEmployerAppliedJob(value, blockPermanent, new function().getCurrentDate());
        return numberOfJobList;
    }

    public int getNumberCompletedJobApplication(int value, int page, int limit, List<String> statuses){
        List<Integer> blockPermanent = new ArrayList<Integer>();
        blockPermanent.add(0);
//        blockPermanent.add(1);
        return respository.getNumberofCompletedJob(value, statuses, blockPermanent);
    }
}