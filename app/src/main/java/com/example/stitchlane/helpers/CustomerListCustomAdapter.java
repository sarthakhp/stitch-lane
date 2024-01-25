package com.example.stitchlane.helpers;

import static com.example.stitchlane.activity.CustomerInfoActivity.openCustomerInfoActivity;
import static com.example.stitchlane.constants.IntentConstants.CUSTOMER_ID;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.stitchlane.R;
import com.example.stitchlane.activity.CustomerInfoActivity;

import java.util.List;

import datamodel.Customer;

public class CustomerListCustomAdapter extends ArrayAdapter<Customer> {

    List<Customer> customerList;

    public CustomerListCustomAdapter(@NonNull Context context, int resource, List<Customer> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.customer_list_layout, parent, false);
        }

        // Get the data item for this position
        Customer customer = getItem(position);

        // Set the TextView to display the item
        Button button = convertView.findViewById(R.id.singleCustomerDetailsButtonId);
        button.setText(customer.getName());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomerInfoActivity(customer.getId(), getContext());
            }
        });

        return convertView;
    }
}
