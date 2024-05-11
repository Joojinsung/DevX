package com.backend.devx.global.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);

        // Amazon S3 클라이언트를 생성하는 빌더 객체를 생성
        // AmazonS3ClientBuilder는 AmazonS3 클라이언트를 생성하는 빌더 패턴을 구현한 클래스입니다.
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                //클라이언트 빌더에 지정된 지역 (Region) 을 설정
                .withRegion(region)
                // 클라이언트 빌더에 AWS 자격 증명을 제공
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                // 생성된 Amazon S3 클라이언트 객체를 반환
                .build();
    }
}

