package dev.jamesleach.example.inttest;

import com.google.common.base.Stopwatch;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import org.junit.Assert;
import org.junit.rules.ExternalResource;

import java.util.concurrent.TimeUnit;

public class IntTestRule extends ExternalResource {

    private static final long WAIT_TIMEOUT_MS = 10000;
    private static final String ADDRESS_ENV = "APP_ADDRESS";
    private final Client client = ClientBuilder.newClient();

    public static String address() {
        return System.getenv(ADDRESS_ENV);
    }

    public WebTarget clientTarget(String path) {
        return client.target("http://" + address() + "/" + path);
    }


    @Override
    protected void before() throws Throwable {
        waitForNonError();
    }


    public void waitForNonError() {
        Stopwatch startTime = Stopwatch.createStarted();
        Exception lastException = null;
        while (startTime.elapsed(TimeUnit.MILLISECONDS) < WAIT_TIMEOUT_MS) {
            try {
                if (clientTarget("").request().get().getStatus() == 200) {
                    return;
                }
            } catch (Exception e) {
                lastException = e;
            }
        }
        if (lastException != null) {
            lastException.printStackTrace();
        }
        Assert.fail("Failed to wait " + WAIT_TIMEOUT_MS + "for app.");
    }
}
