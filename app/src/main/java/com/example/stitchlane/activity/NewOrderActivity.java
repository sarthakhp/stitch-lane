package com.example.stitchlane.activity;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stitchlane.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewOrderActivity extends AppCompatActivity {

    private List<String> measurements = Arrays.asList("Length", "Bust", "Waist", "Height", "Shoulder", "Sleeve", "Bust Point");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        EditText nd = findViewById(R.id.test_num);
        nd.getInputType();

        LinearLayout parent_linear_layout = findViewById(R.id.parent_linear_layout);
        for (String measurement_title : measurements) {
            LinearLayout child_linear_layout = new LinearLayout(this);
            child_linear_layout.setOrientation(LinearLayout.HORIZONTAL);
            TextView textView = new TextView(this);
            textView.setText(measurement_title);
            child_linear_layout.addView(textView);
            EditText editText = new EditText(this);
            editText.setMinimumWidth(400);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
            child_linear_layout.addView(editText);
            parent_linear_layout.addView(child_linear_layout);
        }

    }
}
