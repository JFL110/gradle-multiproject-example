package dev.jamesleach.example.dynamo;

import org.junit.ClassRule;
import org.junit.Test;

public class TestDynamoRule {

    @ClassRule
    public static final DynamoDBTestingRule dynamoRule = new DynamoDBTestingRule(TableOne.class);

    @Test
    public void test(){
    }
}
