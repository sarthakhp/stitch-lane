package com.example.stitchlane.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import datamodel.Customer;

@Database(entities = {Customer.class}, version = 3, exportSchema = false)
public abstract class CustomerDatabase extends RoomDatabase {

    public abstract CustomerDao customerDao();

    private static CustomerDatabase instance;

    public static synchronized CustomerDatabase getCustomerDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    CustomerDatabase.class,
                    "customer_database"
            ).fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    public static CustomerDao getCustomerDao(Context context) {
        return getCustomerDatabase(context).customerDao();
    }
}