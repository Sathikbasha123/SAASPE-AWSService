package com.aws.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.aws.document.AwsCostAndUsageMonthlyDocument;

public interface AwsCostAndUsageMonthlyDocumentRepository extends MongoRepository<AwsCostAndUsageMonthlyDocument,Long>{

    @Query("{'accountId' : :#{#accountId}, 'timePeriod.start' : :#{#start}}")
    AwsCostAndUsageMonthlyDocument findByAccountId(String accountId ,String start);
    
}
