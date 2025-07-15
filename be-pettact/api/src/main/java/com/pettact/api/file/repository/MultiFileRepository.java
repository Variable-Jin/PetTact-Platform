package com.pettact.api.file.repository;

import com.pettact.api.file.entity.File;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MultiFileRepository extends JpaRepository<File, Long> {


    @Query("SELECT f FROM File f WHERE f.referenceTable = :referenceTable AND f.referenceNo = :referenceNo ORDER BY f.fileOrderNo")
    List<File> findFilesByReference(
            @Param("referenceTable") File.ReferenceTable referenceTable,
            @Param("referenceNo") Long referenceNo
    );

    @Query("SELECT MAX(f.fileOrderNo) FROM File f WHERE f.referenceTable = :referenceTable AND f.referenceNo = :referenceNo")
    Integer findMaxOrder(
            @Param("referenceTable") File.ReferenceTable referenceTable,
            @Param("referenceNo") Long referenceNo
    );
}
