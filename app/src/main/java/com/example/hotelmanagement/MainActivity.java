package com.example.hotelmanagement;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.Hasura;
import com.example.hasura.MyQuery;
import com.example.hasura.UniqueQuery;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //region Hasura Testing
        Thread thread = new Thread(() -> {
            Hasura.apolloClient.query(new MyQuery(new Input<Integer>(1,true))).enqueue(new ApolloCall.Callback<MyQuery.Data>() {
                @Override
                public void onResponse(@NonNull Response<MyQuery.Data> response) {
                    System.out.println(response.getData().ROOM().get(0).name());
                }

                @Override
                public void onFailure(@NonNull ApolloException e) {
                    System.out.println(e);
                }
            });
        });
        thread.start();
        //endregion
    }

}