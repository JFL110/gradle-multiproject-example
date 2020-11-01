package dev.jamesleach.example.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

import java.util.function.Supplier;

public interface DynamoSupplier extends Supplier<AmazonDynamoDB> { }
