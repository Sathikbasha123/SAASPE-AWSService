package com.aws.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.aws.document.AwsBudgetDocument;

public interface AwsBudgetDocumentRepository extends MongoRepository<AwsBudgetDocument, Long> {

    @Query("{ 'budgetName' : ?0 }")
	AwsBudgetDocument findByBudgetName(String budgetName);

}
