package com.ssafy.sunin.service;

import com.ssafy.sunin.domain.Comment;
import com.ssafy.sunin.domain.FeedCollections;
import com.ssafy.sunin.payload.request.comment.*;
import com.ssafy.sunin.payload.response.comment.CommentProfile;
import com.ssafy.sunin.repository.FeedRepository;
import com.ssafy.sunin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    /*
     * 댓글 작성하기
     * */
    public FeedCollections writeComment(CommentWrite commentWrite) {
        ObjectId objectId = new ObjectId();

        Comment comment = Comment.commentWriter(commentWrite,objectId);
        FeedCollections feedCollections = feedRepository.findFeedIdByIdAndFlagTrue(new ObjectId(commentWrite.getFeedId()));
        Map<Object,Comment> comments = feedCollections.getComments();
        comments.put(objectId,comment);
        comment.setCommentGroup(objectId);
        feedCollections.setCommentWrite(comments);

        return feedRepository.save(feedCollections);
    }

    /*
     * 댓글 수정하기
     * */
    public Comment updateComment(CommentUpdate commentUpdate) {
        // 피드가져오기
        FeedCollections feedCollections = feedRepository.findFeedIdByIdAndFlagTrue(new ObjectId(commentUpdate.getFeedId()));
        if(feedCollections.getComments().get(commentUpdate.getCommentId()).getWriter().equals(commentUpdate.getWriter())){
            // 변경하고자하는 해당 댓글
            feedCollections.getComments().get(commentUpdate.getCommentId()).setCommentModified(commentUpdate.getContent());
            feedRepository.save(feedCollections);
            return feedCollections.getComments().get(commentUpdate.getCommentId());
        }
        return null;
    }

    /*
     * 댓글 삭제하기
     * */
    public Comment deleteComment(CommentDelete commentDelete) {
        FeedCollections feedCollections = feedRepository.findFeedIdByIdAndFlagTrue(new ObjectId(commentDelete.getFeedId()));
        if(feedCollections.getComments().get(commentDelete.getCommentId()).getWriter().equals(commentDelete.getWriter())) {
            feedCollections.getComments().get(commentDelete.getCommentId()).setCommentDeleted();
            feedRepository.save(feedCollections);
            return feedCollections.getComments().get(commentDelete.getCommentId());
        }

        return null;
    }

    /*
     * 대댓글 작성하기
     * */
    public Comment writeReply(CommentReply commentReply) {
        ObjectId objectId = new ObjectId();
        Comment comment = Comment.commentReply(objectId,commentReply);
        FeedCollections feedCollections = feedRepository.findFeedIdByIdAndFlagTrue(new ObjectId(commentReply.getFeedId()));
        Map<Object,Comment> comments = feedCollections.getComments();
        comments.put(objectId,comment);
        comment.setCommentDepth(new ObjectId(commentReply.getCommentId()));
        feedCollections.setCommentWrite(comments);
        feedRepository.save(feedCollections);
        return comment;
    }

    /*
     * 피드에 달린 댓글 리스트
     * */
    public Map<Object,Comment> findCommentsByFeed(String feedId) {
//        List<Order> orders = new ArrayList<>();
//        orders.add(new Order(Direction.ASC, "group"));
//        orders.add(new Order(Direction.ASC, "order"));
//        return feedRepository.findFeedSortIdByIdAndFlagTrue(new ObjectId(feedId), Sort.by(orders)).getComments();
        return null;
    }

    /*
     * 피드에 달린 댓글 갯수
     * */
    public int countCommentsByFeed(String feedId) {
        FeedCollections feedCollections = feedRepository.findFeedIdByIdAndFlagTrue(new ObjectId(feedId));

        return feedCollections.getComments().size();
    }

    public void likeComment(CommentLike commentLike){
        FeedCollections feedCollections = feedRepository.findByIdAndFlagTrue(new ObjectId(commentLike.getFeedId()));
        Map<Object,Comment> comments = feedCollections.getComments();
        Comment comment = comments.get(commentLike.getCommentId());
        Map<Long, Object> users = new HashMap<>(comment.getLikeUser());

        int like = comment.getLikes();

        if (users.containsKey(commentLike.getUserId())) {
            users.remove(commentLike.getUserId());
            like--;
        } else {
            users.put(commentLike.getUserId(), true);
            like++;
        }

        comment.setCommentLikeModified(like,users);

        feedCollections.setCommentLikeUsers(comments);
        feedRepository.save(feedCollections);
    }

    public List<CommentProfile> getLikeUserList(String feedId, String commentId){
        FeedCollections feedCollections = feedRepository.findByIdAndFlagTrue(new ObjectId(feedId));
        Comment comment = feedCollections.getComments().get(commentId);
        Set<Long> list = comment.getLikeUser().keySet();

        return userRepository.findFollowerSetByUserSeqIn(list)
                .stream()
                .map(user -> CommentProfile.builder()
                        .id(user.getUserSeq())
                        .nickName(user.getUserNickname())
                        .image(user.getProfileImageUrl())
                        .build()).collect(Collectors.toList());
    }

    /*
     * 개발용
     * */
    public List<String> loadAllComment() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(Direction.ASC, "group"));
        orders.add(new Order(Direction.ASC, "order"));
//        List<Comment> list = commentRepository.findAll(Sort.by(orders));

        List<String> result = new ArrayList<>();
//        for(Comment now : list) result.add(now.toString());
//        return result;
        return null;
    }

    public void resetComments() {

//        commentRepository.deleteAll();
    }
}