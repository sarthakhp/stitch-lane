package com.example.stitchlane.activity;

import static com.example.stitchlane.constants.IntentConstants.CUSTOMER_ID;
import static com.example.stitchlane.constants.IntentConstants.EDIT_CUSTOMER_INTENT_TYPE;
import static com.example.stitchlane.database.CustomerDatabase.getCustomerDao;
import static com.example.stitchlane.helpers.CustomerHelper.deleteCustomerById;
import static com.example.stitchlane.helpers.CustomerHelper.getCustomerById;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stitchlane.R;
import com.example.stitchlane.database.CustomerDao;
import com.example.stitchlane.database.CustomerDatabase;

import datamodel.Customer;

public class CustomerInfoActivity extends AppCompatActivity {
//    length
//    Bust
//    Waist
//    Hip
//    Shoulder
//    Arm hole
//    Sleeve
//    Bust Point

    CustomerDatabase customerDatabase;
    CustomerDao customerDao;
    Customer customer;
    long customerId;
    TextView name;
    TextView phoneNumber;
    Button deleteButton;
    Button editButton;
    Button newOrderButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_info_layout);

        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.phoneNumber);
        deleteButton = findViewById(R.id.deleteCustomerInfoId);
        editButton = findViewById(R.id.editCustomerInfoId);
        newOrderButton = findViewById(R.id.newOrderButtonId);
        customerId = getIntent().getLongExtra(CUSTOMER_ID,0);
        customerDatabase = CustomerDatabase.getCustomerDatabase(this);
        customerDao = getCustomerDao(this);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCustomerById(customerDao, customerId);
                finish();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditCustomerInfo();
            }
        });

        newOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNewOrderActivity();
            }
        });



    }

    private void goToNewOrderActivity() {
        Intent intent = new Intent(CustomerInfoActivity.this, NewOrderActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        customer = getCustomerById(customerDao, customerId);
        name.setText(customer.getName());
        phoneNumber.setText(customer.getPhoneNumber());
    }

    private void goToEditCustomerInfo() {
        Intent intent = new Intent(CustomerInfoActivity.this, AddNewCustomerActivity.class);
        intent.putExtra(CUSTOMER_ID, customerId);
        intent.setType(EDIT_CUSTOMER_INTENT_TYPE);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // This will close the current activity and go back to the home activity
    }

    public static void openCustomerInfoActivity(Long customerId, Context context) {
        Intent intent = new Intent(context, CustomerInfoActivity.class);
        intent.putExtra(CUSTOMER_ID, customerId);
        context.startActivity(intent);
    }
}
