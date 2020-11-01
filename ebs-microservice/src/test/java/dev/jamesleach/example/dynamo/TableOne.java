package dev.jamesleach.example.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "TestTableOne")
public class TableOne {

    private String key;
    private String value;

    public TableOne() {
    }


    TableOne(String key, String value) {
        this.key = key;
        this.value = value;
    }


    @DynamoDBHashKey(attributeName = "key")
    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }


    @DynamoDBAttribute(attributeName = "val")
    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }
}