package com.example.stitchlane.helpers;

import static com.example.stitchlane.database.CustomerDatabase.getCustomerDao;
import static java.util.Objects.isNull;

import android.content.Context;

import com.example.stitchlane.database.CustomerDao;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import datamodel.Customer;

public class CloudSyncHelper {

    private static DatabaseReference databaseReference;

    public static synchronized DatabaseReference getFireBaseDatabaseReference() {
        if (isNull(databaseReference)) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

    public static void syncLocalToCloud(List<Customer> customerList) {
        getFireBaseDatabaseReference();
        // Upload local data to Firebase
        databaseReference.child("customers").removeValue();
        for (Customer localCustomer : customerList) {
            String customerId = databaseReference.push().getKey();
//            databaseReference.child("customers").child(String.valueOf(localCustomer.getId())).setValue(localCustomer);
            String primary_key = String.valueOf(localCustomer.getId());
            databaseReference.child("customers").child(primary_key).setValue(localCustomer);
        }

        System.out.println("what?");

    }
}
