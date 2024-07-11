package com.aws.document;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import org.springframework.data.mongodb.core.mapping.Document;

import com.amazonaws.services.budgets.model.Action;
import com.amazonaws.services.budgets.model.CalculatedSpend;
import com.amazonaws.services.budgets.model.CostTypes;
import com.amazonaws.services.budgets.model.Spend;
import com.amazonaws.services.budgets.model.Subscriber;
import com.amazonaws.services.budgets.model.TimePeriod;

import lombok.Data;

@Data
@Document(collection = "AwsBudget")
public class AwsBudgetDocument {

	@Transient
	public static final String SEQUENCE_NAME = "awsBudgetDocumentsequence";

	private Long id;

	private String budgetName;

	private Spend budgetLimit;

	private CostTypes costTypes;

	private Map<String, Spend> plannedBudgetLimits;

	private Map<String, List<String>> costFilters;

	private String timeUnit;

	private TimePeriod timePeriod;

	private CalculatedSpend calculatedSpend;

	private String budgetType;

	private Date lastUpdatedTime;

	private String accountId;

	private List<Action> actions;

	private List<Subscriber> subscriber;
	
	private long amigoId;

}