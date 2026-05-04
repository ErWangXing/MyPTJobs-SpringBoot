package com.MyPTJobs.Services;

import com.MyPTJobs.Class.FileStorageProperties;
import com.MyPTJobs.Class.FileStorageService;
import com.MyPTJobs.Class.Job;
import com.MyPTJobs.Repository.JobApplicationRespository;
import com.MyPTJobs.Repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Component
@RestController //
@RequestMapping(path="/job")
public class JobService {
    private Path rootLocation;

    private final String uploadDir = "Images/Job/";
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobApplicationRespository jobApplicationRespository;

//    private String uploadDir = "Images/JobSeeker/";


//    public void addNewJob(Job job){
//        JobRepository.save(job);
//    }

    public List<Job> getJobList(String token){
        List<Job> jobList = new ArrayList<Job>();
        jobList = jobRepository.getPostedJobList(token);
        return jobList;
    }

    public List<Job> getAllJobList(String value, int page, int limit){
        List<Job> jobList = new ArrayList<Job>();
        jobList = jobRepository.getAllPostedJob(value);
        return jobList;
    }

    public List<Job> getFavouriteJob(int value, int page, int limit){
        List<Job> jobList = new ArrayList<Job>();
        jobList = jobRepository.getAllFavouriteJob(value);
        return jobList;
    }

    public int getNumberJob(String token){
        List<Job> jobList = new ArrayList<Job>();
        jobList = jobRepository.getPostedJobList(token);
        return jobList.size();
    }




    public ResponseEntity<String> addJob(Job job, Optional<MultipartFile> file) throws IOException {
        MultipartFile _file = file.orElse(null);
        job.setCreated_at(new Timestamp(System.currentTimeMillis()));
        job.setDeleted_at(null);
        job.setJobStatus("Active");
        job = jobRepository.save(job);
        jobRepository.flush();

        String fileName = Integer.toString(job.getId())+"-"+(System.currentTimeMillis() / 1000L);
        job.setCreated_at(new Timestamp(System.currentTimeMillis()));
        try{
            if ( _file != null ){
                rootLocation = Paths.get(uploadDir);
                if (!Files.exists(rootLocation)){
                    Files.createDirectories(rootLocation);
                }
                FileStorageProperties saveDir = new FileStorageProperties();
                saveDir.setUploadDir(uploadDir);
                FileStorageService fileService = new FileStorageService(saveDir);
                fileName = fileService.storeFile(_file, fileName);
                job.setImage(fileName);
                jobRepository.updateImage(fileName, job.getId());
            }else {

            }

            return new ResponseEntity<>("success", HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    public Boolean updateJob(Job job, Optional<MultipartFile> file) throws IOException {
        MultipartFile _file = file.orElse(null);
//        job.setCreated_at(new Timestamp(System.currentTimeMillis()));
        jobRepository.updateJob(job.getTitle(),job.getDescription(), job.getSalaryPerHours(), job.getLatitude(),job.getLongitude(),job.getDate(), job.getStartTime(),job.getEndTime(),job.getLocation(), job.getId(), job.getJobType(), job.getArea(), job.getEndDate());
//        jobRepository.flush();

        String fileName = Integer.toString(job.getId())+"-"+(System.currentTimeMillis() / 1000L);
        job.setCreated_at(new Timestamp(System.currentTimeMillis()));
        try{
            if ( _file != null ){
                rootLocation = Paths.get(uploadDir);
                if (!Files.exists(rootLocation)){
                    Files.createDirectories(rootLocation);
                }
                FileStorageProperties saveDir = new FileStorageProperties();
                saveDir.setUploadDir(uploadDir);
                FileStorageService fileService = new FileStorageService(saveDir);
                fileName = fileService.storeFile(_file, fileName);
                job.setImage(fileName);
                jobRepository.updateImage(fileName, job.getId());
            }else {

            }

            return true;
        }catch(Exception e){
            System.out.println(e);
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
        return false;
    }

    public Job getSelectedJob(int id) throws IOException {
            Job returnJob = new Job();
        Optional < Job > optional = jobRepository.getSelectedJob(id);
        if (optional.isPresent()) {
//            System.out.println(optional.get());
            returnJob = optional.get();
        } else {
//            System.out.printf("No employee found with id %d%n", id2);
        }
            return returnJob;

    }

    public Boolean deleteJob(int id) throws IOException {
        jobRepository.deleteJob(id);
        return true;

    }

    public Boolean updateJobStatus(int id, String status) throws IOException {
        jobRepository.updateJobStatus(id, status);
        return true;

    }

}