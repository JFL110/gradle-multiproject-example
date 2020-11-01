package dev.jamesleach.example.dynamo;

import org.junit.ClassRule;
import org.junit.Test;

public class TestDemoDynamoTables {

    @ClassRule
    public static final DynamoDBTestingRule dynamoRule = new DynamoDBTestingRule();

    @Test
    public void test(){

    }
}
