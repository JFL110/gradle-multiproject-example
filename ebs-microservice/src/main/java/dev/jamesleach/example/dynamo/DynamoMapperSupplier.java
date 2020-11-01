package dev.jamesleach.example.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.IDynamoDBMapper;

import java.util.function.Supplier;

/**
 * The applications gateway to DynamoDB
 */
public class DynamoMapperSupplier implements Supplier<IDynamoDBMapper> {

    private final DynamoSupplier dynamoSupplier;

    DynamoMapperSupplier(DynamoSupplier dynamoSupplier) {
        this.dynamoSupplier = dynamoSupplier;
    }

    @Override
    public IDynamoDBMapper get() {
        DynamoDBMapperConfig dynamoDBMapperConfig = DynamoDBMapperConfig.builder()
//                .withTableNameOverride(TableNameOverride.withTableNamePrefix(appTableNamePrefixSupplier.get()))
                .build();
        return new DynamoDBMapper(dynamoSupplier.get(), dynamoDBMapperConfig);
    }
}
