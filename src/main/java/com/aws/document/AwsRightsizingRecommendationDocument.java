package com.aws.document;

import javax.persistence.Transient;

import org.springframework.data.mongodb.core.mapping.Document;

import com.amazonaws.services.costexplorer.model.CurrentInstance;
import com.amazonaws.services.costexplorer.model.ModifyRecommendationDetail;
import com.amazonaws.services.costexplorer.model.TerminateRecommendationDetail;

import lombok.Data;

@Data
@Document(collection = "AwsRightsizingRecommendationDocument")
public class AwsRightsizingRecommendationDocument {

    @Transient
    public static final String SEQUENCE_NAME = "awsRightsizingRecommendationDocumentDocumentsequence";

    private Long id;

    private String accountId;

    private CurrentInstance currentInstance;

    private String rightsizingType;

    private ModifyRecommendationDetail modifyRecommendationDetail;

    private TerminateRecommendationDetail terminateRecommendationDetail;
    
    private long amigoId;
    
}
