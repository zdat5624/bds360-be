package vn.bds360.backend.modules.media.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import vn.bds360.backend.common.annotation.ApiGlobalResponse;
import vn.bds360.backend.common.dto.response.ApiResponse;
import vn.bds360.backend.common.exception.AppException;
import vn.bds360.backend.common.exception.ErrorCode;
import vn.bds360.backend.modules.media.service.FileStorageService;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
@ApiGlobalResponse
@Tag(name = "media", description = "Quản lý upload hình ảnh và tệp tin lên Cloudinary")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload/file")
    public ApiResponse<List<String>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        validateEmptyFiles(files);

        List<String> fileUrls = files.stream()
                .map(fileStorageService::storeFile) // Service giờ đã trả thẳng URL
                .collect(Collectors.toList());

        return ApiResponse.success(fileUrls, "Upload tệp thành công");
    }

    @PostMapping("/upload/image")
    public ApiResponse<List<String>> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        validateEmptyFiles(files);

        List<String> fileUrls = files.stream()
                .map(fileStorageService::storeImage) // Service giờ đã trả thẳng URL
                .collect(Collectors.toList());

        return ApiResponse.success(fileUrls, "Upload ảnh thành công");
    }

    private void validateEmptyFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty() || files.get(0).isEmpty()) {
            throw new AppException(ErrorCode.INVALID_PARAMETER);
        }
    }
}