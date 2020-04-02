package wabilytics;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class LambdaLoggerMock implements LambdaLogger {
    @Override
    public void log(String message) {
        System.out.println(String.format("LambdaLoggerMock: %s", message));
    }

    @Override
    public void log(byte[] message) {
        this.log(new String(message));
    }
}
