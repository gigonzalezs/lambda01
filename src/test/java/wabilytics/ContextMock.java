package wabilytics;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class ContextMock implements Context {
    @Override
    public String getAwsRequestId() {
        return "mockRequestID";
    }

    @Override
    public String getLogGroupName() {
        return "mockLogGroupName";
    }

    @Override
    public String getLogStreamName() {
        return "mockLogStreamName";
    }

    @Override
    public String getFunctionName() {
        return "mockgetFunctionName";
    }

    @Override
    public String getFunctionVersion() {
        return "mockFunctionVersion";
    }

    @Override
    public String getInvokedFunctionArn() {
        return "mockInvokedFunctionArn";
    }

    @Override
    public CognitoIdentity getIdentity() {
        return null;
    }

    @Override
    public ClientContext getClientContext() {
        return new ClientContextMock();
    }

    @Override
    public int getRemainingTimeInMillis() {
        return 0;
    }

    @Override
    public int getMemoryLimitInMB() {
        return 0;
    }

    @Override
    public LambdaLogger getLogger() {
        return new LambdaLoggerMock();
    }
}
