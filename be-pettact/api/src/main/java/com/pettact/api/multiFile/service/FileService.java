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
import java.util.ArrayList;
import java.util.List;

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

            //return filePath.toString();
            return storedFileName;
        } catch (IOException e) {
            log.error("파일 저장 실패: ", e);  // 구체적인 에러 로그
            throw new IllegalArgumentException("파일 저장에 실패하였습니다. 잠시 후에 다시 시도해주세요.");
        }
    }

    /**
     * 다음 순서 번호 계산
     */
    private int getNextFileOrder(MultiFile.ReferenceTable referenceTable, Long referenceNo) {
        Integer maxOrder = fileRepository.findMaxOrderByReference(referenceTable, referenceNo);
        return (maxOrder == null) ? 1 : maxOrder + 1;
    }

    @Transactional
    public FileResponseDto createFile(FileCreateDto fileCreateDto, MultipartFile file, Long userNo) {
    	log.info("[FILE-SERVICE] dto: {}, file is null?={}, userNo={}", fileCreateDto, file == null, userNo);
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
//        // 물리적 파일 저장
//        String filePath = store(file);
//
//        MultiFile multiFile = fileCreateDto.toEntity();
//        multiFile.setUserNo(userNo);
//
//        // 순서 자동 계산 적용
//        int nextOrder = getNextFileOrder(multiFile.getReferenceTable(), multiFile.getReferenceNo());
//        multiFile.setFileOrderNo(nextOrder);
//
//        multiFile.setFilePath(filePath);
//        multiFile.setFileName(file.getOriginalFilename());
//        multiFile.setFileSize((int) file.getSize());
//        multiFile.setCreatedAt(LocalDateTime.now());
//        multiFile.setFileMimeType(file.getContentType());
//
//        MultiFile saved = fileRepository.save(multiFile);


        String storedFileName = store(file); // 저장된 파일명만 받아오기
        String fullFilePath = uploadPath + "/" + storedFileName;
        System.out.println(">>> store() 함수 반환 storedFileName: " + storedFileName);
        System.out.println(">>> fullFilePath 값: " + fullFilePath);


        MultiFile multiFile = fileCreateDto.toEntity();
        System.out.println(">>> MultiFile 생성 - referenceNo: " + multiFile.getReferenceNo());

        multiFile.setUserNo(userNo);

        // 순서 계산
        int nextOrder = getNextFileOrder(multiFile.getReferenceTable(), multiFile.getReferenceNo());
        multiFile.setFileOrderNo(nextOrder);

        // 🔽 설정 값들
        multiFile.setFilePath(fullFilePath); // 전체 경로
        multiFile.setStoredFileName(storedFileName); // ✅ 추가한 저장된 파일명
        multiFile.setImageUrl(storedFileName); //추가
        multiFile.setFileName(file.getOriginalFilename()); // 원본명
        multiFile.setFileSize((int) file.getSize());
        multiFile.setCreatedAt(LocalDateTime.now());
        multiFile.setFileMimeType(file.getContentType());

        System.out.println(">>> MultiFile 필드 세팅 후 저장 전 값들 - filePath: " + multiFile.getFilePath() + ", storedFileName: " + multiFile.getStoredFileName());

        MultiFile saved = fileRepository.save(multiFile);

        System.out.println("✅ 저장된 MultiFile 엔티티 - filePath: " + saved.getFilePath() + ", storedFileName: " + saved.getStoredFileName());


        System.out.println("✅ 저장된 storedFileName: " + saved.getStoredFileName());
        FileResponseDto dto = FileResponseDto.fromEntity(saved);
        System.out.println("✅ FileResponseDto 반환: " + dto);

        log.info("파일 업로드 성공: fileId={}, originalName={}", saved.getFileNo(), saved.getFileName());
        return FileResponseDto.fromEntity(saved);
    }


    public FileResponseDto getFile(Long fileNo) {
        MultiFile file = fileRepository.findById(fileNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 파일을 찾을 수 없습니다."));
        return FileResponseDto.fromEntity(file);
    }


    /**
     * 특정 테이블의 특정 레코드에 연결된 파일 목록 조회
     */
    public List<MultiFile> getFilesByReference(MultiFile.ReferenceTable referenceTable, Long referenceNo) {
        return fileRepository.findByReferenceTableAndReferenceNoOrderByFileOrderNo(
                referenceTable, referenceNo);
    }


    /**
     * 여러 파일을 한번에 저장
     */


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
