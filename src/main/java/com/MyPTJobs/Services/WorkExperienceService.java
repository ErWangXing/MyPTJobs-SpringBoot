package com.MyPTJobs.Services;

import com.MyPTJobs.Class.JobSeeker;
import com.MyPTJobs.Class.WorkExperience;
import com.MyPTJobs.Repository.WorkExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
@Component
@RestController //
public class WorkExperienceService {
    private Path rootLocation;

    @Autowired
    private WorkExperienceRepository workExperienceRepo;

//    private String uploadDir = "Images/JobSeeker/";


    public ResponseEntity<String> updateAddWorkingExperience(WorkExperience workExperience,  @RequestParam("mode") String mode) throws IOException {
        workExperienceRepo.save(workExperience);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    public List<WorkExperience> getListofWorkingExperience(JobSeeker jobSeeker) throws IOException {
        List<WorkExperience> workExperienceList = workExperienceRepo.findAllByJobSeeker(jobSeeker.getId());
        return workExperienceList;
    }

    public WorkExperience getWorkingExperience(int id) throws IOException {
        Optional<WorkExperience> workExperience = workExperienceRepo.getSelectedExperience(id);
        if ( workExperience.isPresent() ){
            return workExperience.get();
        }
        return null;
    }


    public void removeWorkExperience(int id) {
        workExperienceRepo.deleteWorkExperience(id);
    }
}