package com.example.gympt.util.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Util {

    /**
     * s3와 연결되는 시크릿 키를 props 로 받아와
     * 이곳에서 crud 를 어떤식으로 할건지 정해놓고
     * 이 클래스를 호출하여 이미지 리스트 보기 , 업로드 수정 삭제 작업이 이루어 진다
     */
    @Value("${app.props.aws.s3.bucket-name}")
    private String bucketName;

    @Value("${app.props.aws.s3.region}")
    private String region;

    private final AmazonS3 s3Client;
    //아마존  s3 와 연결되는 인터페이스 / 클래스
    //crud 작업을 위한 objectAPI 를 호출하여 사용할 수 있다(버킷에서 세팅한 오브젝트를 가져오는거임)


    /**
     * S3에 파일 업로드
     * @param files 파일 리스트
     * @return 업로드된 파일 URL 리스트
     */

    public List<String> uploadFiles(List<MultipartFile> files) {
        return files.stream()
                .map(this::uploadFile)
                .toList();
    }

    /**
     * S3에 파일 업로드
     *
     * @param file 파일
     * 업로드된 파일 URL 만들어줌
     * MultipartFile : 업로드된 파일의 내용과 메타데이터를 표현 , 클라에게 받을때 from-data 형식으로 받음
     *
     */
    public String uploadFile(MultipartFile file) {
       //1. 파일의 유효성을 검증한다
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        } // 파일이 빈 파일이면 예외처리

        String extension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        //getOriginalFilename : 원본 파일명 반환
        //2. 파일의 확장자 추출 , 검증
        checkImageExtension(extension);
        //이미지 확장자 검증

        /*파일 이름 생성 */
        String originalFilename = file.getOriginalFilename();
        String thumbnailFileName = "s_" + UUID.randomUUID().toString() + "-" + originalFilename;
       //UUID : 고유 식별자 를 생성해주는 클래스(랜덤 숫자 + 문자열 반환 )
        //UUID + 원본 파일 명 -> 고유한 이름으로 생성

        Path thumbnailPath = null;
        //Path : 파일의 경로 조작 인터페이스

        try {
            thumbnailPath = Paths.get(thumbnailFileName);
            // 썸네일 생성
            Thumbnails.of(file.getInputStream()) // Thumbnailator 라이브러리 사용
                    .size(400, 400) // 썸네일 크기
                    .outputFormat(extension)  // 원본 파일의 확장자를 사용
                    .toFile(thumbnailPath.toFile());
            //Thumbnails : 이미지 썸네일을 만들어주는 클래스
            // S3에 썸네일 업로드
            // putObject API를 사용하여 S3에 파일 업로드
            s3Client.putObject(new PutObjectRequest(bucketName, // 내 버킷 이름
                     thumbnailPath.toFile().getName(), //s3 에 저장할 파일 명
                    thumbnailPath.toFile())); // 업로드 할 파일
        }
        //PutObjectRequest : s3에 업로드를 요청할 객체의 정보를 캡슐화
        // 파일 업로드 요청에 필요한 정보들을 하나로 묶어서 관리


        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            // 썸네일 로컬 파일 삭제
            if (thumbnailPath != null && Files.exists(thumbnailPath)) {
                //Files : 파일 생성 , 삭제 작업을 위한 유틸리티 클래스
                log.info("local thumbnailPath exist! {}", thumbnailPath);
                try {
                    Files.delete(thumbnailPath);

                } catch (IOException e) {
                    // 예외 발생 시 로그 남기기
                    log.error("Failed to delete local thumbnail file: {}", e.getMessage());
                }
            }
        }
        return thumbnailFileName;
    }

    /**
     * 이미지 확장자 검증
     *
     * @param extension 이미지 확장자
     */
    private void checkImageExtension(String extension) {
        // 허용된 이미지 확장자 검증 -> TODO webp 추가 (고화질 이미지)
        Set<String> allowedExtensions = Set.of("jpg", "jpeg", "png", "gif");
        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("이미지 확장자는 jpg, jpeg, png, gif만 허용됩니다.");
        }
    }


    /**
     * S3에 있는 파일 가져오기
     *
     * @param fileName 파일 이름
     * @return 파일 리소스
     * 이미지(파일) 이름으로 이미지 불러오기
     * @throws IOException 파일이 없을 경우 예외 발생
     */
    public ResponseEntity<Resource> getFile(String fileName) throws IOException {

        // fileName = dbac534f-f3b6-4b33-9b83-e308e3c2c29d_e52319408af1ee349da788ec09ca6d92ff7bd70a3b99fa287c599037efee.jpg
        // https://mall-s3.s3.ap-northeast-2.amazonaws.com/dbac534f-f3b6-4b33-9b83-e308e3c2c29d_e52319408af1ee349da788ec09ca6d92ff7bd70a3b99fa287c599037efee.jpg
        // 로 전환!
        //fileName 은 이미지 Gym/trainer Image 엔티티의 gym/trainerImageName 와 동일
        String urlStr = s3Client.getUrl(bucketName, fileName).toString();
        //s3 url 생성

        Resource resource;
        //파일, 클래스패스 리소스, URL 등의 리소스를 추상화해주는 인터페이스
        HttpHeaders headers = new HttpHeaders();
        try {
            //url 객체 설정
            URL url = new URL(urlStr);
            URLConnection urlConnection = url.openConnection();
            // url 연결
            InputStream inputStream = urlConnection.getInputStream();
            //InputStream : 바이트 기반 입력 값 , s3의 이미지 데이터를 읽음
            //이미지 url 연결 객체를 인풋스트림으로 읽어옴
            resource = new InputStreamResource(inputStream);
            // 스트림 객체 -> Resource 객체 변환

            // MIME 타입 설정
            //(Multipurpose Internet Mail Extensions)
            //파일형식 식별자 : jpeg, png, text , json
            String mimeType = urlConnection.getContentType();
            if (mimeType == null) {
                // MIME 타입이 없는 경우 파일 확장자로 추측
                Path path = Paths.get(fileName);
                mimeType = Files.probeContentType(path);
            }
            headers.add("Content-Type", mimeType);
            //응답 헤더에 mimeType 추가
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);

    }


    /**
     * S3에 파일 삭제
     * @param fileNames 파일 이름 리스트
     */
    public void deleteFiles(List<String> fileNames) {

        if (fileNames == null || fileNames.isEmpty()) {
            return;
        }

        for (String fileName : fileNames) {
            s3Client.deleteObject(bucketName, fileName);
        } // 이미지 삭제 api
    }

    /**
     * S3에 파일 삭제
     * @param fileName  파일 이름
     */
    public void deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }

    /**
     * S3에 있는 파일 URL 가져오기
     * @param fileName 파일 이름
     * @return 파일 URL
     */
    public String getUrl(String fileName) {
        return s3Client.getUrl(bucketName, fileName).toString();
    }
}