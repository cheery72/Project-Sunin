package com.ssafy.sunin.controller;

import com.ssafy.sunin.domain.Comment;
import com.ssafy.sunin.service.CommentService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RestControllerAdvice
@RequestMapping(value = "/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /*
    * feedId 현재 Integer로 받고 있는데
    * 이거 ObjectId로 바꿔야함 !!!
    * */

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<String> IllegalArgumentException() {
        String msg = "예외 발생 !! (IllegalArgumentException)";
        return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping
    @ApiOperation(value = "댓글 작성하기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "feedId", value = "(#개발용#) 피드 ObjectId", required = true),
            @ApiImplicitParam(name = "writer", value = "작성자", required = true),
            @ApiImplicitParam(name = "content", value = "내용", required = true)
    })
    public ResponseEntity<String> writeComment(int feedId, String writer, String content) {
        Comment result = commentService.writeComment(feedId, writer, content);
        if(result == null) return new ResponseEntity<>("등록 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        else return new ResponseEntity<>("등록 성공", HttpStatus.CREATED);
    }

    @PutMapping
    @ApiOperation(value="댓글 수정하기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "수정할 댓글 ObjectID", required = true),
            @ApiImplicitParam(name = "content", value = "내용", required = true)
    })
    public ResponseEntity<String> updateComment(String commentId, String content) {
        Comment result = commentService.updateComment(commentId, content);
        if(result == null) return new ResponseEntity<>("수정 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        else return new ResponseEntity<>("수정 성공", HttpStatus.OK);
    }

    @DeleteMapping
    @ApiOperation(value="댓글 삭제하기")
    @ApiImplicitParam(name = "commentId", value = "삭제할 댓글 ObjectID", required = true)
    public ResponseEntity<Comment> deleteComment(String commentId) {
        return new ResponseEntity<>(commentService.deleteComment(commentId), HttpStatus.OK);
    }

//    @GetMapping("/one")
//    @ApiOperation(value = "피드에 달린 댓글 개별 조회")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "feedId", value = "피드 ObjectId", required = true),
//            @ApiImplicitParam(name = "commentId", value = "댓글 ObjectId", required = true)
//    })
//    public ResponseEntity<Comment> findCommentByFeed(int feedId, String commentId) {
//        return new ResponseEntity<>(commentService.findCommentById(feedId, commentId), HttpStatus.OK);
//    }

    @GetMapping("/all")
    @ApiOperation(value = "피드에 달린 댓글 전체 조회")
    @ApiImplicitParam(name = "feedId", value = "피드 ObjectId", required = true)
    public ResponseEntity<List<Comment>> findCommentsByFeed(int feedId) {
        return new ResponseEntity<>(commentService.findCommentsByFeed(feedId), HttpStatus.OK);
    }

    @GetMapping("/count")
    @ApiOperation(value = "피드에 달린 댓글 갯수 조회")
    @ApiImplicitParam(name = "feedId", value = "피드 ObjectId", required = true)
    public ResponseEntity<Long> countCommentsByFeed(int feedId) {
        return new ResponseEntity<>(commentService.countCommentsByFeed(feedId), HttpStatus.OK);
    }

    @PostMapping("/reply")
    @ApiOperation(value = "대댓글 작성하기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "feedId", value = "(#개발용#) 피드 ObjectId", required = true),
            @ApiImplicitParam(name = "commentId", value = "(#개발용#) 루트댓글 ObjectId", required = true),
            @ApiImplicitParam(name = "writer", value = "작성자", required = true),
            @ApiImplicitParam(name = "content", value = "내용", required = true)
    })
    public ResponseEntity<Comment> writeReply(int feedId, String commentId, String writer, String content) {
        return new ResponseEntity<>(commentService.writeReply(feedId, commentId, writer, content), HttpStatus.OK);
    }

    @PutMapping("/reply")
    @ApiOperation(value="대댓글 수정하기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "수정할 대댓글 ObjectID", required = true),
            @ApiImplicitParam(name = "content", value = "내용", required = true)
    })
    public ResponseEntity<Comment> updateReply(String commentId, String content) {
        return new ResponseEntity<>(commentService.updateComment(commentId, content), HttpStatus.OK);
    }

    @DeleteMapping("/reply")
    @ApiOperation(value="대댓글 삭제하기")
    @ApiImplicitParam(name = "commentId", value = "삭제할 대댓글 ObjectID", required = true)
    public ResponseEntity<Comment> deleteReply(String commentId) {
        return new ResponseEntity<>(commentService.deleteComment(commentId), HttpStatus.OK);
    }

}
