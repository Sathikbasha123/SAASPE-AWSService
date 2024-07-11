package com.aws.service;

import com.aws.model.BillingResponse;
import com.aws.model.CommonResponse;

public interface AwsService {

	CommonResponse getCostAndUsage();

	CommonResponse getForecast();

	BillingResponse getAnomalies();

	BillingResponse getAnomalySubscriptionsRequest();

	BillingResponse getAnomalyMonitor();

	BillingResponse getCostAndUsageWithResources();

	BillingResponse getCostCategories();

	BillingResponse getDimensionValues();

	BillingResponse getReservationCoverage();

	CommonResponse getRightsizingRecommendation();

	BillingResponse getReservationUtilization();

	CommonResponse getBudgets();

	CommonResponse getResources();

	CommonResponse getCostAndUsageMonthly();

    void initialHit();
}
