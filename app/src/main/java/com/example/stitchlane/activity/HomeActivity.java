package com.example.stitchlane.activity;

import static com.example.stitchlane.database.CustomerDatabase.getCustomerDao;
import static com.example.stitchlane.database.CustomerDatabase.getCustomerDatabase;
import static com.example.stitchlane.helpers.CloudSyncHelper.syncLocalToCloud;
import static com.example.stitchlane.helpers.CustomerHelper.getAllCustomers;

import static java.util.Objects.isNull;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stitchlane.R;
import com.example.stitchlane.database.CustomerDao;
import com.example.stitchlane.database.CustomerDatabase;
import com.example.stitchlane.helpers.CustomerHelper;
import com.example.stitchlane.helpers.CustomerListCustomAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import datamodel.Customer;

public class HomeActivity extends AppCompatActivity {

    CustomerDatabase customerDatabase;
    CustomerDao customerDao;
    DatabaseReference databaseReference;
    ListView customerListView;
    List<Customer> customerList;
    CustomerHelper customerHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseApp.initializeApp(this);
        customerHelper = new CustomerHelper();

        customerDatabase = getCustomerDatabase(this);
        customerDao = getCustomerDao(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Button addNewCustomerButton = findViewById(R.id.addNewButton);
        addNewCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddNewCustomerActivity.class);
                startActivity(intent);
            }
        });

        Button searchButton = findViewById(R.id.searchButton);

        customerListView = findViewById(R.id.entryListView);
        sortAndUpdateCustomers();
        customerListView.setAdapter(new CustomerListCustomAdapter(this, 0, customerList));


        syncLocalToCloud(customerList);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Clicked!");
                doThings(databaseReference);
            }
        });


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Handle the new intent here
    }

    @Override
    protected void onResume() {
        super.onResume();
        sortAndUpdateCustomers();
        customerListView.setAdapter(new CustomerListCustomAdapter(this, 0, customerList));
    }

    @Override
    protected void onStart() {
        super.onStart();
        sortAndUpdateCustomers();
        customerListView.setAdapter(new CustomerListCustomAdapter(this, 0, customerList));
    }

    private void sortAndUpdateCustomers() {
        customerList = getAllCustomers(customerDao);
        customerList = isNull(customerList) ? new ArrayList<>() : customerList;
        customerList.sort((c1, c2) -> Long.compare(c2.getModified(), c1.getModified()));

    }

    private void doThings(DatabaseReference databaseReference) {

    }



    private static void insertCustomer(Customer newCustomer, CustomerDao customerDao) {
        // Perform insert operation in the background thread (recommended)
        ExecutorService executor = Executors.newSingleThreadExecutor();

        List<Customer> customers = new ArrayList<>();

        Future<?> future = executor.submit(() -> {
            customerDao.insertCustomer(newCustomer);
        });

        try {
            future.get(); // This blocks until the result is available
        } catch (Exception e) {
            // Handle exceptions, such as InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            // Shutdown the executor service when done
            executor.shutdown();
        }
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                customerDao.insertCustomer(newCustomer);
//                return null;
//            }
//        }.execute();
    }

    private static void displayCustomers(List<Customer> customers) {
        // Do something with the list of customers
        for (Customer customer : customers) {
            Log.d("Customer", "Name: " + customer.getName() + ", Phone: " + customer.getPhoneNumber());
        }
    }

    private static void deleteAll(CustomerDao customerDao) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                customerDao.deleteAll();
                return null;
            }
        }.execute();
    }

    private void showAllCustomer(DatabaseReference databaseReference) {
        System.out.println(databaseReference);
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    // Get the data snapshot
                    DataSnapshot dataSnapshot = task.getResult();
                    System.out.println(dataSnapshot);
                    // Check if the snapshot is not null and has children
                    if (dataSnapshot != null && dataSnapshot.exists()) {
                        // Iterate through each data snapshot (each customer entry)
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Get the customer data
                            Customer customer = snapshot.getValue(Customer.class);

                            // Log or process the customer data as needed
                            if (customer != null) {
                                Log.d("Customer", "Name: " + customer.getName() + ", Phone: " + customer.getPhoneNumber());
                            }
                        }
                    }
                } else {
                    // Handle errors
                    Log.e("Firebase", "Error fetching data", task.getException());
                }
            }
        });
    }

}
