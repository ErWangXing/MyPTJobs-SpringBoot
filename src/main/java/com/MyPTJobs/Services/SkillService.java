package com.MyPTJobs.Services;

import com.MyPTJobs.Class.JobSeeker;
import com.MyPTJobs.Class.Skill;
import com.MyPTJobs.Repository.SkillRepository;
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
public class SkillService {
    private Path rootLocation;

    @Autowired
    private SkillRepository skillRepo;

//    private String uploadDir = "Images/JobSeeker/";


    public ResponseEntity<String> updateAddSkill(Skill skill, @RequestParam("mode") String mode) throws IOException {
        skillRepo.save(skill);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    public List<Skill> getListofSkill(JobSeeker jobSeeker) throws IOException {
        List<Skill> skillList = skillRepo.findAllByJobSeeker(jobSeeker.getId());
        return skillList;
    }

    public Skill getSkill(int id) throws IOException {
        Optional<Skill> skillList = skillRepo.getSelectedExperience(id);
        if ( skillList.isPresent() ){
            return skillList.get();
        }
        return null;
    }

    public void removeSkill(int id) {
        skillRepo.deleteSkill(id);
    }

}