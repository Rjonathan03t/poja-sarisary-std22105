package hei.school.sarisary.endpoint.rest.controller.health;

import hei.school.sarisary.file.BucketComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@RestController
public class BlackAndWhiteController {

    @Autowired
    private BucketComponent bucketComponent;

    @PutMapping("/black-and-white/{id}")
    public ResponseEntity<Void> processImage(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        File convertedFile = convertMultipartFileToFile(file);
                bucketComponent.upload(convertedFile, id);
        return ResponseEntity.ok().build();
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) {
        try {
            File file = File.createTempFile("temp", null);
            multipartFile.transferTo(file);
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la conversion de MultipartFile en File", e);
        }
    }
}