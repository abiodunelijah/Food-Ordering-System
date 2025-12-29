package com.abiodunelijah.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/uploads")
public class TestAwsController {

    private final AwsS3Service awsS3Service;


    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("keyName") String keyName) {
        URL fileUrl = awsS3Service.uploadFile(keyName, file);
        return ResponseEntity.ok("File uploaded successfully. File URL: " + fileUrl.toString());
    }
}
