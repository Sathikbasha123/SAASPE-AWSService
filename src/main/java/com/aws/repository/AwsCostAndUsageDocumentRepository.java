package com.aws.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.aws.document.AwsCostAndUsageDocument;

public interface AwsCostAndUsageDocumentRepository extends MongoRepository<AwsCostAndUsageDocument, Long> {

    @Query("{'accountId' : :#{#accountId}, 'timePeriod.start' : :#{#start}}")
    AwsCostAndUsageDocument findByAccountId(String accountId ,String start);

}
