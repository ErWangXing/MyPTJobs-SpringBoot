package com.MyPTJobs.Services;

import com.MyPTJobs.Class.JobSeeker;
import com.MyPTJobs.Class.Language;
import com.MyPTJobs.Repository.LanguageRepository;
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
public class LanguageService {
    private Path rootLocation;

    @Autowired
    private LanguageRepository langRepo;

//    private String uploadDir = "Images/JobSeeker/";


    public ResponseEntity<String> updateAddLanguage(Language skill, @RequestParam("mode") String mode) throws IOException {
        langRepo.save(skill);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    public List<Language> getListofLanguage(JobSeeker jobSeeker) throws IOException {
        List<Language> skillList = langRepo.findAllByJobSeeker(jobSeeker.getId());
        return skillList;
    }

    public Language getLanguage(int id) throws IOException {
        Optional<Language> skillList = langRepo.getSelectedLanguage(id);
        if ( skillList.isPresent() ){
            return skillList.get();
        }
        return null;
    }

    public void removeLanguage(int id) {
        langRepo.deleteLanguage(id);
    }


}