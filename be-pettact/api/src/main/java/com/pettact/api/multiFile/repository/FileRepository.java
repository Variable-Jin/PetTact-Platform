package com.pettact.api.multiFile.repository;

import com.pettact.api.multiFile.entity.MultiFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<MultiFile, Long> {
}
