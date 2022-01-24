package com.ssafy.sunin.repository;

import com.ssafy.sunin.domain.FeedCollections;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import java.util.List;

public interface FeedRepository extends MongoRepository<FeedCollections, ObjectId>,
                                        QuerydslPredicateExecutor<FeedCollections>,
        FeedRepositoryCustom {

    List<FeedCollections> findAllByUserId(String userId);

}