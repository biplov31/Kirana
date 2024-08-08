package com.example.Kirana.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "file_name", nullable = false)
    private String fileName;
    @Column(name = "file_type")
    private String fileType;
    @Column(name = "file_path", nullable = false)
    private String filePath;
    @Column(name = "created_at")
    private final LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private KiranaUser kiranaUser;

}
