package com.pettact.api.multiFile.controller;


import com.pettact.api.multiFile.dto.FileCreateDto;
import com.pettact.api.multiFile.dto.FileResponseDto;
import com.pettact.api.multiFile.dto.FileUpdateDto;
import com.pettact.api.multiFile.entity.MultiFile;
import com.pettact.api.multiFile.service.FileService;
import com.pettact.api.security.vo.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/v1/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * @param
     * @param file
     * @return
     * 1. 파일 업로드
     * 2. 파일 정보 조회
     * 3. 파일 다운로드 (같은 controller 추가) -> 추가예정
     * 4. 파일 수정
     * 5. 파일 삭제
     * 관리자
     * 1. 파일 상세 정보  -> 메타데이터 조회
     * 2. 전체 파일 관리
     */

    @PostMapping
    public ResponseEntity<FileResponseDto> createFile(
            @RequestParam("referenceTable") String referenceTableStr,
            @RequestParam("referenceNo") Long referenceNo,
            @RequestParam("fileName") String fileName,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // String → Enum 변환
        MultiFile.ReferenceTable referenceTable = MultiFile.ReferenceTable.valueOf(referenceTableStr);

        // DTO 생성
        FileCreateDto fileCreateDto = new FileCreateDto();
        fileCreateDto.setReferenceTable(referenceTable);
        fileCreateDto.setReferenceNo(referenceNo);
        fileCreateDto.setFileName(fileName);

        Long userNo = userDetails.getUser().getUserNo();
        FileResponseDto responseDto = fileService.createFile(fileCreateDto, file, userNo);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 다운로드 컨트롤러 관련
//    @GetMapping("/download/{fileNo}")
//    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileNo) {
//        try {
//            // 파일 정보 조회
//            FileResponseDto fileInfo = fileService.getFile(fileNo);
//
//            // 실제 파일 경로에서 파일 읽기
//            Path filePath = Paths.get(fileInfo.getFilePath());
//            Resource resource = new UrlResource(filePath.toUri());
//
//            if (resource.exists() && resource.isReadable()) {
//                return ResponseEntity.ok()
//                        .contentType(MediaType.parseMediaType("application/octet-stream"))
//                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileInfo.getFileName() + "\"")
//                        .body(resource);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }

    @GetMapping("/{fileNo}")
    public ResponseEntity<FileResponseDto> getFile(@PathVariable Long fileNo) {
        FileResponseDto responseDto = fileService.getFile(fileNo);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{fileNo}")
    public ResponseEntity<FileResponseDto> updateFile(@PathVariable Long fileNo, @RequestBody FileUpdateDto fileUpdateDto,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUser().getUserNo();
        FileResponseDto responseDto = fileService.updateFile(fileNo, fileUpdateDto, userNo);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{fileNo}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileNo, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUser().getUserNo();
        fileService.delete(fileNo, userNo);
        return ResponseEntity.noContent().build();
    }

}
