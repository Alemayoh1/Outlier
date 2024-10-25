// ProductActivity.java (refactored)
package com.example.ada.tucanocaffe;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ProductActivity extends Activity {

    public static final String EXTRA_COFFEE_NO = "coffeeNo";
    private TextView productNameView, coffeeDescripView;
    private ImageView coffeeImageView;
    private EditText clientMessageView;
    private Spinner tableNumsSpinner;
    private int coffeeNo;
    private String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Initialize UI elements
        productNameView = findViewById(R.id.name);
        coffeeDescripView = findViewById(R.id.description);
        coffeeImageView = findViewById(R.id.image);
        clientMessageView = findViewById(R.id.clientMessage);
        tableNumsSpinner = findViewById(R.id.tableNums);

        // Get coffeeNo from intent
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_COFFEE_NO)) {
            coffeeNo = getIntent().getExtras().getInt(EXTRA_COFFEE_NO);
            loadProductDetails(coffeeNo);
        } else {
            showErrorToast("Invalid product ID.");
        }
    }

    private void loadProductDetails(int coffeeNo) {
        SQLiteOpenHelper tucanoDatabaseHelper = new tucanoDatabaseHelper(this);
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = tucanoDatabaseHelper.getReadableDatabase();
            cursor = db.query("Product", new String[]{"Name", "Description", "ImageResourceId"},
                              "_id = ?", new String[]{Integer.toString(coffeeNo)}, null, null, null);
            if (cursor.moveToFirst()) {
                Product product = new Product(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
                displayProduct(product);
            } else {
                showErrorToast("Product not found.");
            }
        } catch (SQLiteException e) {
            showErrorToast("Database unavailable.");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    private void displayProduct(Product product) {
        productName = product.getName();
        coffeeImageView.setImageResource(product.getImageResourceId());
        coffeeImageView.setContentDescription(productName);
        productNameView.setText(productName);
        coffeeDescripView.setText(product.getDescription());
    }

    public void onSendOrder(View view) {
        String clientMessage = clientMessageView.getText().toString();
        String tableNum = tableNumsSpinner.getSelectedItem().toString();

        Intent sendOrderIntent = new Intent(ProductActivity.this, OrderActivity.class);
        sendOrderIntent.putExtra("productName", productName);
        sendOrderIntent.putExtra("clientMessage", clientMessage);
        sendOrderIntent.putExtra("tableNumber", tableNum);
        sendOrderIntent.putExtra("coffeeId", Integer.toString(coffeeNo));
        startActivity(sendOrderIntent);
    }

    public void onBackToProductsCategory(View view) {
        Intent intent = new Intent(this, AllProductsInCategoryActivity.class);
        startActivity(intent);
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
