package com.aws.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.aws.utils.Constant;

public class CredentialsClient {

	public AWSStaticCredentialsProvider getCredentials() {
		AssumeRoleRequest assumeRole = new AssumeRoleRequest().withRoleArn(null)
				.withRoleSessionName(Constant.sessionName);
		AWSSecurityTokenService sts = AWSSecurityTokenServiceClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.build();
		Credentials credentials = sts.assumeRole(assumeRole).getCredentials();
		BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(credentials.getAccessKeyId(),
				credentials.getSecretAccessKey(), credentials.getSessionToken());
		return new AWSStaticCredentialsProvider(sessionCredentials);
	}

}
