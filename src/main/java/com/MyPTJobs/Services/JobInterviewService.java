package com.MyPTJobs.Services;

import com.MyPTJobs.Class.JobApplication;
import com.MyPTJobs.Class.JobInterview;
import com.MyPTJobs.Class.function;
import com.MyPTJobs.Repository.JobInterviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Component
public class JobInterviewService {
    private Path rootLocation;
    @Autowired
    private JobInterviewRepository respository;

    @Autowired
    private JobApplicationService jobApplicationService;

//    public boolean checkInterview(String token, int jobID){
//        Optional<JobInterview> checkInterviewJob = respository.checkSelectedJob(token, jobID);
//        if ( checkInterviewJob.isPresent() ){
//            return true;
//        }
//        return false;
//    }

    public boolean checkExistingInterview( int applicationID){
        Optional<JobInterview> checkInterviewJob = respository.checkExistingInterview(applicationID);
        return checkInterviewJob.isPresent();
    }

    public Optional<JobInterview>  checkExisting( int applicationID){
        Optional<JobInterview> checkInterviewJob = respository.checkExistingInterview(applicationID);
        return checkInterviewJob;
    }

    public Optional<JobApplication>  checkExistingJobApplication( int interviewID){
        Optional<JobApplication> checkInterviewJob = respository.checkExistingApplication(interviewID);
        return checkInterviewJob;
    }

    public Boolean addInterview(JobInterview jobInterview){
//        JobInterview newInterview = new JobInterview(applicationID);
        jobApplicationService.updateApplicationStatus(jobInterview.getApplicationID(), "Interview");
        respository.save(jobInterview);
        return true;
    }

    public void deleteInterview(int interviewID){
        respository.deleteInterview(interviewID);

    }
    public boolean updateEmployerInterviewStatus(int id, String status){
        Optional<JobInterview>  jobInterview = checkExisting(id);
        if ( jobInterview.isPresent()){
            if ( status.equals("Accepted") ){
                jobApplicationService.updateApplicationStatus(jobInterview.get().getApplicationID(), status);
                respository.updateInterviewStatus(id, "Accepted");
            }else{
                jobApplicationService.updateApplicationStatus(jobInterview.get().getApplicationID(), status);
                respository.updateInterviewStatus(id, "Rejected");
            }

            return true;
        }else{
            return false;
        }


    }
    public boolean updateInterviewStatus(int id, String status){
        Optional<JobInterview>  jobInterview = checkExisting(id);
        if ( jobInterview.isPresent()){
            if ( status.equals("Accepted") ){
                jobApplicationService.updateApplicationStatus(jobInterview.get().getApplicationID(), "User Accept");
                respository.updateInterviewStatus(id, "User Accept");
            }else{
                jobApplicationService.updateApplicationStatus(jobInterview.get().getApplicationID(), "User Reject");
                respository.updateInterviewStatus(id, "User Reject");
            }

            return true;
        }else{
            return false;
        }


    }
    public List<JobInterview> getJobSeekerInterview(int value, int page, int limit){
        List<JobInterview> jobList = new ArrayList<JobInterview>();
        jobList = respository.getAllJobSeekerInterview(value, new function().getCurrentDate());
        return jobList;
    }

    public List<JobInterview> getEmployerInterview(int value, int page, int limit){
        List<JobInterview> jobList = new ArrayList<JobInterview>();
        List<Integer> blockPermanent = new ArrayList<Integer>();
        blockPermanent.add(0);
        blockPermanent.add(1);
        jobList = respository.getAllEmployerInterview(value, blockPermanent, new function().getCurrentDate());
        return jobList;
    }

    public int getNumberEmployerInterview(int value, int page, int limit){
        int numberOfJobList = 0;
        List<Integer> blockPermanent = new ArrayList<Integer>();
        blockPermanent.add(0);
//        blockPermanent.add(1);
        numberOfJobList = respository.getNumberofAllEmployerInterview(value ,blockPermanent, new function().getCurrentDate());
        return numberOfJobList;
    }
}