package com.example.stitchlane.activity;

import static java.util.Objects.nonNull;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.stitchlane.R;
import com.example.stitchlane.helpers.CustomerListCustomAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datamodel.Customer;

public class TextEditDialogFragment extends DialogFragment {

    EditText editText;
    View view;
    Trie trie;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        System.out.println(("width"));
        System.out.println(layoutParams.width);
        layoutParams.width = 1;
        dialog.getWindow().setAttributes(layoutParams);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_text_edit, container, false);
        // Get references to UI elements
        editText = view.findViewById(R.id.editText);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSuggestions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void updateSuggestions(String input) {
        ListView suggestionListView = view.findViewById(R.id.contactSuggestionListView);
//        List<Customer> suggestionedCustomers = findTopMatchingNames(getContacts(), input, 3);

        List<Customer> suggestionedCustomers = trie.getTopSuggestions(input);;
        suggestionListView.setAdapter(new CustomerListCustomAdapter(getContext(), 0, suggestionedCustomers));
    }

    @Override
    public void onStart() {
        super.onStart();

        editText.requestFocus();
        Handler handler = new Handler();
        handler.postDelayed(this::showKeyboard, 400);


        // Make the background non-touchable by setting the window's dim amount
        if (getDialog() != null) {
            getDialog().getWindow().setDimAmount(0.8f);
        }


        Dialog dialog = getDialog();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(layoutParams);

        trie = new Trie();
        for (Customer customer: getContacts()) {
            trie.insert(customer);
        }


    }


//    private void showKeyboard() {
//        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (inputMethodManager != null) {
//            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
//        }
//    }

    private void showKeyboard() {
        LinearLayout relativeLayout = view.findViewById(R.id.customer_search_linear_layout);
        InputMethodManager inputMethodManager =
                (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                relativeLayout.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }


    private List<Customer> getContacts() {
        // Define the columns to retrieve
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

        // Query the contacts TODO null pointer
        Cursor cursor = getContext().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        int total = 0;

        System.out.println("Counting");
        List<Customer> names = new ArrayList<>();

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
                    names.add(new Customer(contactName, contactNumber));
                }

            } while (cursor.moveToNext());

            // Close the cursor after use
            cursor.close();
            System.out.println(total);
        }

        return names;
    }


    public static List<Customer> findTopMatchingNames(List<Customer> customers, String inputName, int topN) {
        // Use Levenshtein distance for similarity measurement
        Map<Customer, Integer> similarityMap = new HashMap<>();

        for (Customer customer : customers) {
            String fullName = customer.getName();
            int distance = calculateLevenshteinDistance(fullName, inputName);
            similarityMap.put(customer, distance);
        }

        // Sort the map by values (ascending order)
        List<Map.Entry<Customer, Integer>> sortedList = new ArrayList<>(similarityMap.entrySet());
        sortedList.sort(Comparator.comparing(Map.Entry::getValue));

        // Extract the top N matching names
        List<Customer> topMatches = new ArrayList<>();
        for (int i = 0; i < Math.min(topN, sortedList.size()); i++) {
            topMatches.add(sortedList.get(i).getKey());
        }

        return topMatches;
    }

    public static int calculateLevenshteinDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            for (int j = 0; j <= str2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(
                            dp[i - 1][j - 1] + costOfSubstitution(str1.charAt(i - 1), str2.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1
                    );
                }
            }
        }

        return dp[str1.length()][str2.length()];
    }

    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    public static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }

    class TrieNode {
        TrieNode[] children;
        boolean isEndOfWord;
        Customer customer;

        TrieNode() {
            children = new TrieNode[256]; // Assuming only lowercase alphabets
            isEndOfWord = false;
        }
    }

    class Trie {
        TrieNode root;

        Trie() {
            root = new TrieNode();
        }

        void insert(Customer customer) {
            String word = customer.getName();
            word = word.toLowerCase();
            TrieNode node = root;
            for (char ch : word.toCharArray()) {
                int index = ch;
                if (index > 256) {
                    break;
                }
                if (node.children[index] == null) {
                    node.children[index] = new TrieNode();
                }
                node = node.children[index];
            }
            node.customer = customer;
            System.out.println(word);
            node.isEndOfWord = true;
        }

        List<Customer> getTopSuggestions(String input) {
            input = input.toLowerCase();
            List<Customer> suggestions = new ArrayList<>();
            TrieNode node = root;

            // Traverse the Trie based on the input prefix
            for (char ch : input.toCharArray()) {
                int index = ch;
                if (node.children[index] == null) {
                    return suggestions; // No suggestions for the given prefix
                }
                node = node.children[index];
            }

            // Collect all words with the given prefix
            collectWords(node, input, suggestions);

            // Sort the suggestions based on some criteria (e.g., frequency, alphabetical order)
            suggestions.sort(Comparator.comparing(Customer::getName));

            // Return the top 3 suggestions
            return suggestions.subList(0, Math.min(3, suggestions.size()));
        }

        private void collectWords(TrieNode node, String prefix, List<Customer> suggestions) {
            if (node.isEndOfWord) {
                suggestions.add(node.customer);
            }

            for (int ch = 0; ch < 256; ch++) {
                TrieNode child = node.children[ch];
                if (child != null) {
                    collectWords(child, prefix + ch, suggestions);
                }
            }
        }
    }

}

