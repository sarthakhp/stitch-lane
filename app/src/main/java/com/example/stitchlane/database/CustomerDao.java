package com.example.stitchlane.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import datamodel.Customer;

@Dao
public interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCustomer(Customer customer);

    @Update
    void updateCustomer(Customer customer);

    @Delete
    void deleteCustomer(Customer customer);

    @Query("SELECT * FROM customers WHERE name = :name")
    Customer getCustomerByNameQuery(String name);

    @Query("SELECT * FROM customers WHERE id = :id")
    Customer getCustomerByIdQuery(long id);

    @Query("DELETE FROM customers WHERE id = :id")
    void deleteCustomerByIdQuery(long id);

    @Query("DELETE FROM customers")
    void deleteAll();

    @Query("SELECT * FROM customers")
    List<Customer> getAllCustomersQuery();

}