package com.MyPTJobs.Controllor;

import com.MyPTJobs.Class.FileStorageProperties;
import com.MyPTJobs.Class.FileStorageService;
import com.MyPTJobs.Repository.JobApplicationRespository;
import com.MyPTJobs.Repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Component
@RestController //
@RequestMapping(path = "/job")
public class JobController {
    private Path rootLocation;

    private final String uploadDir = "Images/Job/";
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobApplicationRespository jobApplicationRespository;


    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws IOException {
        FileStorageProperties saveDir = new FileStorageProperties();
        saveDir.setUploadDir(uploadDir);
        FileStorageService fileService = new FileStorageService(saveDir);
        Resource file = fileService.loadFileAsResource(filename);
        String type = Files.probeContentType(Path.of(uploadDir + filename));
//        System.out.println(type);
        // download file
//        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
//        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(file);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, type).body(file);
    }

}