package org.kie.kogito.index.test.quarkus.http;

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.index.test.containers.DataIndexMongoDBContainer;
import org.kie.kogito.test.resources.TestResource;
import org.kie.kogito.testcontainers.KogitoMongoDBContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;

public class DataIndexMongoDBHttpResource implements TestResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataIndexMongoDBHttpResource.class);

    KogitoMongoDBContainer mongodb = new KogitoMongoDBContainer();
    DataIndexMongoDBContainer dataIndex = new DataIndexMongoDBContainer();
    Map<String, String> properties = new HashMap<>();

    @Override
    public String getResourceName() {
        return dataIndex.getResourceName();
    }

    @Override
    public void start() {
        LOGGER.debug("Start MongoDB Quarkus test resource");
        properties.clear();
        Network network = Network.newNetwork();
        mongodb.withNetwork(network);
        mongodb.withNetworkAliases("mongo");
        mongodb.start();
        properties.put("quarkus.mongodb.connection-string", mongodb.getReplicaSetUrl());

        dataIndex.addProtoFileFolder();
        dataIndex.withNetwork(network);
        dataIndex.setMongoDBURL("mongodb://mongo:27017/test");
        dataIndex.addEnv("QUARKUS_PROFILE", "http-events-support");
        dataIndex.start();
        LOGGER.debug("Data Index Service started");
    }

    @Override
    public void stop() {
        dataIndex.stop();
        mongodb.stop();
        LOGGER.debug("Stop MongoDB Quarkus test resource");
    }

    @Override
    public int getMappedPort() {
        return dataIndex.getMappedPort();
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
