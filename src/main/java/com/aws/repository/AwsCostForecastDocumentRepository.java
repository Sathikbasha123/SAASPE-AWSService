package com.aws.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.aws.document.AwsCostForecastDocument;

@Repository
public interface AwsCostForecastDocumentRepository extends MongoRepository<AwsCostForecastDocument, Long> {

    //@Query("{'accountId' : ?0}")
    @Query("{'accountId' : :#{#accountId}, 'timePeriod.start' : :#{#start}}")
    AwsCostForecastDocument findByAccountId(String accountId ,String start);

}
