package com.pettact.api.multiFile.service;


import com.pettact.api.multiFile.FileUtil;
import com.pettact.api.multiFile.dto.FileCreateDto;
import com.pettact.api.multiFile.dto.FileResponseDto;
import com.pettact.api.multiFile.dto.FileUpdateDto;
import com.pettact.api.multiFile.entity.MultiFile;
import com.pettact.api.multiFile.repository.FileRepository;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
@Slf4j
public class FileService {

    @Autowired
    private FileRepository fileRepository;


    @Value("${file.upload.path:/uploads}")
    private String uploadPath;
    @Autowired
    private UserRepository userRepository;

    public String store(MultipartFile file) {
        try {
            // 폴더가 없으면 생성
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalFilename = file.getOriginalFilename();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String storedFileName = timestamp + "_" + originalFilename;

            Path filePath = uploadDir.resolve(storedFileName);
            file.transferTo(filePath.toFile());

            return filePath.toString();
        } catch (IOException e) {
            log.error("파일 저장 실패: ", e);  // 구체적인 에러 로그
            throw new IllegalArgumentException("파일 저장에 실패하였습니다. 잠시 후에 다시 시도해주세요.");
        }
    }

    @Transactional
    public FileResponseDto createFile(FileCreateDto fileCreateDto, MultipartFile file, Long userNo) {
         if (file == null || file.isEmpty()) {
             throw new IllegalArgumentException("파일이 비어있습니다.");
         }
         String mimeType = file.getContentType();

        if (FileUtil.isImageFile(mimeType)) {
            // 이미지: 10MB 제한
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("이미지 파일 크기는 10MB를 초과할 수 없습니다.");
            }
        } else {
            // 첨부파일: 50MB 제한
            if (file.getSize() > 50 * 1024 * 1024) {
                throw new IllegalArgumentException("첨부파일 크기는 50MB를 초과할 수 없습니다.");
            }
        }

        userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 물리적 파일 저장
        String filePath = store(file);

        MultiFile multiFile = fileCreateDto.toEntity();
        multiFile.setUserNo(userNo);
        multiFile.setFilePath(filePath);
        multiFile.setFileName(file.getOriginalFilename());
        multiFile.setFileSize((int) file.getSize());
        multiFile.setCreatedAt(LocalDateTime.now());
        multiFile.setFileMimeType(file.getContentType());

        MultiFile saved = fileRepository.save(multiFile);

        log.info("파일 업로드 성공: fileId={}, originalName={}", saved.getFileNo(), saved.getFileName());
        return FileResponseDto.fromEntity(saved);
    }


    public FileResponseDto getFile(Long fileNo) {
        MultiFile file = fileRepository.findById(fileNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 파일을 찾을 수 없습니다."));
        return FileResponseDto.fromEntity(file);
    }

    @Transactional
    public FileResponseDto updateFile(Long fileNo, FileUpdateDto fileUpdateDto, Long userNo) {
        MultiFile file = fileRepository.findById(fileNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 파일을 찾을 수 없습니다."));
        if(!file.getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("파일 수정 권한이 없습니다.");
        }

        userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (fileUpdateDto.getFileMimeType() != null && fileUpdateDto.getFileSize() != null) {
            String newMimeType = fileUpdateDto.getFileMimeType();
            Integer newFileSize = fileUpdateDto.getFileSize();

            if (FileUtil.isImageFile(newMimeType)) {
                // 이미지: 10MB 제한
                if (newFileSize > 10 * 1024 * 1024) {
                    throw new IllegalArgumentException("이미지 파일 크기는 10MB를 초과할 수 없습니다.");
                }
            } else {
                // 첨부파일: 50MB 제한
                if (newFileSize > 50 * 1024 * 1024) {
                    throw new IllegalArgumentException("첨부파일 크기는 50MB를 초과할 수 없습니다.");
                }
            }
        }

        file.patch(fileUpdateDto);
        MultiFile saved = fileRepository.save(file);
        return FileResponseDto.fromEntity(saved);
    }

    @Transactional
    public void delete(Long fileNo, Long userNo) {
        MultiFile file = fileRepository.findById(fileNo)
                .orElseThrow(() -> new IllegalArgumentException("헤딩 파일을 찾을 수 없습니다."));

        if (!file.getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("파일 삭제 권한이 없습니다.");
        }

        try {
            Files.deleteIfExists(Paths.get(file.getFilePath()));
            log.info("물리적 파일 삭제 성공: {}", file.getFilePath());
        } catch (IOException e) {
            log.warn("물리적 파일 삭제 실패: {}", file.getFilePath(), e);
            // 물리적 파일 삭제 실패해도 DB에서는 삭제 진행
        }
        fileRepository.deleteById(fileNo);
    }


}
