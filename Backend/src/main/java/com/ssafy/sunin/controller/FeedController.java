package com.ssafy.sunin.controller;

import com.ssafy.sunin.payload.response.feed.FeedSearch;
import com.ssafy.sunin.payload.response.user.UserDetailProfile;
import com.ssafy.sunin.payload.request.feed.*;
import com.ssafy.sunin.payload.response.feed.FeedCommentDto;
import com.ssafy.sunin.payload.response.feed.FeedDto;
import com.ssafy.sunin.service.FeedServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RestControllerAdvice(annotations = RestController.class)
@RequestMapping("/feed")
@Slf4j
public class FeedController {

    private final FeedServiceImpl feedService;

    @ApiOperation(value = "Feed 작성", notes = "다중 파일 업로드 가능")
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<FeedDto> writeImageFeed(@RequestBody @Valid FeedWrite feedWrite){
        log.info("writerImageFeed");

        if(ObjectUtils.isEmpty(feedWrite)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(feedService.writeImageFeed(feedWrite));
    }

    @ApiOperation(value = "file 다운로드")
    @GetMapping("/download")
    public ResponseEntity<List<String>> downloadFiles(@RequestParam("fileName") String fileName){
        log.info("downloadFiles");
        return ResponseEntity.ok(feedService.downloadFileFeed(fileName));
    }

    @ApiOperation(value = "피드 사진 삭제", notes = "사진 여러장 삭제 가능")
    @PutMapping("/file")
    public ResponseEntity<FeedDto> updateFile(@RequestBody @Valid FileUpdate fileUpdate) {
        log.info("updateFile");
        if(ObjectUtils.isEmpty(fileUpdate)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(feedService.updateFile(fileUpdate));
    }

    @ApiOperation(value = "피드 사진 추가", notes = "사진 여러장 추가 가능")
    @PutMapping(value = "/addFile",consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<FeedDto> addFile(@RequestBody @Valid FeedFile feedFile) {
        log.info("addFile");
        if(ObjectUtils.isEmpty(feedFile)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(feedService.addFile(feedFile));
    }

    @ApiOperation(value = "피드 상세 페이지")
    @GetMapping("/detail/{id}")
    public ResponseEntity<FeedCommentDto> getDetailFeed(@PathVariable("id") String id) {
        log.info("getDetailFeed");
        return ResponseEntity.ok(feedService.getDetailFeed(id));
    }

    @ApiOperation(value = "피드 수정")
    @PutMapping
    public ResponseEntity<FeedDto> updateFeed(@RequestBody @Valid FeedUpdate feedUpdate){
        log.info("updateFeed");
        if(ObjectUtils.isEmpty(feedUpdate)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(feedService.updateFeed(feedUpdate));

    }

    @ApiOperation(value = "피드 삭제")
    @DeleteMapping("/{id}/{userId}")
    public ResponseEntity<String> deleteFeed(@PathVariable("id") String id, @PathVariable Long userId) {
        log.info("deleteFeed");
        feedService.deleteFeed(id,userId);
        return ResponseEntity.ok("success");
    }

    @ApiOperation(value = "나의 팔로워 최신순 피드 조회", notes = "나의 userId 값 전달")
    @GetMapping("/followerLatest/{userId}")
    public ResponseEntity<List<FeedDto>> getFollowerLatestFeed(@PathVariable("userId") Long userId){
        log.info("getFollowerFeed");
        return ResponseEntity.ok(feedService.getFollowerLatestFeed(userId));
    }

    @ApiOperation(value = "나의 팔로워 좋아요순 피드 조회 피드 ", notes = "나의 userId 값 전달")
    @GetMapping("/followerLike/{userId}")
    public ResponseEntity<List<FeedDto>> getFollowerLikeFeed(@PathVariable("userId") Long userId){
        log.info("getFollowerLikeFeed");
        return ResponseEntity.ok(feedService.getFollowerLikeFeed(userId));
    }

    @ApiOperation(value = "나의 피드 최신순 프로필용")
    @GetMapping("/person/{userId}")
    public ResponseEntity<List<FeedDto>> getPersonalFeed(@PathVariable("userId") Long userId){
        log.info("getPersonalFeed");
        return ResponseEntity.ok(feedService.getPersonalFeed(userId));
    }


    @ApiOperation(value = "전체 최신순 피드 조회")
    @GetMapping("/latest")
    public ResponseEntity<List<FeedDto>> getLatestFeed(@PageableDefault(sort="createdDate",
                                                            direction = Sort.Direction.DESC) Pageable pageable){
        log.info("getLatestFeed");
        return ResponseEntity.ok(feedService.getLatestFeed(pageable));
    }

    @ApiOperation(value = "전체 좋아요순 피드 조회")
    @GetMapping("/like")
    public ResponseEntity<List<FeedDto>> getLikesFeed(@PageableDefault(sort="likes",
                                                            direction = Sort.Direction.DESC) Pageable pageable){
        log.info("getLikesFeed");
        return ResponseEntity.ok(feedService.getLikeFeed(pageable));
    }

    @ApiOperation(value = "좋아요 등록 취소")
    @PutMapping("/addLike")
    public ResponseEntity<String> likeFeed(@RequestBody @Valid FeedLike feedLike){
        log.info("likeFeed");
        if(ObjectUtils.isEmpty(feedLike)){
            return ResponseEntity.notFound().build();
        }
        feedService.likeFeed(feedLike);
        return ResponseEntity.ok("success");
    }

    @ApiOperation(value = "게시글의 좋아요를 누른 유저들의 프로필, 피드 id")
    @GetMapping("/likeUser/{id}")
    public ResponseEntity<List<UserDetailProfile>> getLikeUserList(@PathVariable("id") String id){
        log.info("likeUserList");

        return ResponseEntity.ok(feedService.getLikeUserList(id));
    }

    @ApiOperation(value = "내용 및 해시태그 검색 창", notes = "해시태그 검색은 #내용, 내용검색은 내용만 보내면 됨")
    @GetMapping("/search")
    public ResponseEntity<FeedSearch> getSearchList(@PageableDefault(size = 30,sort = "createdDate",
                                                            direction = Sort.Direction.DESC) Pageable pageable, @RequestParam("content") String content){
        log.info("getSearchList");

        return ResponseEntity.ok(feedService.getSearchList(pageable,content));
    }
}