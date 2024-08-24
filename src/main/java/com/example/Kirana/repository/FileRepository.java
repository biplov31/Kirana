package com.example.Kirana.repository;

import com.example.Kirana.model.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<ImageFile, Integer> {

    List<ImageFile> findByKiranaUserIdOrderByCreatedAtDesc(Integer id);
}
