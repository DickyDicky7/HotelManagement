package com.example.hasura;

import com.apollographql.apollo.ApolloClient;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Hasura {

    private static final Interceptor hasuraInterceptor = chain -> {
        Request request = chain.request().newBuilder()
                .addHeader
                        (
                                "content-type",
                                "application/json"
                        )
                .addHeader
                        (
                                "x-hasura-admin-secret",
                                "S7w2ds8mD6A6BWBmAnQw6Yf0kuLl530sNM0apzqQmCVR5Z2UtbCQddZ951W7jx2A"
                        )
                .build();

        return chain.proceed(request);
    };

    public static final ApolloClient apolloClient = ApolloClient
            .builder().serverUrl("https://open-primate-91.hasura.app/v1/graphql")
            .okHttpClient(new OkHttpClient.Builder().addInterceptor(hasuraInterceptor).build()).build();

}