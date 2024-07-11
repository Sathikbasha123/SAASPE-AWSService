package com.aws.controller;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aws.model.BillingResponse;
import com.aws.model.CommonResponse;
import com.aws.service.AwsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1/aws")
public class AwsController {

	@Autowired
	private AwsService awsService;

	@Scheduled(cron = "0 0 9 1/2 * ?", zone = "IST")
	public ResponseEntity<CommonResponse> getCostAndUsage() {
		CommonResponse commonResponse = new CommonResponse();
		try {
			commonResponse = awsService.getCostAndUsage();
			log.info("====getCostAndUsage method success====" + LocalDate.now(ZoneId.systemDefault()));
			return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
			log.error("====getCostAndUsage method failed====", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@Scheduled(cron = "0 20 14 1/2 * ?", zone = "IST")
	public ResponseEntity<CommonResponse> getCostAndUsageMonthly() {
		CommonResponse commonResponse = new CommonResponse();
		try {
			commonResponse = awsService.getCostAndUsageMonthly();
			log.info("====getCostAndUsageMonthly method success====" + LocalDate.now(ZoneId.systemDefault()));
			return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
			log.error("====getCostAndUsageMonthly method failed====", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@Scheduled(cron = "0 0 9 1/2 * ?", zone = "IST")
	public ResponseEntity<CommonResponse> getForecast() {
		CommonResponse commonResponse = new CommonResponse();
		try {
			commonResponse = awsService.getForecast();
			log.info("====getForecast method success====" + LocalDate.now(ZoneId.systemDefault()));
			return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
			log.info("====getForecast method success====", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@Scheduled(cron = "0 0 9 1/2 * ?", zone = "IST")
	@GetMapping("/b")
	public ResponseEntity<CommonResponse> getBudgets() {
		CommonResponse commonResponse = new CommonResponse();
		try {
			commonResponse = awsService.getBudgets();
			log.info("====getBudgets method success====" + LocalDate.now(ZoneId.systemDefault()));
			return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
			log.info("====getBudgets method success====", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@Scheduled(cron = "0 0 9 1/2 * ?", zone = "IST")
	public ResponseEntity<CommonResponse> getRightsizingRecommendation() {
		CommonResponse commonResponse = new CommonResponse();
		try {
			commonResponse = awsService.getRightsizingRecommendation();
			log.info("====getRightsizingRecommendation method success====" + LocalDate.now(ZoneId.systemDefault()));
			return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
			log.info("====getRightsizingRecommendation method failed====", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@Scheduled(cron = "0 0 9 1/2 * ?", zone = "IST")
	public ResponseEntity<CommonResponse> getResources() {
		CommonResponse commonResponse = new CommonResponse();
		try {
			commonResponse = awsService.getResources();
			log.info("====getResources method success====");
			return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
			log.info("====getResources method failed====", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// @GetMapping("/get/anomalies")
	public ResponseEntity<BillingResponse> getAnomalies() {
		try {
			BillingResponse response = awsService.getAnomalies();
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// @GetMapping("/get/anomalie/monitors")
	public ResponseEntity<BillingResponse> getAnomalyMonitor() {
		try {
			BillingResponse response = awsService.getAnomalyMonitor();
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// @GetMapping("/get/anomalies/subscription/request")
	public ResponseEntity<BillingResponse> getAnomalySubscriptionsRequest() {
		try {
			BillingResponse response = awsService.getAnomalySubscriptionsRequest();
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// @GetMapping("/get/cost/usage/with/resources")
	public ResponseEntity<BillingResponse> getCostAndUsageWithResources() {
		try {
			BillingResponse response = awsService.getCostAndUsageWithResources();
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// @GetMapping("/get/cost/category")
	public ResponseEntity<BillingResponse> getCostCategories() {
		try {
			BillingResponse response = awsService.getCostCategories();
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// @GetMapping("/get/dimension/values")
	public ResponseEntity<BillingResponse> getDimensionValues() {
		try {
			BillingResponse response = awsService.getDimensionValues();
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// @GetMapping("/get/reservation/coverage")
	public ResponseEntity<BillingResponse> getReservationCoverage() {
		try {
			BillingResponse response = awsService.getReservationCoverage();
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// @GetMapping("/get/reservation/utilization")
	public ResponseEntity<BillingResponse> getReservationUtilization() {
		try {
			BillingResponse response = awsService.getReservationUtilization();
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@Scheduled(cron = "0 */15 * ? * *")
	public void initialHit() {
		log.info("initialHit start");
		awsService.initialHit();
		log.info("initialHit End");
	}
}
