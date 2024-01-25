package com.example.stitchlane.activity;

import static com.example.stitchlane.activity.CustomerInfoActivity.openCustomerInfoActivity;
import static com.example.stitchlane.constants.IntentConstants.CUSTOMER_ID;
import static com.example.stitchlane.constants.IntentConstants.EDIT_CUSTOMER_INTENT_TYPE;
import static com.example.stitchlane.database.CustomerDatabase.getCustomerDao;
import static com.example.stitchlane.database.CustomerDatabase.getCustomerDatabase;
import static com.example.stitchlane.helpers.CustomerHelper.getCustomerById;
import static com.example.stitchlane.helpers.CustomerHelper.insertCustomer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.stitchlane.R;
import com.example.stitchlane.database.CustomerDao;
import com.example.stitchlane.database.CustomerDatabase;

import java.util.Date;

import datamodel.Customer;

public class AddNewCustomerActivity extends AppCompatActivity {


    CustomerDatabase customerDatabase;
    CustomerDao customerDao;
    Customer newCustomer;
    EditText nameInput;
    EditText numberInput;

    private static int CONTACTS_PERMISSION_REQUEST_CODE = 123; // Use any integer value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_customer);

        customerDatabase = getCustomerDatabase(this);
        customerDao = getCustomerDao(this);

        nameInput = findViewById(R.id.customerNameInputId);
        numberInput = findViewById(R.id.phoneNumberInputId);
        Button openContactsButton = findViewById(R.id.openContactsButtonId);
        Button addCustomerButton = findViewById(R.id.saveNewCustomer);


        openContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchContactsChoosing();
            }
        });

        addCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCustomer();
                if (!EDIT_CUSTOMER_INTENT_TYPE.equals(getIntent().getType())) {
                    openCustomerInfoActivity(newCustomer.getId(), v.getContext());
                }
                finish();
            }
        });
//
//        getSupportFragmentManager();

    }

    private void populateValues(long customerId) {
        newCustomer = getCustomerById(customerDao, customerId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        newCustomer = new Customer();
        if (EDIT_CUSTOMER_INTENT_TYPE.equals(getIntent().getType())){
            populateValues(getIntent().getLongExtra(CUSTOMER_ID, -1));
        }

        nameInput.setText(newCustomer.getName());
        numberInput.setText(newCustomer.getPhoneNumber());
    }

    private void showKeyboard() {
        LinearLayout relativeLayout = findViewById(R.id.add_customer_relative_layout);
        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                relativeLayout.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    private void launchContactsChoosing() {
        // Check if the READ_CONTACTS permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            System.out.println((CONTACTS_PERMISSION_REQUEST_CODE));
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, proceed with reading contacts
            searchContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CONTACTS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with reading contacts
                searchContacts();
            } else {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, CONTACTS_PERMISSION_REQUEST_CODE);
                // Permission denied, handle accordingly (e.g., show a message or disable the feature)
                Toast.makeText(this, "Contacts permission denied. Cannot proceed.", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void searchContacts() {
        showTextEditDialog();
        // Define the columns to retrieve
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

        // Query the contacts
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        int total = 0;

        System.out.println("Counting");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Extract contact information
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                String contactName = null;
                if (nameIndex >= 0) {
                    contactName = cursor.getString(nameIndex);
                }
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String contactNumber = null;
                if (numberIndex >= 0) {
                    contactNumber = cursor.getString(numberIndex);
                }

                if (nonNull(contactName) && nonNull(contactNumber)) {
                    total += 1;
                }

                // Create a new Customer object or use your existing Customer class
//                    Customer newCustomer = new Customer();
//                    newCustomer.setName(contactName);
//                    newCustomer.setPhoneNumber(contactNumber);
//
//                    // Add the new customer to the database (implement your own method)
//                    addCustomerToDatabase(newCustomer);

            } while (cursor.moveToNext());

            // Close the cursor after use
            cursor.close();
            System.out.println(total);
        }
    }

    private void showTextEditDialog() {
        TextEditDialogFragment textEditDialogFragment = new TextEditDialogFragment();
        textEditDialogFragment.show(getSupportFragmentManager(), "TextEditDialogFragment");
    }

    private void saveCustomer() {


        // Example: Insert a new customer
        newCustomer.setName(nameInput.getText().toString());
        newCustomer.setPhoneNumber(numberInput.getText().toString());
        newCustomer.setModified(new Date().getTime());
        if (isNull(newCustomer.getCreated())) {
            newCustomer.setCreated(newCustomer.getModified());
        }
        newCustomer = getCustomerById(customerDao, insertCustomer(newCustomer, customerDao));
    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // This will close the current activity and go back to the home activity
    }
}
