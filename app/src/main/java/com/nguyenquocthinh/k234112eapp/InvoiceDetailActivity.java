package com.nguyenquocthinh.k234112eapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.nguyenquocthinh.models.Customer;
import com.nguyenquocthinh.models.DataWareHouse;
import com.nguyenquocthinh.models.Employee;
import com.nguyenquocthinh.models.Order;
import com.nguyenquocthinh.models.OrderDetail;
import com.nguyenquocthinh.models.Product;

import java.text.DecimalFormat;
import java.util.List;

public class InvoiceDetailActivity extends AppCompatActivity {

    private TextView tvInvoiceOrderId;
    private TextView tvInvoiceDate;
    private TextView tvInvoiceCustomer;
    private TextView tvInvoiceEmployee;
    private LinearLayout layoutInvoiceProducts;
    private TextView tvInvoiceTotal;
    private Button btnBackInvoice;

    private final DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        tvInvoiceOrderId = findViewById(R.id.tvInvoiceOrderId);
        tvInvoiceDate = findViewById(R.id.tvInvoiceDate);
        tvInvoiceCustomer = findViewById(R.id.tvInvoiceCustomer);
        tvInvoiceEmployee = findViewById(R.id.tvInvoiceEmployee);
        layoutInvoiceProducts = findViewById(R.id.layoutInvoiceProducts);
        tvInvoiceTotal = findViewById(R.id.tvInvoiceTotal);
        btnBackInvoice = findViewById(R.id.btnBackInvoice);

        // Get Serializable Order parameter
        Order order = (Order) getIntent().getSerializableExtra("SELECTED_ORDER");
        if (order != null) {
            populateInvoice(order);
        }

        btnBackInvoice.setOnClickListener(v -> finish());
    }

    private void populateInvoice(Order order) {
        tvInvoiceOrderId.setText("INVOICE #" + order.getOrderId());
        tvInvoiceDate.setText("Date: " + order.getDate());

        // Fill Client Customer
        Customer customer = DataWareHouse.getCustomerById(order.getCustomerId());
        if (customer != null) {
            String customerDetails = customer.getName() + " • " + customer.getPhone() + "\n" + customer.getAddress();
            tvInvoiceCustomer.setText(customerDetails);
        } else {
            tvInvoiceCustomer.setText("Unknown Customer");
        }

        // Fill Staff handled by
        Employee employee = DataWareHouse.getEmployeeById(order.getEmployeeId());
        if (employee != null) {
            String employeeDetails = employee.getName() + " (" + employee.getRole() + ")";
            tvInvoiceEmployee.setText(employeeDetails);
        } else {
            tvInvoiceEmployee.setText("Unknown Crew");
        }

        // Inflate dynamic products row lines
        layoutInvoiceProducts.removeAllViews();
        List<OrderDetail> details = DataWareHouse.getOrderDetailsForOrder(order.getOrderId());
        double subTotalSum = 0.0;

        LayoutInflater inflater = LayoutInflater.from(this);
        for (OrderDetail detail : details) {
            Product product = DataWareHouse.getProductById(detail.getProductId());
            String productName = (product != null) ? product.getName() : "Item " + detail.getProductId();

            double lineTotal = detail.getPrice() * detail.getQuantity();
            subTotalSum += lineTotal;

            // Inflate small layout row on the fly
            View rowView = inflater.inflate(android.R.layout.simple_list_item_2, layoutInvoiceProducts, false);
            TextView text1 = rowView.findViewById(android.R.id.text1);
            TextView text2 = rowView.findViewById(android.R.id.text2);

            // Style text size and colors
            text1.setText(productName + " (x" + detail.getQuantity() + ")");
            text1.setTextColor(getResources().getColor(R.color.order_text_primary));
            text1.setTextSize(14);

            text2.setText("Unit: " + currencyFormat.format(detail.getPrice()) + " • Line total: " + currencyFormat.format(lineTotal));
            text2.setTextColor(getResources().getColor(R.color.order_text_secondary));
            text2.setTextSize(12);

            layoutInvoiceProducts.addView(rowView);
        }

        // Update final sum info
        tvInvoiceTotal.setText(currencyFormat.format(subTotalSum));
    }
}
