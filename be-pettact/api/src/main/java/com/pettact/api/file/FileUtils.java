package com.pettact.api.file;

import java.util.Arrays;
import java.util.List;

public class FileUtils {

    private static final List<String> IMAGE_MIME_TYPE = Arrays.asList
            ("image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp",
                    "image/webp", "image/svg+xml", "image/tiff", "image/ico", "image/heic", "image/heif");

    /**
     * MIME 타입 기반 이미지 파일 여부 판단
     * @param mimeType MIME 타입
     * @return imageFile == true or != false
     */

    public static boolean isImageFile(String mimeType) {
        if (mimeType == null || mimeType.trim().isEmpty()) {
            return false;
        }
        String lowerMimeType = mimeType.toLowerCase().trim();
        return lowerMimeType.startsWith("image/") || IMAGE_MIME_TYPE.contains(lowerMimeType);
    }

    /**
     *
     * @param mimeType MIME 타입
     * @return attachment == true or != false
     */

    public static boolean isAttachmentFile(String mimeType) {
        return !isImageFile(mimeType);
    }

    public static boolean isImageFileByExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        String lowerFileName = fileName.toLowerCase().trim();
        String extension = "";

        int lastDotIndex = lowerFileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < lowerFileName.length() - 1) {
            extension = lowerFileName.substring(lastDotIndex + 1);
        }

        List<String> imageExtensions = Arrays.asList(
                "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "tiff", "tif", "ico", "heic", "heif"
        );

        return imageExtensions.contains(extension);
    }

    public static boolean isImageFile(String mimeType, String fileName) {
        if (isImageFile(mimeType)) {
            return true;
        }

        // MIME 타입이 애매하면 파일 확장자로 판별
        return isImageFileByExtension(fileName);
    }
}
