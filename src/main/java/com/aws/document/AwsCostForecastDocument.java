package com.aws.document;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.amazonaws.services.costexplorer.model.DateInterval;
import com.amazonaws.services.costexplorer.model.MetricValue;

import lombok.Data;

@Data
@Document(collection = "AwsCostForecast")
public class AwsCostForecastDocument {

	@Transient
	public static final String SEQUENCE_NAME = "awsCostForecastDocumentsequence";

	private Long id;

	private MetricValue totalCost;

	private DateInterval timePeriod;

	private String meanValue;

	private String predictionIntervalLowerBound;

	private String predictionIntervalUpperBound;

	private String accountId;
	
	private long amigoId;

}
