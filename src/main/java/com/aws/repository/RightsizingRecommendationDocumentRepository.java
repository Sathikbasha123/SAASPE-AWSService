package com.aws.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.aws.document.AwsRightsizingRecommendationDocument;

public interface RightsizingRecommendationDocumentRepository
        extends MongoRepository<AwsRightsizingRecommendationDocument, Long> {

    @Query("{'accountId' : ?0}")
    AwsRightsizingRecommendationDocument findByAccountId(String accountId);

}
