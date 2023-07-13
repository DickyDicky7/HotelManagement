package com.example.hotelmanagement.hasura;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.CredentialsManager;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.authentication.storage.Storage;
import com.auth0.android.callback.Callback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;

import java.util.function.Consumer;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Hasura {

    @NonNull
    public static Boolean mediaManagerNotInitialized = true;
    @Nullable
    protected static Hasura instance;
    @NonNull
    public LogState currentLogState;
    @NonNull
    protected Auth0 auth_0;
    @NonNull
    protected String scheme;

    @Nullable
    protected Interceptor interceptor;
    @Nullable
    protected OkHttpClient okHttpClient;
    @Nullable
    protected ApolloClient apolloClient;

    @Nullable
    protected Storage storage;
    @Nullable
    protected CredentialsManager credentialsManager;
    @Nullable
    protected AuthenticationAPIClient authenticationAPIClient;

    protected Hasura(@NonNull String clientId, @NonNull String domain, @NonNull String scheme) {
        this.currentLogState = LogState.OUT;
        this.scheme = scheme;
        this.auth_0 = new Auth0(clientId, domain);
    }

    @Nullable
    public static Hasura getInstance() {
        return instance;
    }

    @NonNull
    public static Hasura requireInstance() {
        assert instance != null;
        return instance;
    }

    @NonNull
    public static Hasura requireInstance(@NonNull String clientId, @NonNull String domain, @NonNull String scheme) {
        if (instance == null) {
            instance = new Hasura(clientId, domain, scheme);
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    public void Login(
            @NonNull Context context,
            @Nullable Runnable onSuccessCallback,
            @Nullable Runnable onFailureCallback,
            @Nullable Consumer<AuthenticationException> authenticationExceptionConsumer) {
        try {
            WebAuthProvider.login(auth_0).withScheme(scheme).start(context, new Callback<Credentials, AuthenticationException>() {

                @SuppressWarnings("ConstantConditions")
                @Override
                public void onSuccess(Credentials credentials) {
                    createInterceptor(credentials.getIdToken());
                    String serverURL = null;
                    String socketURL = null;
                    createApolloClient(serverURL, socketURL);
                    configAuthenticationAPIClientAndStorageAndCredentialsManager(context, credentials);
                    currentLogState = LogState.IN;
                    if (onSuccessCallback != null) {
                        onSuccessCallback.run();
                    }
                }

                @Override
                public void onFailure(@NonNull AuthenticationException e) {
                    Log.e("Auth0 Login Failure", e.getDescription());
                    currentLogState = LogState.OUT;
                    if (onFailureCallback != null) {
                        onFailureCallback.run();
                    }
                    if (authenticationExceptionConsumer != null) {
                        authenticationExceptionConsumer.accept(e);
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Logout(
            @NonNull Context context,
            @Nullable Runnable onSuccessCallback,
            @Nullable Runnable onFailureCallback,
            @Nullable Consumer<AuthenticationException> authenticationExceptionConsumer) {
        try {
            WebAuthProvider.logout(auth_0).withScheme(scheme).start(context, new Callback<Void, AuthenticationException>() {

                @Override
                public void onSuccess(Void unused) {
                    currentLogState = LogState.OUT;
                    if (onSuccessCallback != null) {
                        onSuccessCallback.run();
                    }
                }

                @Override
                public void onFailure(@NonNull AuthenticationException e) {
                    Log.e("Auth0 Logout Failure", e.getDescription());
                    currentLogState = LogState.IN;
                    if (onFailureCallback != null) {
                        onFailureCallback.run();
                    }
                    if (authenticationExceptionConsumer != null) {
                        authenticationExceptionConsumer.accept(e);
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public ApolloClient requireApolloClient() {
        assert apolloClient != null;
        return apolloClient;
    }

    public void getCredentials(@Nullable Consumer<Credentials> credentialsConsumer, @Nullable Consumer<CredentialsManagerException> credentialsManagerExceptionConsumer) {
        if (credentialsManager != null) {
            credentialsManager.getCredentials(new Callback<Credentials, CredentialsManagerException>() {

                @Override
                public void onSuccess(
                        Credentials credentials) {

                    if (credentialsConsumer != null) {
                        credentialsConsumer.accept(credentials);
                    }

                }

                @Override
                public void onFailure(@NonNull CredentialsManagerException e) {

                    if (credentialsManagerExceptionConsumer != null) {
                        credentialsManagerExceptionConsumer.accept(e);
                    }

                }

            });
        }
    }

    public void resetPassword(
            @Nullable Runnable onSuccessCallback,
            @Nullable Runnable onFailureCallback,
            @Nullable Consumer<AuthenticationException> authenticationExceptionConsumer) {

        getCredentials(credentials -> {
                    String email = credentials.getUser().getEmail();
                    if (null != email && null != authenticationAPIClient) {
                        authenticationAPIClient.resetPassword(email, "Username-Password-Authentication").start(new Callback<Void, AuthenticationException>() {

                            @Override
                            public void onSuccess(Void unused) {
                                if (onSuccessCallback != null) {
                                    onSuccessCallback.run();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull AuthenticationException e) {
                                if (onFailureCallback != null) {
                                    onFailureCallback.run();
                                }
                                if (authenticationExceptionConsumer != null) {
                                    authenticationExceptionConsumer.accept(e);
                                }
                                Log.e("Auth0 Reset Password Failure", e.getDescription());
                            }

                        });
                    }
                }
                , null);
    }

    protected void configAuthenticationAPIClientAndStorageAndCredentialsManager(@NonNull Context context, @NonNull Credentials credentials) {

        if (authenticationAPIClient == null) {
            authenticationAPIClient = new AuthenticationAPIClient(auth_0);
        }

        if (storage == null) {
            storage = new SharedPreferencesStorage(context);
        }

        if (credentialsManager == null) {
            credentialsManager = new CredentialsManager(authenticationAPIClient, storage);
            credentialsManager.saveCredentials(credentials);
        }

    }

    @SuppressWarnings("SameParameterValue")
    protected void createApolloClient(@Nullable String serverURL, @Nullable String webSocketURL) {
        if (interceptor != null) {
            try {

                if (serverURL == null) {
                    serverURL = "https://open-primate-91.hasura.app/v1/graphql";
                }
                if (webSocketURL == null) {
                    webSocketURL = "wss://open-primate-91.hasura.app/v1/graphql";
                }

                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
                }
                if (apolloClient == null) {
                    WebSocketSubscriptionTransport.Factory webSocketSubscriptionTransportFactory = new WebSocketSubscriptionTransport
                            .Factory(webSocketURL, okHttpClient);

                    apolloClient = ApolloClient.builder().serverUrl(serverURL).okHttpClient(okHttpClient)
                            .subscriptionTransportFactory(webSocketSubscriptionTransportFactory).build();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void createInterceptor(@NonNull String authorizationToken) {
        if (interceptor == null) {
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
    }

    public enum LogState {
        IN, OUT,
    }

}
