package com.aws.document;

import java.util.List;

import javax.persistence.Transient;

import org.springframework.data.mongodb.core.mapping.Document;

import com.amazonaws.services.resourcegroupstaggingapi.model.ComplianceDetails;
import com.amazonaws.services.resourcegroupstaggingapi.model.Tag;

import lombok.Data;

@Data
@Document(collection = "AwsResourcesDocument")
public class AwsResourcesDocument {

    @Transient
    public static final String SEQUENCE_NAME = "awsResourcesDocumentsequence";

    private Long id;

    private String accountId;

    // ResourceTagMapping

    private String region;

    private String resourceARN;

    private List<Tag> tags;

    private ComplianceDetails complianceDetails;
    
    private long amigoId;

}
