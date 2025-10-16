package com.pettact.api.multiFile.repository;

import com.pettact.api.multiFile.entity.MultiFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<MultiFile, Long> {
    List<MultiFile> findByReferenceTableAndReferenceNoOrderByFileOrderNo(MultiFile.ReferenceTable referenceTable, Long referenceNo);

    @Query("SELECT MAX(m.fileOrderNo) FROM MultiFile m WHERE m.referenceTable = :referenceTable AND m.referenceNo = :referenceNo")
    Integer findMaxOrderByReference(@Param("referenceTable") MultiFile.ReferenceTable referenceTable, @Param("referenceNo") Long referenceNo);

}
