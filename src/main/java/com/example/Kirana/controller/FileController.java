package com.example.Kirana.controller;

import com.example.Kirana.model.ImageFile;
import com.example.Kirana.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload-bill")
    public ResponseEntity<List<String>> uploadFile(@RequestParam("images") List<MultipartFile> files) {
        List<String> savedFileNames = fileService.saveFiles(files);
        if (savedFileNames != null) {
            return new ResponseEntity<>(savedFileNames, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // @GetMapping("/my-bill/{billId}")
    // public ResponseEntity<?> getBill(@PathVariable Integer billId) {
    //     ImageFile bill = fileService.getBill(billId);
    //     byte[] imageData = fileService.getBillData(bill);
    //     return ResponseEntity.status(HttpStatus.OK)
    //             .contentType(MediaType.valueOf(bill.getFileType()))
    //             .body(imageData);
    // }

    @GetMapping("/my-bill/{fileName}")
    public ResponseEntity<Resource> getBill(@PathVariable String fileName) {
        Optional<Resource> resourceOptional = fileService.getBill(fileName);

        if (resourceOptional.isPresent()) {
            Resource resource = resourceOptional.get();
            try {
                String contentType = Files.probeContentType(Paths.get(resource.getURI()));
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(
                                contentType != null ? contentType : "application/octet-stream")
                        )
                        .body(resource);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/my-bills")
    public ResponseEntity<List<String>> getUserBills() {
        List<String> userImageUrls = fileService.getBills();
        return ResponseEntity.ok(userImageUrls);

        // List<File> imageLinks = fileService.getBills();
        // List<ByteArrayResource> inputStreams;
        // inputStreams = imageLinks.stream()
        //         .map(imageLink -> {
        //             try {
        //                 return new ByteArrayResource(Files.readAllBytes(Paths.get(imageLink.toURI())));
        //             } catch (IOException e) {
        //                 throw new RuntimeException(e);
        //             }
        //         })
        //         .toList();
        //
        // System.out.println(inputStreams.get(0));
        //
        // return ResponseEntity.status(HttpStatus.OK)
        //         .contentType(MediaType.IMAGE_JPEG)
        //         .body(inputStreams);

        // List<InputStreamResource> inputStreamResources;
        // inputStreamResources = imageLinks.stream()
        //         .map(imageLink -> {
        //             try {
        //                 return new InputStreamResource(Files.newInputStream(imageLink.toPath()));
        //             } catch (IOException e) {
        //                 throw new RuntimeException(e);
        //             }
        //         })
        //         .toList();
        //
        //
        // return ResponseEntity.status(HttpStatus.OK)
        //         .contentType(MediaType.IMAGE_JPEG)
        //         .body(inputStreamResources);

    }



}