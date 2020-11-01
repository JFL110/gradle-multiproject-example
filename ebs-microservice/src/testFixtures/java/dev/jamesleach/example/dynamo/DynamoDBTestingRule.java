package dev.jamesleach.example.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.google.common.collect.ImmutableList;
import org.junit.rules.ExternalResource;

/**
 * Creates a local DynamoDB instance for testing.
 */
public class DynamoDBTestingRule extends ExternalResource {

    private AmazonDynamoDB amazonDynamoDB;
    private final ImmutableList<Class<?>> clazzes;
    private final String prefix;

    public DynamoDBTestingRule(Class<?>... clazzes) {
        this("", clazzes);
    }

    public DynamoDBTestingRule(String prefix, Class<?>... clazzes) {
        System.setProperty("sqlite4java.library.path", "build/libs");
        this.clazzes = ImmutableList.copyOf(clazzes);
        this.prefix = prefix;
    }


    @Override
    protected void before() throws Throwable {
        amazonDynamoDB = DynamoDBEmbedded.create().amazonDynamoDB();

        clazzes.forEach(c -> {
//            GlobalSecondaryIndexSchema gsiAnnotation = c.getAnnotation(GlobalSecondaryIndexSchema.class);
//            List<GlobalSecondaryIndex> gsis = gsiAnnotation == null ? ImmutableList.of()
//                    : Guice.createInjector().getInstance(gsiAnnotation.value()).get();

            amazonDynamoDB.createTable(new DynamoDBMapper(amazonDynamoDB, DynamoDBMapperConfig.builder()
                    .withTableNameOverride(TableNameOverride.withTableNamePrefix(prefix))
                    .build()).generateCreateTableRequest(c)
//                    .withGlobalSecondaryIndexes(gsis.isEmpty() ? null : gsis)
                    .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L)));
        });
    }


    AmazonDynamoDB getAmazonDynamoDB() {
        return amazonDynamoDB;
    }
}