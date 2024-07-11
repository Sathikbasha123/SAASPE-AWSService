package com.aws.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.aws.document.AwsResourcesDocument;

public interface AwsResourcesDocumentRepository extends MongoRepository<AwsResourcesDocument, Long> {

    @Query("{'accountId' : ?0}")
    AwsResourcesDocument findByAccountId(String accountId);

    @Query("{'resourceARN' : ?0}")
    AwsResourcesDocument findByResourceARN(String resourceARN);

}
