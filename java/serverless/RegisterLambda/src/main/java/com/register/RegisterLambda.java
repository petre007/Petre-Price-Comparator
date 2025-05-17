package com.register;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.CognitoUserPoolPostConfirmationEvent;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;

import org.json.JSONObject;

public class RegisterLambda implements RequestHandler<CognitoUserPoolPostConfirmationEvent, CognitoUserPoolPostConfirmationEvent> {

    private final AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
    private final String topicArn = System.getenv("SNS_TOPIC_ARN");

    @Override
    public CognitoUserPoolPostConfirmationEvent handleRequest(CognitoUserPoolPostConfirmationEvent event, Context context) {
        JSONObject userInfo = new JSONObject();
        userInfo.put("username", event.getUserName());
        userInfo.put("email", event.getRequest().getUserAttributes().get("email"));
        userInfo.put("name", event.getRequest().getUserAttributes().get("name"));

        PublishRequest publishRequest = new PublishRequest()
                .withTopicArn(topicArn)
                .withMessage(userInfo.toString());

        snsClient.publish(publishRequest);

        return event; // Must return the event object
    }
}
