package dev.jamesleach.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LambdaFunctionHandler implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object input, Context context) {

//        ApplicationContext c = new AnnotationConfigApplicationContext(VersionProperties.class);
//
//        System.out.println(c.getBean(VersionProperties.class).getBuildTimeMillis());

        return "abc";
    }
}
