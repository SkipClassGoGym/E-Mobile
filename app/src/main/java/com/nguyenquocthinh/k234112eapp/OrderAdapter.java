package com.nguyenquocthinh.k234112eapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nguyenquocthinh.models.Customer;
import com.nguyenquocthinh.models.DataWareHouse;
import com.nguyenquocthinh.models.Order;
import com.nguyenquocthinh.models.OrderDetail;

import java.text.DecimalFormat;
import java.util.List;

public class OrderAdapter extends ArrayAdapter<Order> {

    private final Context context;
    private final List<Order> orders;
    private final DecimalFormat df = new DecimalFormat("$#,##0.00");

    public OrderAdapter(Context context, List<Order> orders) {
        super(context, R.layout.item_order, orders);
        this.context = context;
        this.orders = orders;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        }

        Order order = orders.get(position);

        TextView txtOrderSummary = convertView.findViewById(R.id.txtOrderSummary);
        TextView txtOrderStatus = convertView.findViewById(R.id.txtOrderStatus);

        // Calculate order sum from its lines
        double totalSum = 0.0;
        List<OrderDetail> details = DataWareHouse.getOrderDetailsForOrder(order.getOrderId());
        for (OrderDetail d : details) {
            totalSum += (d.getPrice() * d.getQuantity());
        }

        // Get customer name
        Customer customer = DataWareHouse.getCustomerById(order.getCustomerId());
        String customerName = (customer != null) ? customer.getName() : "Unknown Customer";

        String summary = "ID: " + order.getOrderId() + " • " + order.getDate() + "\n" +
                "Client: " + customerName + "\n" +
                "Total Value: " + df.format(totalSum);

        txtOrderSummary.setText(summary);
        txtOrderStatus.setText(order.getStatus());

        // Set status colors
        switch (order.getStatus()) {
            case "Complete":
                txtOrderStatus.setBackgroundResource(R.drawable.bg_status_success);
                txtOrderStatus.setTextColor(context.getResources().getColor(R.color.order_success_text));
                break;
            case "Not Payment":
                txtOrderStatus.setBackgroundResource(R.drawable.bg_status_pending);
                txtOrderStatus.setTextColor(context.getResources().getColor(R.color.order_pending_text));
                break;
            case "On Logistic":
                txtOrderStatus.setBackgroundResource(R.drawable.bg_status_warning);
                txtOrderStatus.setTextColor(context.getResources().getColor(R.color.order_warning_text));
                break;
            case "Customer Complaint":
                txtOrderStatus.setBackgroundResource(R.drawable.bg_status_complaint);
                txtOrderStatus.setTextColor(context.getResources().getColor(R.color.order_complaint_text));
                break;
            default:
                txtOrderStatus.setBackgroundResource(R.drawable.bg_order_chip);
                txtOrderStatus.setTextColor(context.getResources().getColor(R.color.order_text_primary));
                break;
        }

        return convertView;
    }
}
