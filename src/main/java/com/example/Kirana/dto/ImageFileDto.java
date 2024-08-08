package com.example.Kirana.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageFileDto {

    private String fileName;
    private String fileType;
    private String filePath;

}
