package com.ecommerce.config;

import java.util.Base64;

import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticsearchConfig {

    private static final String USERNAME = "elastic";
    private static final String PASSWORD = "8PEcY*3rJn=1jojn*iHR";

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        String credentials = Base64.getEncoder()
                .encodeToString((USERNAME + ":" + PASSWORD).getBytes());

        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200, "http"))
                .setDefaultHeaders(new BasicHeader[]{
                        new BasicHeader("Authorization", "Basic " + credentials)
                })
                .build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        return new ElasticsearchClient(transport);
    }
}
