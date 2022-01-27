package com.ssafy.sunin.repository;

import com.ssafy.sunin.domain.FeedCollections;
import com.ssafy.sunin.domain.QFeedCollections;
import com.ssafy.sunin.dto.FeedList;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.QuerydslRepositorySupport;
import java.util.*;

public class FeedRepositoryImpl extends QuerydslRepositorySupport implements FeedRepositoryCustom {

    private static final QFeedCollections qfeed = QFeedCollections.feedCollections;

    public FeedRepositoryImpl(MongoOperations operations){
        super(operations);
    }

    @Override
    public List<FeedCollections> getFollowerFeed(List<String> followList) {
        // db.feed.find({userId:{$in:["qweqwe","asdf"]}});
        return from(qfeed)
                .where(qfeed.userId.in(followList))
                .orderBy(qfeed.createdDate.desc())
                .fetch();
    }

    @Override
    public List<FeedCollections> getLatestFeed(FeedList feedList) {
        return from(qfeed)
                .where(qfeed.userId.eq(feedList.getUserId()).and(qfeed.flag.eq(true)))
                .orderBy(qfeed.createdDate.desc())
                .offset(feedList.getPageNum())
                .limit(feedList.getSize())
                .fetch();
    }
    @Override
    public List<FeedCollections> getLikeFeed(FeedList feedList) {
        return from(qfeed)
                .where(qfeed.userId.eq(feedList.getUserId()).and(qfeed.flag.eq(true)))
                .orderBy(qfeed.likes.desc())
                .offset(feedList.getPageNum())
                .limit(feedList.getSize())
                .fetch();
    }

}