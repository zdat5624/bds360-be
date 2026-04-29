package vn.bds360.backend.modules.media.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.bds360.backend.common.exception.AppException;
import vn.bds360.backend.common.exception.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final Cloudinary cloudinary;

    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "image/bmp", "image/tiff", "image/heic", "image/avif", "image/apng");
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    // ==========================================
    // LƯU ẢNH (Có validate định dạng ảnh)
    // ==========================================
    public String storeImage(MultipartFile file) {
        validateFileSize(file);
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new AppException(ErrorCode.FILE_FORMAT_INVALID);
        }
        return executeUpload(file, "bds360/images");
    }

    // ==========================================
    // LƯU FILE BẤT KỲ (Chỉ validate dung lượng)
    // ==========================================
    public String storeFile(MultipartFile file) {
        validateFileSize(file);
        return executeUpload(file, "bds360/files");
    }

    // ==========================================
    // CÁC HÀM HELPER NỘI BỘ
    // ==========================================
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }
    }

    private String executeUpload(MultipartFile file, String folderName) {
        try {
            // Đẩy lên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderName,
                            "resource_type", "auto" // Tự động nhận diện ảnh/video/file thô
                    ));

            // Lấy URL an toàn (HTTPS) trả về
            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            log.error(">>> Lỗi upload file lên Cloudinary: {}", e.getMessage());
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }
}