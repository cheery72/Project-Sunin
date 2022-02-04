package com.ssafy.sunin.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.ssafy.sunin.domain.FeedCollections;
import com.ssafy.sunin.domain.user.User;
import com.ssafy.sunin.dto.*;
import com.ssafy.sunin.repository.FeedRepository;
import com.ssafy.sunin.repository.FollowerRepository;
import com.ssafy.sunin.user.UserRepository;
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
    public FeedDto writeImageFeed(FeedVO feedVO) {
        List<String> fileNameList = new ArrayList<>();
        List<MultipartFile> files = feedVO.getFiles();
        if (files != null) {
            feedVO.getFiles().forEach(file -> {
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

                fileNameList.add(String.format(url + "/%s", fileName));
            });
        }

        FeedCollections feedCollections = FeedCollections.builder()
                .userId(feedVO.getUserId())
                .content(feedVO.getContent())
                .hashtags(feedVO.getHashtags())
                .likes(0)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .flag(true)
                .likeUser(new HashMap<>())
                .filePath(fileNameList)
                .comments(new ArrayList<>())
                .build();

        ObjectId id = feedRepository.save(feedCollections).getId();
        suninDays(feedVO.getUserId());

        return FeedDto.builder()
                .id(id.toString())
                .userId(feedCollections.getUserId())
                .content(feedCollections.getContent())
                .likes(feedCollections.getLikes())
                .hashtags(feedCollections.getHashtags())
                .createdDate(feedCollections.getCreatedDate())
                .modifiedDate(feedCollections.getLastModifiedDate())
                .filePath(feedCollections.getFilePath())
//                .comments(feedCollections.getComments())
                .likeUser(feedCollections.getLikeUser())
                .build();
    }

    private void suninDays(String userNickname) {
        User user = userRepository.getUser(userNickname);
        int sunin = user.getSuninDays();
//        sunin++;
        user.setUserSuninDay(sunin);
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
    public FeedDto getDetailFeed(String id) {
        FeedCollections feedCollections = feedRepository.findByIdAndFlagTrue(new ObjectId(String.valueOf(id)));

        return FeedDto.builder()
                .id(feedCollections.getId().toString())
                .userId(feedCollections.getUserId())
                .hashtags(feedCollections.getHashtags())
                .likes(feedCollections.getLikes())
                .createdDate(feedCollections.getCreatedDate())
                .modifiedDate(feedCollections.getLastModifiedDate())
                .filePath(feedCollections.getFilePath())
                .content(feedCollections.getContent())
                .likeUser(feedCollections.getLikeUser())
//                .comments(feedCollections.getComments())
                .build();
    }

    @Override
    public FeedDto updateFeed(FeedUpdate feedUpdate) {
        FeedCollections feedCollections = feedRepository.findByIdAndFlagTrue(new ObjectId(feedUpdate.getId()));
        LocalDateTime time = LocalDateTime.now();
        feedCollections.setContent(feedUpdate.getContent());
        feedCollections.setHashtags(feedUpdate.getHashtags());
        feedCollections.setLastModifiedDate(time);
        feedCollections.setFilePath(feedCollections.getFilePath());
        feedRepository.save(feedCollections);

        return FeedDto.builder()
                .id(feedCollections.getId().toString())
                .userId(feedCollections.getUserId())
                .hashtags(feedCollections.getHashtags())
                .likes(feedCollections.getLikes())
                .content(feedUpdate.getContent())
                .createdDate(feedCollections.getCreatedDate())
                .modifiedDate(time)
                .likeUser(feedCollections.getLikeUser())
                .filePath(feedCollections.getFilePath())
//                .comments(feedCollections.getComments())
                .build();
    }

    @Override
    public void deleteFeed(String id) {
        FeedCollections feedCollections = feedRepository.findByIdAndFlagTrue(new ObjectId(id));
        feedCollections.setFlag(false);
        feedCollections.setLastModifiedDate(LocalDateTime.now());
        feedRepository.save(feedCollections);
    }

    @Override
    public List<FeedDto> getFollowerFeed(Long id) {
        List<String> followers = followerRepository.getFollowingList(id);
        return feedRepository.getFollowerFeed(followers)
                .stream()
                .map(this::test)
                .collect(Collectors.toList());
    }

    private FeedDto test(FeedCollections feed) {
        return FeedDto.builder()
                .id(feed.getId().toString())
                .userId(feed.getUserId())
                .content(feed.getContent())
                .hashtags(feed.getHashtags())
                .likes(feed.getLikes())
                .createdDate(feed.getCreatedDate())
                .modifiedDate(feed.getLastModifiedDate())
                .likeUser(feed.getLikeUser())
                .filePath(feed.getFilePath())
                .build();
    }

    @Override
    public List<FeedDto> getLatestFeed(FeedList feedList) {
        List<FeedCollections> feed = feedRepository.getLatestFeed(feedList);
        return feed.stream()
                .map(this::test)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedDto> getPageLatestFeed(Pageable pageable) {
        return feedRepository.findAll(pageable).stream()
                .map(feedCollections -> FeedDto.builder()
                        .id(feedCollections.getId().toString())
                        .userId(feedCollections.getUserId())
                        .content(feedCollections.getContent())
                        .likes(feedCollections.getLikes())
                        .filePath(feedCollections.getFilePath())
                        .hashtags(feedCollections.getHashtags())
                        .createdDate(feedCollections.getCreatedDate())
                        .modifiedDate(feedCollections.getLastModifiedDate())
                        .likeUser(feedCollections.getLikeUser())
                        .build()).collect(Collectors.toList());
    }

    @Override
    public List<FeedDto> getLikeFeed(FeedList feedList) {
        List<FeedCollections> feed = feedRepository.getLikeFeed(feedList);
        return feed.stream()
                .map(feedCollections -> FeedDto.builder()
                        .id(feedCollections.getId().toString())
                        .userId(feedCollections.getUserId())
                        .content(feedCollections.getContent())
                        .likes(feedCollections.getLikes())
                        .filePath(feedCollections.getFilePath())
                        .hashtags(feedCollections.getHashtags())
                        .createdDate(feedCollections.getCreatedDate())
                        .modifiedDate(feedCollections.getLastModifiedDate())
                        .likeUser(feedCollections.getLikeUser())
                        .build()).collect(Collectors.toList());
    }

    @Override
    public List<FeedDto> getPageLikeFeed(FeedPage feedPage) {
        Page<FeedCollections> feed = feedRepository.findAll(feedPage.getPageable());
        return feed.stream()
                .map(feedCollections -> FeedDto.builder()
                        .id(feedCollections.getId().toString())
                        .userId(feedCollections.getUserId())
                        .content(feedCollections.getContent())
                        .likes(feedCollections.getLikes())
                        .filePath(feedCollections.getFilePath())
                        .hashtags(feedCollections.getHashtags())
                        .createdDate(feedCollections.getCreatedDate())
                        .modifiedDate(feedCollections.getLastModifiedDate())
                        .likeUser(feedCollections.getLikeUser())
                        .build()).collect(Collectors.toList());
    }

    @Override
    public FeedDto likeFeed(FeedLike feedLike) {
        FeedCollections feedCollections = feedRepository.findByIdAndFlagTrue(new ObjectId(feedLike.getId()));
        Map<String, Object> users = new HashMap<>();
        users.putAll(feedCollections.getLikeUser());

        int like = feedCollections.getLikes();
        // 좋아요 누른상태
        if (users.containsKey(feedLike.getUser())) {
            users.remove(feedLike.getUser());
            feedCollections.setLikes(--like);
        } else {
            users.put(feedLike.getUser(), true);
            feedCollections.setLikes(++like);
        }

        feedCollections.setLikeUser(users);
        feedRepository.save(feedCollections);
        return FeedDto.builder()
                .id(feedLike.getId())
                .userId(feedLike.getUser())
                .likeUser(users)
                .likes(like).build();
    }

    @Override
    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
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
