package com.ssafy.sunin.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.ssafy.sunin.domain.FeedCollections;
import com.ssafy.sunin.domain.user.User;
import com.ssafy.sunin.dto.feed.*;
import com.ssafy.sunin.repository.FeedRepository;
import com.ssafy.sunin.repository.FollowerRepository;
import com.ssafy.sunin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;
    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.url}")
    private String url;

    private final AmazonS3 amazonS3;

    @Override
    public FeedDto writeImageFeed(FeedWrite feedWrite) {
        List<String> fileList = new ArrayList<>();
        List<MultipartFile> files = feedWrite.getFiles();
        if (files != null) {
            AwsFile(feedWrite.getFiles(),fileList);
        }
        User user = userRepository.findProfileByUserSeq(feedWrite.getUserId());
        FeedCollections feedCollections = FeedCollections.builder()
                .userId(feedWrite.getUserId())
                .content(feedWrite.getContent())
                .hashtags(feedWrite.getHashtags())
                .likes(0)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .flag(true)
                .likeUser(new HashMap<>())
                .filePath(fileList)
                .comments(new HashMap<>())
                .build();

        ObjectId id = feedRepository.save(feedCollections).getId();
        suninDays(feedWrite.getUserId());

        return FeedDto.builder()
                .id(id.toString())
                .userId(feedCollections.getUserId())
                .content(feedCollections.getContent())
                .likes(feedCollections.getLikes())
                .hashtags(feedCollections.getHashtags())
                .createdDate(feedCollections.getCreatedDate())
                .modifiedDate(feedCollections.getModifiedDate())
                .filePath(feedCollections.getFilePath())
//                .comments(feedCollections.getComments())
                .likeUser(feedCollections.getLikeUser())
                .nickName(user.getUserNickname())
                .image(user.getProfileImageUrl())
                .build();
    }

    private void suninDays(Long id) {
        User user = userRepository.findProfileByUserSeq(id);
        user.setSuninDayIncrease();
        userRepository.save(user);
    }

    @Override
    public List<String> downloadFileFeed(String fileNames) {
        ObjectListing objectListing = amazonS3.listObjects(bucket);
        List<String> arrayKeyList = new ArrayList<>();
        List<Date> arrayModTimeList = new ArrayList<>();
        List<String> fileNameList = new ArrayList<>();
        for (S3ObjectSummary s : objectListing.getObjectSummaries()) {
            arrayKeyList.add(s.getKey());
            arrayModTimeList.add(s.getLastModified());
        }
        Date max = Collections.max(arrayModTimeList);
        String fileName = arrayKeyList.get(arrayModTimeList.indexOf(max));

        fileNameList.add(String.format(url + "/%s", fileNames));
        return fileNameList;
    }

    @Override
    public FeedDto updateFile(FileUpdate fileUpdate) {
        Optional<FeedCollections> feed = feedRepository.findByIdAndUserId(new ObjectId(fileUpdate.getId()),fileUpdate.getUserId());
        if(feed.isPresent()){
            fileUpdate.getFiles().forEach(file -> {
                amazonS3.deleteObject(new DeleteObjectRequest(bucket, file));
                feed.get().getFilePath().remove(file);
            });

            User user = userRepository.findProfileByUserSeq(fileUpdate.getUserId());
            feed.get().setFileModified(feed.get().getFilePath());
            FeedCollections feedCollections = feedRepository.save(feed.get());
            return FeedDto.builder()
                    .id(feedCollections.getId().toString())
                    .hashtags(feedCollections.getHashtags())
                    .likes(feedCollections.getLikes())
                    .createdDate(feedCollections.getCreatedDate())
                    .modifiedDate(feedCollections.getModifiedDate())
                    .filePath(feedCollections.getFilePath())
                    .content(feedCollections.getContent())
                    .likeUser(feedCollections.getLikeUser())
//                .comments(feedCollections.getComments())
                    .userId(user.getUserSeq())
                    .nickName(user.getUserNickname())
                    .image(user.getProfileImageUrl())
                    .build();
        }

        return null;
    }

    @Override
    public FeedDto addFile(FeedFile feedFile) {
        Optional<FeedCollections> feed = feedRepository.findByIdAndUserId(new ObjectId(feedFile.getId()),feedFile.getUserId());
        if(feed.isPresent()){
            List<String> fileList = AwsFile(feedFile.getFiles(),feed.get().getFilePath());
            feed.get().setFileModified(fileList);
            FeedCollections feedCollections = feedRepository.save(feed.get());
            User user = userRepository.findProfileByUserSeq(feedFile.getUserId());
            return FeedDto.builder()
                    .id(feedCollections.getId().toString())
                    .hashtags(feedCollections.getHashtags())
                    .likes(feedCollections.getLikes())
                    .createdDate(feedCollections.getCreatedDate())
                    .modifiedDate(feedCollections.getModifiedDate())
                    .filePath(feedCollections.getFilePath())
                    .content(feedCollections.getContent())
                    .likeUser(feedCollections.getLikeUser())
//                .comments(feedCollections.getComments())
                    .userId(feedCollections.getUserId())
                    .nickName(user.getUserNickname())
                    .image(user.getProfileImageUrl())
                    .build();
        }

        return null;
    }

    private List<String> AwsFile(List<MultipartFile> files, List<String> list){
        files.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }
            list.add(String.format(url + "/%s", fileName));
        });

        return list;
    }

    // userId
    @Override
    public FeedDto getDetailFeed(String id) {
        FeedCollections feedCollections = feedRepository.findByIdAndFlagTrue(new ObjectId(String.valueOf(id)));
        User user = userRepository.findProfileByUserSeq(feedCollections.getUserId());
        return FeedDto.builder()
                .id(feedCollections.getId().toString())
                .hashtags(feedCollections.getHashtags())
                .likes(feedCollections.getLikes())
                .createdDate(feedCollections.getCreatedDate())
                .modifiedDate(feedCollections.getModifiedDate())
                .filePath(feedCollections.getFilePath())
                .content(feedCollections.getContent())
                .likeUser(feedCollections.getLikeUser())
//                .comments(feedCollections.getComments())
                .userId(user.getUserSeq())
                .nickName(user.getUserNickname())
                .image(user.getProfileImageUrl())
                .build();
    }

    @Override
    public FeedDto updateFeed(FeedUpdate feedUpdate) {
        FeedCollections feedCollections = feedRepository.findByIdAndUserIdAndFlagTrue(new ObjectId(feedUpdate.getId()), feedUpdate.getUserId());
        feedCollections.setFeedModified(feedUpdate);
        feedRepository.save(feedCollections);

        User user = userRepository.findProfileByUserSeq(feedCollections.getUserId());
        return FeedDto.builder()
                .id(feedCollections.getId().toString())
                .userId(feedCollections.getUserId())
                .hashtags(feedCollections.getHashtags())
                .likes(feedCollections.getLikes())
                .content(feedUpdate.getContent())
                .createdDate(feedCollections.getCreatedDate())
                .modifiedDate(feedCollections.getModifiedDate())
                .likeUser(feedCollections.getLikeUser())
                .filePath(feedCollections.getFilePath())
//                .comments(feedCollections.getComments())
                .nickName(user.getUserNickname())
                .image(user.getProfileImageUrl())
                .build();
    }

    @Override
    public void deleteFeed(String id, Long userId) {
        FeedCollections feedCollections = feedRepository.findByIdAndUserIdAndFlagTrue(new ObjectId(id),userId);
        feedCollections.setFeedDelete();
        feedRepository.save(feedCollections);
    }

    @Override
    public List<FeedDto> getFollowerFeed(Long userId) {
        List<Long> followers = followerRepository.getFollowingList(userId);
        List<User> users = userRepository.findFollowerListByUserSeqIn(followers);
        System.out.println();
        return feedRepository.getFollowerFeed(followers)
                .stream()
                .map(feedCollections -> FeedDto.builder()
                        .id(feedCollections.getId().toString())
                        .userId(feedCollections.getUserId())
                        .content(feedCollections.getContent())
                        .hashtags(feedCollections.getHashtags())
                        .likes(feedCollections.getLikes())
                        .createdDate(feedCollections.getCreatedDate())
                        .modifiedDate(feedCollections.getModifiedDate())
                        .likeUser(feedCollections.getLikeUser())
                        .filePath(feedCollections.getFilePath())
                        .build()).collect(Collectors.toList());
//                .stream()
//                .map(FeedDto::feedDto)
//                .collect(Collectors.toList());
    }


    @Override
    public Page<FeedDto> getLatestFeed(Pageable pageable, Long userId) {
        return feedRepository.findAllByUserId(pageable,userId);
    }

    @Override
    public Page<FeedDto> getLikeFeed(Pageable pageable, Long userId) {
        return feedRepository.findAllByUserId(pageable,userId);
    }

    @Override
    public FeedDto likeFeed(FeedLike feedLike) {
        FeedCollections feedCollections = feedRepository.findByIdAndFlagTrue(new ObjectId(feedLike.getId()));
        Map<Long, Object> users = new HashMap<>(feedCollections.getLikeUser());

        int like = feedCollections.getLikes();

        if (users.containsKey(feedLike.getUserId())) {
            users.remove(feedLike.getUserId());
            like--;
        } else {
            users.put(feedLike.getUserId(), true);
            like++;
        }
        feedCollections.setLikeModified(like,users);

        feedRepository.save(feedCollections);
        return FeedDto.builder()
                .id(feedLike.getId())
                .userId(feedLike.getUserId())
                .likeUser(feedCollections.getLikeUser())
                .likes(feedCollections.getLikes()).build();
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }
}