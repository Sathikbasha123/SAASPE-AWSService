package com.aws.service;

import java.time.LocalDate;
import java.time.Year;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.budgets.AWSBudgets;
import com.amazonaws.services.budgets.AWSBudgetsClientBuilder;
import com.amazonaws.services.budgets.model.Budget;
import com.amazonaws.services.budgets.model.DescribeBudgetsRequest;
import com.amazonaws.services.budgets.model.DescribeBudgetsResult;
import com.amazonaws.services.budgets.model.DescribeNotificationsForBudgetRequest;
import com.amazonaws.services.budgets.model.DescribeNotificationsForBudgetResult;
import com.amazonaws.services.budgets.model.DescribeSubscribersForNotificationRequest;
import com.amazonaws.services.budgets.model.DescribeSubscribersForNotificationResult;
import com.amazonaws.services.budgets.model.Notification;
import com.amazonaws.services.budgets.model.Subscriber;
import com.amazonaws.services.costexplorer.AWSCostExplorer;
import com.amazonaws.services.costexplorer.AWSCostExplorerClientBuilder;
import com.amazonaws.services.costexplorer.model.AnomalyDateInterval;
import com.amazonaws.services.costexplorer.model.Context;
import com.amazonaws.services.costexplorer.model.DateInterval;
import com.amazonaws.services.costexplorer.model.Dimension;
import com.amazonaws.services.costexplorer.model.DimensionValues;
import com.amazonaws.services.costexplorer.model.Expression;
import com.amazonaws.services.costexplorer.model.ForecastResult;
import com.amazonaws.services.costexplorer.model.GetAnomaliesRequest;
import com.amazonaws.services.costexplorer.model.GetAnomaliesResult;
import com.amazonaws.services.costexplorer.model.GetAnomalyMonitorsRequest;
import com.amazonaws.services.costexplorer.model.GetAnomalyMonitorsResult;
import com.amazonaws.services.costexplorer.model.GetAnomalySubscriptionsRequest;
import com.amazonaws.services.costexplorer.model.GetAnomalySubscriptionsResult;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageRequest;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageResult;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageWithResourcesRequest;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageWithResourcesResult;
import com.amazonaws.services.costexplorer.model.GetCostCategoriesRequest;
import com.amazonaws.services.costexplorer.model.GetCostCategoriesResult;
import com.amazonaws.services.costexplorer.model.GetCostForecastRequest;
import com.amazonaws.services.costexplorer.model.GetCostForecastResult;
import com.amazonaws.services.costexplorer.model.GetDimensionValuesRequest;
import com.amazonaws.services.costexplorer.model.GetDimensionValuesResult;
import com.amazonaws.services.costexplorer.model.GetReservationCoverageRequest;
import com.amazonaws.services.costexplorer.model.GetReservationCoverageResult;
import com.amazonaws.services.costexplorer.model.GetReservationUtilizationRequest;
import com.amazonaws.services.costexplorer.model.GetReservationUtilizationResult;
import com.amazonaws.services.costexplorer.model.GetRightsizingRecommendationRequest;
import com.amazonaws.services.costexplorer.model.GetRightsizingRecommendationResult;
import com.amazonaws.services.costexplorer.model.Granularity;
import com.amazonaws.services.costexplorer.model.GroupDefinition;
import com.amazonaws.services.costexplorer.model.Metric;
import com.amazonaws.services.costexplorer.model.RecommendationTarget;
import com.amazonaws.services.costexplorer.model.ResultByTime;
import com.amazonaws.services.costexplorer.model.RightsizingRecommendation;
import com.amazonaws.services.costexplorer.model.RightsizingRecommendationConfiguration;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPIAsyncClient;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesResult;
import com.amazonaws.services.resourcegroupstaggingapi.model.ResourceTagMapping;
import com.aws.configuration.SequenceGeneratorService;
import com.aws.document.AwsBudgetDocument;
import com.aws.document.AwsCostAndUsageDocument;
import com.aws.document.AwsCostAndUsageMonthlyDocument;
import com.aws.document.AwsCostForecastDocument;
import com.aws.document.AwsResourcesDocument;
import com.aws.document.AwsRightsizingRecommendationDocument;
import com.aws.entity.MultiCloudDetails;
import com.aws.model.BillingResponse;
import com.aws.model.CommonResponse;
import com.aws.model.Response;
import com.aws.repository.AwsBudgetDocumentRepository;
import com.aws.repository.AwsCostAndUsageDocumentRepository;
import com.aws.repository.AwsCostAndUsageMonthlyDocumentRepository;
import com.aws.repository.AwsCostForecastDocumentRepository;
import com.aws.repository.AwsResourcesDocumentRepository;
import com.aws.repository.MultiCloudDetailRepository;
import com.aws.repository.RightsizingRecommendationDocumentRepository;
import com.aws.utils.Constant;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AwsServiceImpl implements AwsService {

    @Autowired
    private AwsCostForecastDocumentRepository awsCostForecastDocumentRepository;

    @Autowired
    private AwsBudgetDocumentRepository awsBudgetDocumentRepository;

    @Autowired
    private AwsCostAndUsageDocumentRepository awsCostAndUsageDocumentRepository;

    @Autowired
    private RightsizingRecommendationDocumentRepository rightsizingRecommendationDocumentRepository;

    @Autowired
    private AwsCostAndUsageMonthlyDocumentRepository awsCostAndUsageMonthlyDocumentRepository;

    @Autowired
    private AwsResourcesDocumentRepository awsResourcesDocumentRepository;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private MultiCloudDetailRepository multiCloudDetailRepository;

    @Override
    public CommonResponse getCostAndUsage() {
        CommonResponse commonResponse = new CommonResponse();
        Response response = new Response();
        AwsCostAndUsageDocument awsCostAndUsageDocument = new AwsCostAndUsageDocument();
        MultiCloudDetails cloudDetails = multiCloudDetailRepository.findByProviderName("AWS");
        String startDate = Year.now().getValue() + "-01-01";
        LocalDate date = LocalDate.now();
        String endDate = date.toString();
        GetCostAndUsageRequest costAndUsageRequest = new GetCostAndUsageRequest()
                .withTimePeriod(new DateInterval().withStart(startDate).withEnd(endDate))
                .withGranularity(Granularity.MONTHLY).withMetrics("UNBLENDED_COST")
                .withGroupBy(new GroupDefinition().withType("DIMENSION").withKey("SERVICE"));
        AWSCredentials awsCredentials = new BasicAWSCredentials(cloudDetails.getAccessKeyId(),
                cloudDetails.getSecretAccessKey());
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AWSCostExplorer awsCostExplorer = AWSCostExplorerClientBuilder.standard()
                .withCredentials(awsStaticCredentialsProvider).withRegion(Regions.US_EAST_1).build();
        GetCostAndUsageResult costAndUsageResult = awsCostExplorer.getCostAndUsage(costAndUsageRequest);
        List<ResultByTime> resultByTimes = costAndUsageResult.getResultsByTime();
        for (ResultByTime resultByTime : resultByTimes) {
            AwsCostAndUsageDocument document = awsCostAndUsageDocumentRepository
                    .findByAccountId(cloudDetails.getTenantId(), resultByTime.getTimePeriod().getStart());
            if (document == null) {
                awsCostAndUsageDocument
                        .setId(sequenceGeneratorService.generateSequence(AwsCostAndUsageDocument.SEQUENCE_NAME));
                awsCostAndUsageDocument
                .setAmigoId(sequenceGeneratorService.generateSequence(Constant.SEQUENCE_NAME));
                awsCostAndUsageDocument.setAccountId(cloudDetails.getTenantId());
                awsCostAndUsageDocument.setEstimated(resultByTime.getEstimated());
                awsCostAndUsageDocument.setGroups(resultByTime.getGroups());
                awsCostAndUsageDocument.setTimePeriod(resultByTime.getTimePeriod());
                awsCostAndUsageDocument.setTotal(resultByTime.getTotal());
                awsCostAndUsageDocumentRepository.save(awsCostAndUsageDocument);
            } else {
                document.setAccountId(cloudDetails.getTenantId());
                document.setEstimated(resultByTime.getEstimated());
                document.setGroups(resultByTime.getGroups());
                document.setTimePeriod(resultByTime.getTimePeriod());
                document.setTotal(resultByTime.getTotal());
                awsCostAndUsageDocumentRepository.save(document);
            }
        }
        response.setAction(null);
        response.setData(awsCostAndUsageDocument);
        commonResponse.setMessage(null);
        commonResponse.setStatus(HttpStatus.OK);
        commonResponse.setResponse(response);
        return commonResponse;
    }

    @Override
    public CommonResponse getCostAndUsageMonthly() {
        CommonResponse commonResponse = new CommonResponse();
        Response response = new Response();
        AwsCostAndUsageMonthlyDocument awsCostAndUsageDocument = new AwsCostAndUsageMonthlyDocument();
        MultiCloudDetails cloudDetails = multiCloudDetailRepository.findByProviderName("AWS");
        String startDate = Year.now().getValue() + "-01-01";
        LocalDate date = LocalDate.now();
        String endDate = date.toString();
        GetCostAndUsageRequest costAndUsageRequest = new GetCostAndUsageRequest()
                .withTimePeriod(new DateInterval().withStart(startDate).withEnd(endDate))
                .withGranularity(Granularity.MONTHLY).withMetrics("UNBLENDED_COST");
        AWSCredentials awsCredentials = new BasicAWSCredentials(cloudDetails.getAccessKeyId(),
                cloudDetails.getSecretAccessKey());
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AWSCostExplorer awsCostExplorer = AWSCostExplorerClientBuilder.standard()
                .withCredentials(awsStaticCredentialsProvider).withRegion(Regions.US_EAST_1).build();
        GetCostAndUsageResult costAndUsageResult = awsCostExplorer.getCostAndUsage(costAndUsageRequest);
        List<ResultByTime> resultByTimes = costAndUsageResult.getResultsByTime();
        for (ResultByTime resultByTime : resultByTimes) {
            AwsCostAndUsageMonthlyDocument document = awsCostAndUsageMonthlyDocumentRepository
                    .findByAccountId(cloudDetails.getTenantId(), resultByTime.getTimePeriod().getStart());
            if (document == null) {
                awsCostAndUsageDocument
                        .setId(sequenceGeneratorService.generateSequence(AwsCostAndUsageMonthlyDocument.SEQUENCE_NAME));
                awsCostAndUsageDocument.setAmigoId(sequenceGeneratorService.generateSequence(Constant.SEQUENCE_NAME));
                awsCostAndUsageDocument.setAccountId(cloudDetails.getTenantId());
                awsCostAndUsageDocument.setEstimated(resultByTime.getEstimated());
                awsCostAndUsageDocument.setGroups(resultByTime.getGroups());
                awsCostAndUsageDocument.setTimePeriod(resultByTime.getTimePeriod());
                awsCostAndUsageDocument.setTotal(resultByTime.getTotal());
                awsCostAndUsageMonthlyDocumentRepository.save(awsCostAndUsageDocument);
            } else {
                document.setAccountId(cloudDetails.getTenantId());
                document.setEstimated(resultByTime.getEstimated());
                document.setGroups(resultByTime.getGroups());
                document.setTimePeriod(resultByTime.getTimePeriod());
                document.setTotal(resultByTime.getTotal());
                awsCostAndUsageMonthlyDocumentRepository.save(document);
            }
        }
        response.setAction(null);
        response.setData(awsCostAndUsageDocument);
        commonResponse.setMessage(null);
        commonResponse.setStatus(HttpStatus.OK);
        commonResponse.setResponse(response);
        return commonResponse;
    }

    @Override
    public void initialHit() {
        MultiCloudDetails multiCloudDetails = multiCloudDetailRepository.findByProviderName("AWS");
        log.info(multiCloudDetails.getProviderName());
        if (multiCloudDetails != null) {
            if (multiCloudDetails.getUpdatedOn() == null) {
                getCostAndUsage();
                getCostAndUsageMonthly();
                getForecast();
                getBudgets();
                getRightsizingRecommendation();
                getResources();
                multiCloudDetails.setUpdatedOn(new Date());
                multiCloudDetailRepository.save(multiCloudDetails);
            }
        }
    }

    @Override
    public CommonResponse getForecast() {
        AwsCostForecastDocument awsCostForecastDocument = new AwsCostForecastDocument();
        CommonResponse commonResponse = new CommonResponse();
        Response response = new Response();
        MultiCloudDetails cloudDetails = multiCloudDetailRepository.findByProviderName("AWS");
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(12);
        GetCostForecastRequest forecast = new GetCostForecastRequest()
                .withTimePeriod(new DateInterval().withStart(startDate.toString()).withEnd(endDate.toString()))
                .withGranularity(Granularity.MONTHLY).withMetric(Metric.UNBLENDED_COST).withPredictionIntervalLevel(80);
        AWSCostExplorer awsCostExplorer = AWSCostExplorerClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(cloudDetails.getAccessKeyId(), cloudDetails.getSecretAccessKey())))
                .withRegion(Regions.US_EAST_1).build();
        GetCostForecastResult ceResult = awsCostExplorer.getCostForecast(forecast);
        List<ForecastResult> forecastResults = ceResult.getForecastResultsByTime();
        for (ForecastResult result : forecastResults) {
            AwsCostForecastDocument document = awsCostForecastDocumentRepository
                    .findByAccountId(cloudDetails.getTenantId(), result.getTimePeriod().getStart());
            if (document == null) {
                awsCostForecastDocument
                        .setId(sequenceGeneratorService.generateSequence(AwsCostForecastDocument.SEQUENCE_NAME));
                awsCostForecastDocument.setAmigoId(sequenceGeneratorService.generateSequence(Constant.SEQUENCE_NAME));
                awsCostForecastDocument.setAccountId(cloudDetails.getTenantId());
                awsCostForecastDocument.setTotalCost(ceResult.getTotal());
                awsCostForecastDocument.setMeanValue(result.getMeanValue());
                awsCostForecastDocument.setPredictionIntervalLowerBound(result.getPredictionIntervalLowerBound());
                awsCostForecastDocument.setPredictionIntervalUpperBound(result.getPredictionIntervalUpperBound());
                awsCostForecastDocument.setTimePeriod(result.getTimePeriod());
                awsCostForecastDocumentRepository.save(awsCostForecastDocument);
            } else {
                document.setAccountId(cloudDetails.getTenantId());
                document.setTotalCost(ceResult.getTotal());
                document.setMeanValue(result.getMeanValue());
                document.setPredictionIntervalLowerBound(result.getPredictionIntervalLowerBound());
                document.setPredictionIntervalUpperBound(result.getPredictionIntervalUpperBound());
                document.setTimePeriod(result.getTimePeriod());
                awsCostForecastDocumentRepository.save(document);
            }
        }
        response.setAction(null);
        response.setData(awsCostForecastDocument);
        commonResponse.setMessage(null);
        commonResponse.setResponse(response);
        commonResponse.setStatus(HttpStatus.OK);
        return commonResponse;
    }

    @Override
    public CommonResponse getBudgets() {
        CommonResponse commonResponse = new CommonResponse();
        Response response = new Response();
        MultiCloudDetails cloudDetails = multiCloudDetailRepository.findByProviderName("AWS");
        AwsBudgetDocument awsBudgetDocument = new AwsBudgetDocument();
        AWSBudgets awsBudgets = AWSBudgetsClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(cloudDetails.getAccessKeyId(), cloudDetails.getSecretAccessKey())))
                .withRegion(Regions.US_EAST_1).build();
        DescribeBudgetsResult budgetsResult = awsBudgets
                .describeBudgets(new DescribeBudgetsRequest().withAccountId(cloudDetails.getTenantId()));
        List<Budget> budgets = budgetsResult.getBudgets();
        for (Budget budget : budgets) {
            AwsBudgetDocument document = awsBudgetDocumentRepository.findByBudgetName(budget.getBudgetName());

            DescribeNotificationsForBudgetResult describeNotificationsForBudgetRequest = awsBudgets
                    .describeNotificationsForBudget(
                            new DescribeNotificationsForBudgetRequest().withAccountId(cloudDetails.getTenantId())
                                    .withBudgetName(budget.getBudgetName()));
            List<Notification> notifications = describeNotificationsForBudgetRequest.getNotifications();
            for (Notification notification : notifications) {
                DescribeSubscribersForNotificationResult describeSubscribersForNotificationResult = awsBudgets
                        .describeSubscribersForNotification(
                                new DescribeSubscribersForNotificationRequest()
                                        .withAccountId(cloudDetails.getTenantId())
                                        .withBudgetName(budget.getBudgetName())
                                        .withNotification(new Notification()
                                                .withComparisonOperator(notification.getComparisonOperator())
                                                .withNotificationType(notification.getNotificationType())
                                                .withThreshold(notification.getThreshold())));
                List<Subscriber> subscribers = describeSubscribersForNotificationResult.getSubscribers();
                for (Subscriber subscriber : subscribers) {
                    subscriber.getAddress();
                    subscriber.getSubscriptionType();
                }
            }

            response.setData(notifications);
            if (document == null) {
                awsBudgetDocument.setId(sequenceGeneratorService.generateSequence(AwsBudgetDocument.SEQUENCE_NAME));
                awsBudgetDocument.setAmigoId(sequenceGeneratorService.generateSequence(Constant.SEQUENCE_NAME));
                awsBudgetDocument.setBudgetName(budget.getBudgetName());
                awsBudgetDocument.setBudgetLimit(budget.getBudgetLimit());
                awsBudgetDocument.setBudgetType(budget.getBudgetType());
                awsBudgetDocument.setCalculatedSpend(budget.getCalculatedSpend());
                awsBudgetDocument.setCostFilters(budget.getCostFilters());
                awsBudgetDocument.setCostTypes(budget.getCostTypes());
                awsBudgetDocument.setLastUpdatedTime(budget.getLastUpdatedTime());
                awsBudgetDocument.setPlannedBudgetLimits(budget.getPlannedBudgetLimits());
                awsBudgetDocument.setTimePeriod(budget.getTimePeriod());
                awsBudgetDocument.setTimeUnit(budget.getTimeUnit());
                awsBudgetDocument.setAccountId(cloudDetails.getTenantId());
                awsBudgetDocumentRepository.save(awsBudgetDocument);

            } else {
                document.setBudgetName(budget.getBudgetName());
                document.setBudgetLimit(budget.getBudgetLimit());
                document.setBudgetType(budget.getBudgetType());
                document.setCalculatedSpend(budget.getCalculatedSpend());
                document.setCostFilters(budget.getCostFilters());
                document.setCostTypes(budget.getCostTypes());
                document.setLastUpdatedTime(budget.getLastUpdatedTime());
                document.setPlannedBudgetLimits(budget.getPlannedBudgetLimits());
                document.setTimePeriod(budget.getTimePeriod());
                document.setTimeUnit(budget.getTimeUnit());
                document.setAccountId(cloudDetails.getTenantId());
                awsBudgetDocumentRepository.save(document);
            }
        }
        commonResponse.setResponse(response);
        return commonResponse;
    }

    @Override
    public CommonResponse getRightsizingRecommendation() {
        CommonResponse commonResponse = new CommonResponse();
        Response response = new Response();
        MultiCloudDetails cloudDetails = multiCloudDetailRepository.findByProviderName("AWS");
        AwsRightsizingRecommendationDocument rightsizingRecommendationDocument = new AwsRightsizingRecommendationDocument();
        GetRightsizingRecommendationRequest getRightsizingRecommendationRequest = new GetRightsizingRecommendationRequest()
                .withService("AmazonEC2")
                .withConfiguration(new RightsizingRecommendationConfiguration().withBenefitsConsidered(true)
                        .withRecommendationTarget(RecommendationTarget.SAME_INSTANCE_FAMILY));
        AWSCostExplorer ce = AWSCostExplorerClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(cloudDetails.getAccessKeyId(), cloudDetails.getSecretAccessKey())))
                .withRegion(Regions.US_EAST_1).build();
        GetRightsizingRecommendationResult result = ce
                .getRightsizingRecommendation(getRightsizingRecommendationRequest);
        List<RightsizingRecommendation> list = result.getRightsizingRecommendations();
        for (RightsizingRecommendation recommendation : list) {
            AwsRightsizingRecommendationDocument document = rightsizingRecommendationDocumentRepository
                    .findByAccountId(cloudDetails.getTenantId());
            if (document == null) {
                rightsizingRecommendationDocument.setId(
                        sequenceGeneratorService.generateSequence(AwsRightsizingRecommendationDocument.SEQUENCE_NAME));
                rightsizingRecommendationDocument.setAmigoId(sequenceGeneratorService.generateSequence(Constant.SEQUENCE_NAME));
                rightsizingRecommendationDocument.setAccountId(recommendation.getAccountId());
                rightsizingRecommendationDocument.setCurrentInstance(recommendation.getCurrentInstance());
                rightsizingRecommendationDocument
                        .setModifyRecommendationDetail(recommendation.getModifyRecommendationDetail());
                rightsizingRecommendationDocument.setRightsizingType(recommendation.getRightsizingType());
                rightsizingRecommendationDocument
                        .setTerminateRecommendationDetail(recommendation.getTerminateRecommendationDetail());
                rightsizingRecommendationDocumentRepository.save(rightsizingRecommendationDocument);
            } else {
                document.setAccountId(recommendation.getAccountId());
                document.setCurrentInstance(recommendation.getCurrentInstance());
                document.setModifyRecommendationDetail(recommendation.getModifyRecommendationDetail());
                document.setRightsizingType(recommendation.getRightsizingType());
                document.setTerminateRecommendationDetail(recommendation.getTerminateRecommendationDetail());
                rightsizingRecommendationDocumentRepository.save(document);
            }
        }
        response.setData(rightsizingRecommendationDocument);
        commonResponse.setResponse(response);
        return commonResponse;
    }

    @Override
    public CommonResponse getResources() {
        CommonResponse commonResponse = new CommonResponse();
        Response response = new Response();
        MultiCloudDetails cloudDetails = multiCloudDetailRepository.findByProviderName("AWS");
        AwsResourcesDocument awsResourcesDocument = new AwsResourcesDocument();
        AWSResourceGroupsTaggingAPI apiAsyncClient = AWSResourceGroupsTaggingAPIAsyncClient.builder()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(cloudDetails.getAccessKeyId(), cloudDetails.getSecretAccessKey())))
                .withRegion(Regions.AP_SOUTHEAST_1).build();
        GetResourcesRequest getResourcesRequest = new GetResourcesRequest();
        // getResourcesRequest.withExcludeCompliantResources(null);
        // getResourcesRequest.withIncludeComplianceDetails(true);
        // getResourcesRequest.withPaginationToken("1");
        // getResourcesRequest.setTagsPerPage(150);
        GetResourcesResult getResourcesResult = apiAsyncClient.getResources(getResourcesRequest);
        List<ResourceTagMapping> list = getResourcesResult.getResourceTagMappingList();
        for (ResourceTagMapping mapping : list) {
            AwsResourcesDocument document = awsResourcesDocumentRepository.findByResourceARN(mapping.getResourceARN());
            if (document == null) {
                awsResourcesDocument
                        .setId(sequenceGeneratorService.generateSequence(AwsResourcesDocument.SEQUENCE_NAME));
                awsResourcesDocument.setAmigoId(sequenceGeneratorService.generateSequence(Constant.SEQUENCE_NAME));
                awsResourcesDocument.setAccountId(cloudDetails.getTenantId());
                awsResourcesDocument.setRegion("AP_SOUTHEAST_1");
                awsResourcesDocument.setComplianceDetails(mapping.getComplianceDetails());
                awsResourcesDocument.setResourceARN(mapping.getResourceARN());
                awsResourcesDocument.setTags(mapping.getTags());
                awsResourcesDocumentRepository.save(awsResourcesDocument);
            } else {
                awsResourcesDocument.setAccountId(cloudDetails.getTenantId());
                awsResourcesDocument.setRegion("AP_SOUTHEAST_1");
                awsResourcesDocument.setComplianceDetails(mapping.getComplianceDetails());
                awsResourcesDocument.setResourceARN(mapping.getResourceARN());
                awsResourcesDocument.setTags(mapping.getTags());
                awsResourcesDocumentRepository.save(document);
            }
        }
        response.setData(getResourcesResult.getResourceTagMappingList());
        commonResponse.setResponse(response);
        return commonResponse;
    }

    @Override
    public BillingResponse getAnomalies() {
        MultiCloudDetails cloudDetails = multiCloudDetailRepository.findByProviderName("AWS");
        final GetAnomaliesRequest anomaly = new GetAnomaliesRequest()
                .withDateInterval(new AnomalyDateInterval().withEndDate("2022-03-31").withStartDate("2022-03-01"));
        BillingResponse response = new BillingResponse();
        AWSCostExplorer ce = AWSCostExplorerClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(cloudDetails.getAccessKeyId(), cloudDetails.getSecretAccessKey())))
                .build();
        GetAnomaliesResult ceResult = ce.getAnomalies(anomaly);
        response.setValue(ceResult);
        return response;
    }

    @Override
    public BillingResponse getAnomalyMonitor() {
        GetAnomalyMonitorsRequest anomalyMonitorsRequest = new GetAnomalyMonitorsRequest();
        MultiCloudDetails cloudDetails = multiCloudDetailRepository.findByProviderName("AWS");
        BillingResponse response = new BillingResponse();
        AWSCostExplorer ce = AWSCostExplorerClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(cloudDetails.getAccessKeyId(), cloudDetails.getSecretAccessKey())))
                .build();
        GetAnomalyMonitorsResult ceResult = ce.getAnomalyMonitors(anomalyMonitorsRequest);
        response.setValue(ceResult);
        return response;
    }

    @Override
    public BillingResponse getAnomalySubscriptionsRequest() {
        GetAnomalySubscriptionsRequest anomalySubscriptionsRequest = new GetAnomalySubscriptionsRequest();
        MultiCloudDetails cloudDetails = multiCloudDetailRepository.findByProviderName("AWS");
        BillingResponse response = new BillingResponse();
        AWSCostExplorer ce = AWSCostExplorerClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(cloudDetails.getAccessKeyId(), cloudDetails.getSecretAccessKey())))
                .build();
        GetAnomalySubscriptionsResult ceResult = ce.getAnomalySubscriptions(anomalySubscriptionsRequest);
        response.setValue(ceResult);
        return response;
    }

    @Override
    public BillingResponse getCostAndUsageWithResources() {
        MultiCloudDetails cloudDetails = multiCloudDetailRepository.findByProviderName("AWS");
        GetCostAndUsageWithResourcesRequest costAndUsageWithResourcesRequest = new GetCostAndUsageWithResourcesRequest()
                .withFilter(new Expression().withDimensions(new DimensionValues().withKey("SERVICE")
                        .withValues("Amazon Relational Database Service").withMatchOptions("EQUALS")))
                .withGranularity(Granularity.MONTHLY)
                .withTimePeriod(new DateInterval().withStart("2022-03-01").withEnd("2022-04-30"))
                .withGroupBy(new GroupDefinition().withType("DIMENSION").withKey("RESOURCE_ID"));
        BillingResponse response = new BillingResponse();
        AWSCostExplorer ce = AWSCostExplorerClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(cloudDetails.getAccessKeyId(), cloudDetails.getSecretAccessKey())))
                .build();
        GetCostAndUsageWithResourcesResult ceResult = ce.getCostAndUsageWithResources(costAndUsageWithResourcesRequest);
        response.setValue(ceResult);
        return response;
    }

    @Override
    public BillingResponse getCostCategories() {
        MultiCloudDetails cloudDetails = multiCloudDetailRepository.findByProviderName("AWS");
        GetCostCategoriesRequest costCategoriesRequest = new GetCostCategoriesRequest()
                .withTimePeriod(new DateInterval().withStart("2022-03-01").withEnd("2022-03-31"));
        BillingResponse response = new BillingResponse();
        AWSCostExplorer ce = AWSCostExplorerClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(cloudDetails.getAccessKeyId(), cloudDetails.getSecretAccessKey())))
                .build();
        GetCostCategoriesResult ceResult = ce.getCostCategories(costCategoriesRequest);
        response.setValue(ceResult);
        return response;
    }

    @Override
    public BillingResponse getDimensionValues() {
        MultiCloudDetails cloudDetails = multiCloudDetailRepository.findByProviderName("AWS");
        GetDimensionValuesRequest getDimensionValuesRequest = new GetDimensionValuesRequest()
                .withContext(Context.COST_AND_USAGE).withDimension(Dimension.LEGAL_ENTITY_NAME)
                .withTimePeriod(new DateInterval().withStart("2022-03-01").withEnd("2022-03-31"));
        BillingResponse response = new BillingResponse();
        AWSCostExplorer ce = AWSCostExplorerClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(cloudDetails.getAccessKeyId(), cloudDetails.getSecretAccessKey())))
                .build();
        GetDimensionValuesResult ceResult = ce.getDimensionValues(getDimensionValuesRequest);
        response.setValue(ceResult);
        return response;
    }

    @Override
    public BillingResponse getReservationCoverage() {
        GetReservationCoverageRequest getReservationCoverageRequest = new GetReservationCoverageRequest()
                .withTimePeriod(new DateInterval().withStart("2022-03-01").withEnd("2022-03-31"));
        MultiCloudDetails cloudDetails = multiCloudDetailRepository.findByProviderName("AWS");
        BillingResponse response = new BillingResponse();
        AWSCostExplorer ce = AWSCostExplorerClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(cloudDetails.getAccessKeyId(), cloudDetails.getSecretAccessKey())))
                .build();
        GetReservationCoverageResult ceResult = ce.getReservationCoverage(getReservationCoverageRequest);
        response.setValue(ceResult);
        return response;
    }

    @Override
    public BillingResponse getReservationUtilization() {
        MultiCloudDetails cloudDetails = multiCloudDetailRepository.findByProviderName("AWS");
        GetReservationUtilizationRequest reservationUtilizationRequest = new GetReservationUtilizationRequest()
                .withTimePeriod(new DateInterval().withStart("2022-03-01").withEnd("2022-06-09"))
                .withGroupBy(new GroupDefinition().withType("DIMENSION").withKey("SUBSCRIPTION_ID"));
        BillingResponse response = new BillingResponse();
        AWSCostExplorer ce = AWSCostExplorerClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(cloudDetails.getAccessKeyId(), cloudDetails.getSecretAccessKey())))
                .build();
        GetReservationUtilizationResult ceResult = ce.getReservationUtilization(reservationUtilizationRequest);
        response.setValue(ceResult);
        return response;
    }

}
