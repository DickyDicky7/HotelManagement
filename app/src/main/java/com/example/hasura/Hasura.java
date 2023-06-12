package com.example.hasura;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.Callback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Hasura {

    public static Credentials credentials;
    public static ApolloClient apolloClient;
    public static Boolean loginSuccessfully;
    public static Boolean logoutSuccessfully;

    protected static Auth0 auth0;
    protected static String scheme;
    protected static Interceptor interceptor;
    protected static OkHttpClient okHttpClient;

    public static void configAuth0(@NonNull String clientId, @NonNull String domain, @NonNull String scheme) {
        try {
            Hasura.scheme = scheme;
            loginSuccessfully = false;
            logoutSuccessfully = false;
            auth0 = new Auth0(clientId, domain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void login(@NonNull Context context, @Nullable Runnable onSuccessCallback, @Nullable Runnable onFailureCallback) {
        try {
            WebAuthProvider.login(auth0).withScheme(scheme).start(context, new Callback<Credentials, AuthenticationException>() {

                @Override
                public void onSuccess(Credentials credentials) {
                    createInterceptor(credentials.getIdToken());
                    createApolloClient(null, null);
                    Hasura.credentials = credentials;
                    loginSuccessfully = true;
                    if (onSuccessCallback != null)
                        onSuccessCallback.run();
                }

                @Override
                public void onFailure(@NonNull AuthenticationException e) {
                    System.out.println(e.getDescription());
                    loginSuccessfully = false;
                    if (onFailureCallback != null)
                        onFailureCallback.run();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logout(@NonNull Context context, @Nullable Runnable onSuccessCallback, @Nullable Runnable onFailureCallback) {
        try {
            WebAuthProvider.logout(auth0).withScheme(scheme).start(context, new Callback<Void, AuthenticationException>() {

                @Override
                public void onSuccess(Void unused) {
                    logoutSuccessfully = true;
                    if (onSuccessCallback != null)
                        onSuccessCallback.run();
                }

                @Override
                public void onFailure(@NonNull AuthenticationException e) {
                    System.out.println(e.getDescription());
                    logoutSuccessfully = false;
                    if (onFailureCallback != null)
                        onFailureCallback.run();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void createInterceptor(@NonNull String authorizationToken) {
        try {
            interceptor = chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader
                                (
                                        "content-type",
                                        "application/json"
                                )
                        .addHeader
                                (
                                        "Authorization",
                                        "Bearer" + " " + authorizationToken
                                )
                        .build();
                return chain.proceed(request);
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void createApolloClient(@Nullable String serverURL, @Nullable String webSocketURL) {
        try {

            if (serverURL == null)
                serverURL = "https://open-primate-91.hasura.app/v1/graphql";
            if (webSocketURL == null)
                webSocketURL = "wss://open-primate-91.hasura.app/v1/graphql";

            okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            WebSocketSubscriptionTransport.Factory webSocketSubscriptionTransportFactory = new WebSocketSubscriptionTransport.Factory(webSocketURL, okHttpClient);
            apolloClient = ApolloClient.builder().serverUrl(serverURL).okHttpClient(okHttpClient).subscriptionTransportFactory(webSocketSubscriptionTransportFactory).build();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}