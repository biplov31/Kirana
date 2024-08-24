package com.example.Kirana.service;

import com.example.Kirana.model.ImageFile;
import com.example.Kirana.model.KiranaUser;
import com.example.Kirana.repository.FileRepository;
import com.example.Kirana.utils.LoggedInUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    @Value("${image.upload-dir}")
    private String imageDir;

    private static final String UPLOAD_DIRECTORY = "C:\\KiranaUploads";
    private static final UUID uuid = UUID.randomUUID();

    private static final Set<String> ALLOWED_FILE_TYPES = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/jpg",
            MediaType.APPLICATION_OCTET_STREAM_VALUE
    );

    private final FileRepository fileRepository;
    private final LoggedInUser loggedInUser;

    public String modifyFileName(String filename) {
        String[] fileNameSplits = filename.split("\\.");
        int nameIndex = 0;
        int extensionIndex = fileNameSplits.length - 1;
        return fileNameSplits[nameIndex] + "_" + uuid.toString() + "." + fileNameSplits[extensionIndex];
    }

    public ImageFile saveFileToDrive(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        if (fileName == null || fileName.contains("..") || !ALLOWED_FILE_TYPES.contains(contentType)) {
            throw new SecurityException("Invalid file name or type.");
        }
        // var targetFile = new File(UPLOAD_DIRECTORY + File.separator + modifyFileName(file.getOriginalFilename()));
        Path targetFilePath = Paths.get(UPLOAD_DIRECTORY).resolve(modifyFileName(fileName)).normalize();

        try {
            // create directories if they do not exist
            Files.createDirectories(targetFilePath.getParent());
            // copy the file to the target location
            Files.copy(file.getInputStream(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);

            return ImageFile.builder()
                    .fileName(fileName)
                    .fileType(contentType)
                    .filePath(targetFilePath.toString())
                    .build();
        } catch (IOException e) {
            log.error("IO Exception: {}", e.getMessage());
            return null;
        }
    }

    public ImageFile saveFileToFolder(MultipartFile file) {
        String fileName = StringUtils.cleanPath(modifyFileName(file.getOriginalFilename()));
        String filePath = Paths.get(imageDir, fileName).toString();

        // create folder if it doesn't exist
        File directory = new File(imageDir);
        if (!directory.exists()) directory.mkdirs();

        try {
            if (fileName.contains("..")) {
                throw new SecurityException("Invalid file name.");
            }

            // save to folder
            Path targetPath = Paths.get(filePath);
            Files.copy(file.getInputStream(), targetPath);

            // check file type
            String fileType = file.getContentType();
            if (fileType == null) fileType = Files.probeContentType(targetPath);

            return ImageFile.builder()
                    .fileName(fileName)
                    .fileType(fileType)
                    .filePath(filePath)
                    .build();

        } catch (IOException e) {
            log.error("IO Exception: {}", e.getMessage());
            return null;
        }
    }

    // @Transactional
    public List<String> saveFiles(List<MultipartFile> fileList) {
        // if (LOGGED_IN_USER == null) {
        //     LOGGED_IN_USER = loggedInUser.getLoggedInUserEntity();
        // }
        KiranaUser kiranaUser = loggedInUser.getLoggedInUserEntity();
        List<ImageFile> imageFileList = fileList.stream()
                .map(file -> {
                    ImageFile savedFile = saveFileToDrive(file);
                    // ImageFile savedFile = saveFileToFolder(file);
                    savedFile.setKiranaUser(kiranaUser);
                    return savedFile;
                })
                .collect(Collectors.toList());

        List<ImageFile> savedFiles = fileRepository.saveAll(imageFileList);
        return savedFiles.stream()
                .map(file -> file.getFileName())
                .collect(Collectors.toList());
    }

    // public ImageFile getBill(Integer billId) {
    //     Optional<ImageFile> imageFile = fileRepository.findById(billId);
    //     return imageFile.orElse(null);
    // }
    //
    // public byte[] getBillData(ImageFile imageFile) {
    //     String filePath = imageFile.getFilePath();
    //     try {
    //         byte[] imageData = Files.readAllBytes(new File(filePath).toPath());
    //         return imageData;
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         return null;
    //     }
    // }

    public Optional<Resource> getBill(String fileName) {
        try {
            if (fileName.contains("..")) {
                log.warn("Invalid file name: {}", fileName);
                return Optional.empty();
            }

            // Paths.get() creates a Path object, resolve() combines base path with the file name and normalize() cleans up the path
            Path file = Paths.get(UPLOAD_DIRECTORY).resolve(fileName).normalize();
            // UrlResource represents resources accessible via a URL, toUri() converts the Path object to a URI object
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return Optional.of(resource);
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            log.error("IO Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public List<String> getBills() {
        KiranaUser kiranaUser = loggedInUser.getLoggedInUserEntity();

        List<ImageFile> userImages = fileRepository.findByKiranaUserIdOrderByCreatedAtDesc(kiranaUser.getId());
        List<String> imageLinks = userImages.stream()
                .map(imageFile -> {
                    String fullPath = UPLOAD_DIRECTORY + File.separator + imageFile.getFileName();
                    return getImageUri(fullPath);
                })
                .toList();

        return imageLinks;
    }

    public String getImageUri(String fullPath) {
        // Extract the filename from the full path
        String filename = Paths.get(fullPath).getFileName().toString();

        // Generate the URI
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/my-bill/")
                .path(filename)
                .toUriString();
    }

}
