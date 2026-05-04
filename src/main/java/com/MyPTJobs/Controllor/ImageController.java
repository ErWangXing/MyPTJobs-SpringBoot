package com.MyPTJobs.Controllor;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/resources")
public class ImageController {

    @GetMapping("/{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) throws IOException {
        ClassPathResource imgFile = new ClassPathResource("static/" + imageName);

        // Check if the image file exists
        if (imgFile.exists()) {
            byte[] imageBytes = Files.readAllBytes(Path.of(imgFile.getURI()));

            // Create an HTTP response with the image content
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG) // Adjust the media type as per your image type
                    .body(imageBytes);
        } else {
            // Image file not found
            return ResponseEntity.notFound().build();
        }
    }
}
