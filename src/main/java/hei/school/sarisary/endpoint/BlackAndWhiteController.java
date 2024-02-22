package sary.hei.school.endpoint.rest.controller.health;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sary.hei.school.file.BucketComponent;
import sary.hei.school.file.FileHash;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Optional;

import static sary.hei.school.file.FileHashAlgorithm.NONE;

@RestController
public class BlackAndWhiteController {
    private static final String IMAGE_KEY = "health/";
    BucketComponent bucketComponent;
    @GetMapping(value = "/image/{id}")
    public String getBlackAndWhiteImage(@PathVariable int id) {
        String fileSuffix=".png";
        int filePrefix=id;
        String bucketKey=IMAGE_KEY+filePrefix+fileSuffix;
        bucketComponent.download(bucketKey);
        return "downloaded successfully";

    }



    @PutMapping("/black/{id}")
    public ResponseEntity<String> uploadImage(@RequestBody MultipartFile image,@PathVariable String id) throws IOException {
        BufferedImage originalImage = ImageIO.read(image.getInputStream());
        BufferedImage blackAndWhiteImage = convertToBlackAndWhite(originalImage);
        String  formatName="png";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(blackAndWhiteImage, formatName, baos);
        ByteArrayResource ressource=new ByteArrayResource(baos.toByteArray());


        File templeFile=File.createTempFile(id,".png");

        String suffix=".png";
        String bucketKey=(IMAGE_KEY+id).toString();
        try (InputStream in = ressource.getInputStream();
             java.io.FileOutputStream out = new java.io.FileOutputStream(templeFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer,  0, bytesRead);
            }
        }
        File im=(File) image;
        String bucketKey=(IMAGE_KEY+id).toString();

        can_upload_directory(templeFile,bucketKey);
        return ResponseEntity.ok("OK");
    }
    private BufferedImage convertToBlackAndWhite(BufferedImage originalImage) {
        BufferedImage blackAndWhiteImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D graphics = blackAndWhiteImage.createGraphics();
        graphics.drawImage(originalImage,  0,  0, null);
        graphics.dispose();
        return blackAndWhiteImage;
    }
    private FileHash can_upload_directory(File toUpload, String bucketKey) {
        var hash = bucketComponent.upload(toUpload, bucketKey);
        if (!NONE.equals(hash.algorithm())) {
            throw new RuntimeException("FileHashAlgorithm.NONE expected but got: " + hash.algorithm());
        }
        return hash;
    }

    private URL can_presign(String fileBucketKey) {
        return bucketComponent.presign(fileBucketKey, Duration.ofMinutes(2));

    }



}