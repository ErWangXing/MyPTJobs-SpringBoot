package com.MyPTJobs.Class;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
@Service
public class FileStorageService {

    private final Path fileStorageLocation;
//    @Autowired
public FileStorageService(FileStorageProperties fileStorageProperties) {
    this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
            .toAbsolutePath().normalize();

    try {
        Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
        throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
    }
}

    public String storeFile(MultipartFile file, String newFilename) {
        // Normalize file name
//        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileName = newFilename+"."+file.getOriginalFilename().split("\\.(?=[^\\.]+$)")[1];

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            if (!Files.exists(fileStorageLocation)){
                Files.createDirectories(fileStorageLocation);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
    public String saveFileFromURL(String url, String newFilename) {
        // Normalize file name
        String fileName = newFilename + ".png";
        try(InputStream in = new URL(url).openStream()){
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(in, targetLocation);
        } catch (MalformedURLException e) {
            return "";
//            throw new RuntimeException(e);
        } catch (IOException e) {
            return "";
//            throw new RuntimeException(e);
        }

       return fileName;
    }
//    public void deleteFile(String fileName) throws IOException
//    {
//        Path fileToDeletePath =  this.fileStorageLocation.resolve(fileName);
//        Files.delete(fileToDeletePath);
//    }
}