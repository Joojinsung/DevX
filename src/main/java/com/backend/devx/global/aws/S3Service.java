package com.backend.devx.domain.board.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.backend.devx.global.exception.BusinessException;
import com.backend.devx.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 파일 업로드 처리 메서드
    public List<String> upload(List<MultipartFile> multipartFiles) {
        List<String> imgUrlList = new ArrayList<>();

        if (multipartFiles == null || multipartFiles.isEmpty()) {
            throw new BusinessException(ErrorCode.REQUIRED_IMAGE);
        }

        for (MultipartFile file : multipartFiles) {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                String imgUrl = generateImgUrl(fileName);
                imgUrlList.add(imgUrl);
            } catch (IOException e) {
                throw new BusinessException(ErrorCode.UPLOAD_ERROR_IMAGE);
            }
        }
        return imgUrlList;
    }

    private String generateImgUrl(String filename) {
        return "https://" + bucket + ".s3.amazonaws.com/" + filename;
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new BusinessException(ErrorCode.REQUIRED_IMAGE);
        }

        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        List<String> fileValidate = List.of(".jpg", ".jpeg", ".png", ".JPG", ".JPEG", ".PNG");
        if (!fileValidate.contains(idxFileName)) {
            throw new BusinessException(ErrorCode.VALID_ERROR_IMAGE);
        }
        return idxFileName;
    }

    public void deleteFile(String fileName) {
        amazonS3.deleteObject(bucket, fileName);
    }
}
