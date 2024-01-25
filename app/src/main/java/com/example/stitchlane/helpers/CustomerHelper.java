package com.example.stitchlane.helpers;

import android.os.AsyncTask;

import com.example.stitchlane.database.CustomerDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import datamodel.Customer;

public class CustomerHelper {

    public static List<Customer> getAllCustomers(CustomerDao customerDao){
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<List<Customer>> future = executor.submit(customerDao::getAllCustomersQuery);

        return performDatabaseOperation(future, executor);

    }

    public static long insertCustomer(Customer newCustomer, CustomerDao customerDao) {
        // Perform insert operation in the background thread (recommended)
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(() -> customerDao.insertCustomer(newCustomer));
        return performDatabaseOperation(future, executor);
    }

    public static Customer getCustomerById(CustomerDao customerDao, long id) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Customer> future = executor.submit(() -> customerDao.getCustomerByIdQuery(id));
        return performDatabaseOperation(future, executor);
    }

    public static Customer getCustomerByName(CustomerDao customerDao, String name) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Customer> future = executor.submit(() -> customerDao.getCustomerByNameQuery(name));
        return performDatabaseOperation(future, executor);
    }

    public static void deleteCustomerById(CustomerDao customerDao, long id) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(() -> customerDao.deleteCustomerByIdQuery(id));
        performDatabaseOperation(future, executor);
    }

    private static <T> T performDatabaseOperation(Future<?> future, ExecutorService executor) {
        T result = null;
        try {
            result = (T) future.get(); // This blocks until the result is available
            return result;
        } catch (Exception e) {
            // Handle exceptions, such as InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            // Shutdown the executor service when done
            executor.shutdown();
        }
        return result;
    }

}
