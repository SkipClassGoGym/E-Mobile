package com.nguyenquocthinh.k234112eapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.nguyenquocthinh.models.DataWareHouse;
import com.nguyenquocthinh.models.Order;
import com.nguyenquocthinh.models.OrderDetail;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderManagement extends AppCompatActivity {

    private TextView tvTodayOrders;
    private TextView tvTodayRevenue;
    private TextView tvNeedAction;

    private Button btnFromDate;
    private Button btnToDate;
    private Button btnFilterByDate;
    private Button btnClearDateFilter;
    private Button btnOrderStatusMenu;
    private ListView lvOrders;

    private List<Order> displayedOrdersList = new ArrayList<>();
    private OrderAdapter adapter;

    private Calendar fromCalendar = Calendar.getInstance();
    private Calendar toCalendar = Calendar.getInstance();
    
    private String selectedFromDate = "";
    private String selectedToDate = "";
    private String selectedStatus = "All Status";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        // Bind layouts
        tvTodayOrders = findViewById(R.id.tvTodayOrders);
        tvTodayRevenue = findViewById(R.id.tvTodayRevenue);
        tvNeedAction = findViewById(R.id.tvNeedAction);

        btnFromDate = findViewById(R.id.btnFromDate);
        btnToDate = findViewById(R.id.btnToDate);
        btnFilterByDate = findViewById(R.id.btnFilterByDate);
        btnClearDateFilter = findViewById(R.id.btnClearDateFilter);
        btnOrderStatusMenu = findViewById(R.id.btnOrderStatusMenu);
        lvOrders = findViewById(R.id.lvOrders);

        // Populate initially
        displayedOrdersList.addAll(DataWareHouse.getOrders());
        adapter = new OrderAdapter(this, displayedOrdersList);
        lvOrders.setAdapter(adapter);

        // Refresh KPIs based on active loaded items
        updateKPIs();

        // Date Picker dialogues bindings
        btnFromDate.setOnClickListener(v -> showDatePicker(true));
        btnToDate.setOnClickListener(v -> showDatePicker(false));

        // Range click checks
        btnFilterByDate.setOnClickListener(v -> applyFilters());

        // Reset click checks
        btnClearDateFilter.setOnClickListener(v -> {
            selectedFromDate = "";
            selectedToDate = "";
            selectedStatus = "All Status";
            btnFromDate.setText("From: Date");
            btnToDate.setText("To: Date");
            btnOrderStatusMenu.setText("All Status ▼");
            applyFilters();
        });

        // Status menu bindings
        btnOrderStatusMenu.setOnClickListener(v -> showStatusPopupMenu());

        // Item click handles
        lvOrders.setOnItemClickListener((parent, view, position, id) -> {
            Order selectedOrder = displayedOrdersList.get(position);
            Intent intent = new Intent(OrderManagement.this, InvoiceDetailActivity.class);
            intent.putExtra("SELECTED_ORDER", selectedOrder);
            startActivity(intent);
        });

        // Back button actions
        findViewById(R.id.btnBackOrder).setOnClickListener(v -> finish());
    }

    private void showDatePicker(boolean isFrom) {
        Calendar activeCalendar = isFrom ? fromCalendar : toCalendar;
        int year = activeCalendar.get(Calendar.YEAR);
        int month = activeCalendar.get(Calendar.MONTH);
        int day = activeCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, selectYear, selectMonth, selectDay) -> {
            activeCalendar.set(Calendar.YEAR, selectYear);
            activeCalendar.set(Calendar.MONTH, selectMonth);
            activeCalendar.set(Calendar.DAY_OF_MONTH, selectDay);

            String selectedDate = dateFormat.format(activeCalendar.getTime());
            if (isFrom) {
                selectedFromDate = selectedDate;
                btnFromDate.setText("From: " + selectedDate);
            } else {
                selectedToDate = selectedDate;
                btnToDate.setText("To: " + selectedDate);
            }
        }, year, month, day);

        dialog.show();
    }

    private void showStatusPopupMenu() {
        PopupMenu popup = new PopupMenu(this, btnOrderStatusMenu);
        popup.getMenuInflater().inflate(R.menu.orderstatus, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_all_status) {
                selectedStatus = "All Status";
            } else if (itemId == R.id.menu_complete) {
                selectedStatus = "Complete";
            } else if (itemId == R.id.menu_not_payment) {
                selectedStatus = "Not Payment";
            } else if (itemId == R.id.menu_on_logistic) {
                selectedStatus = "On Logistic";
            } else if (itemId == R.id.menu_customer_complaint) {
                selectedStatus = "Customer Complaint";
            }

            btnOrderStatusMenu.setText(selectedStatus + " ▼");
            applyFilters();
            return true;
        });

        popup.show();
    }

    private void applyFilters() {
        displayedOrdersList.clear();
        List<Order> source = DataWareHouse.getOrders();

        for (Order order : source) {
            // Status check
            if (!selectedStatus.equals("All Status") && !order.getStatus().equalsIgnoreCase(selectedStatus)) {
                continue;
            }

            // Date Range check
            if (!selectedFromDate.isEmpty()) {
                if (order.getDate().compareTo(selectedFromDate) < 0) {
                    continue;
                }
            }
            if (!selectedToDate.isEmpty()) {
                if (order.getDate().compareTo(selectedToDate) > 0) {
                    continue;
                }
            }

            displayedOrdersList.add(order);
        }

        adapter.notifyDataSetChanged();
        updateKPIs();
    }

    private void updateKPIs() {
        int totalOrders = displayedOrdersList.size();
        double totalSalesValue = 0.0;
        int needsAction = 0;

        for (Order o : displayedOrdersList) {
            // Total cost calc
            List<OrderDetail> details = DataWareHouse.getOrderDetailsForOrder(o.getOrderId());
            for (OrderDetail d : details) {
                totalSalesValue += (d.getPrice() * d.getQuantity());
            }

            // Needs action check
            if (o.getStatus().equals("Not Payment") || o.getStatus().equals("Customer Complaint")) {
                needsAction++;
            }
        }

        tvTodayOrders.setText(String.valueOf(totalOrders));
        tvTodayRevenue.setText(currencyFormat.format(totalSalesValue));
        tvNeedAction.setText(String.valueOf(needsAction));
    }
}
