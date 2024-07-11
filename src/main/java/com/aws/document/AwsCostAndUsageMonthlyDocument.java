package com.aws.document;

import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.data.mongodb.core.mapping.Document;

import com.amazonaws.services.costexplorer.model.DateInterval;
import com.amazonaws.services.costexplorer.model.Group;
import com.amazonaws.services.costexplorer.model.MetricValue;

import lombok.Data;

@Data
@Document(collection = "AwsCostAndUsageMonthly")
public class AwsCostAndUsageMonthlyDocument {

    @Transient
    public static final String SEQUENCE_NAME = "costAndUsageMonthlyDocumentsequence";

    @Id
    private Long id;

    private String accountId;

    private DateInterval timePeriod;

    private Map<String, MetricValue> total;

    private List<Group> groups;

    private Boolean estimated;
    
    private long amigoId;
    
}
