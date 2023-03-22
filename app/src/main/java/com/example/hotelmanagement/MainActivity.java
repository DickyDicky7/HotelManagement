package com.example.hotelmanagement;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import com.example.hasura.Hasura;
import com.example.hasura.ROOMKIND_All_Query;
import com.example.hasura.ROOMKIND_By_Id_Query;
import com.example.hasura.ROOMKIND_Insert_Mutation;
import com.example.hasura.ROOMKIND_Update_Price_By_Id_Mutation;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //region Hasura Testing

        Thread thread = new Thread(() -> {

            Hasura.apolloClient.query(new ROOMKIND_All_Query())
                    .enqueue(new ApolloCall.Callback<ROOMKIND_All_Query.Data>() {
                        @Override
                        public void onResponse(@NonNull Response<ROOMKIND_All_Query.Data> response) {
                            System.out.print("\n");
                            response.getData().ROOMKIND().forEach(System.out::println);
                            System.out.print("\n");
                        }

                        @Override
                        public void onFailure(@NonNull ApolloException e) {
                            System.out.println(e);
                        }
                    });

            Hasura.apolloClient.query(new ROOMKIND_By_Id_Query(new Input<Integer>(1, true)))
                    .enqueue(new ApolloCall.Callback<ROOMKIND_By_Id_Query.Data>() {
                        @Override
                        public void onResponse(@NonNull Response<ROOMKIND_By_Id_Query.Data> response) {
                            System.out.print("\n");
                            response.getData().ROOMKIND().forEach(System.out::println);
                            System.out.print("\n");
                        }

                        @Override
                        public void onFailure(@NonNull ApolloException e) {
                            System.out.println(e);
                        }
                    });

            ROOMKIND_Insert_Mutation roomkindInsertMutation = ROOMKIND_Insert_Mutation
                    .builder()
                    .name("Quad")
                    .price(400)
                    .capacity(5)
                    .surcharge_percentage(0.5)
                    .surcharge_start_ordinal(5)
                    .build();

            Hasura.apolloClient.mutate(roomkindInsertMutation)
                    .enqueue(new ApolloCall.Callback<ROOMKIND_Insert_Mutation.Data>() {
                        @Override
                        public void onResponse(@NonNull Response<ROOMKIND_Insert_Mutation.Data> response) {
                            System.out.print("\n");
                            System.out.println(response.getData().insert_ROOMKIND());
                            System.out.print("\n");
                        }

                        @Override
                        public void onFailure(@NonNull ApolloException e) {
                            System.out.println(e);
                        }
                    });

            ROOMKIND_Update_Price_By_Id_Mutation roomkindUpdatePriceByIdMutation = ROOMKIND_Update_Price_By_Id_Mutation
                    .builder()
                    .id(1)
                    .price(600)
                    .build();

            Hasura.apolloClient.mutate(roomkindUpdatePriceByIdMutation)
                    .enqueue(new ApolloCall.Callback<ROOMKIND_Update_Price_By_Id_Mutation.Data>() {
                        @Override
                        public void onResponse(@NonNull Response<ROOMKIND_Update_Price_By_Id_Mutation.Data> response) {
                            System.out.print("\n");
                            System.out.println(response.getData().update_ROOMKIND());
                            System.out.print("\n");
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