package com.nguyenquocthinh.k234112eapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class CalculatorActivity extends AppCompatActivity {

    private TextView tvCalcHistory;
    private TextView tvCalcResult;
    private TextView tvCalcValue;

    private String currentInput = "";
    private double firstValue = Double.NaN;
    private String pendingOperation = "";
    private double memoryValue = 0.0;

    private static final String PREF_NAME = "CalcPreferences";
    private static final String KEY_LAST_CALC = "last_calculation_history";
    
    private final DecimalFormat format = new DecimalFormat("0.######");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        // Map UI views
        tvCalcHistory = findViewById(R.id.tvCalcHistory);
        tvCalcResult = findViewById(R.id.tvCalcResult);
        tvCalcValue = findViewById(R.id.tvCalcValue);

        // Load saved formula calculation from shared prefs
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String lastHistory = prefs.getString(KEY_LAST_CALC, "History: None");
        tvCalcHistory.setText(lastHistory);

        // Bind standard digit keys
        int[] digitIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot
        };

        View.OnClickListener digitListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                String text = btn.getText().toString();
                if (text.equals(".") && currentInput.contains(".")) {
                    return; // Prevent duplicate dec point
                }
                currentInput += text;
                tvCalcValue.setText(currentInput);
            }
        };

        for (int id : digitIds) {
            findViewById(id).setOnClickListener(digitListener);
        }

        // Bind operator keys
        findViewById(R.id.btnAdd).setOnClickListener(v -> selectOperation("+"));
        findViewById(R.id.btnSub).setOnClickListener(v -> selectOperation("-"));
        findViewById(R.id.btnMul).setOnClickListener(v -> selectOperation("×"));
        findViewById(R.id.btnDiv).setOnClickListener(v -> selectOperation("/"));

        // Special mathematical operators
        findViewById(R.id.btnSqrt).setOnClickListener(v -> applyUnaryOperator("sqrt"));
        findViewById(R.id.btnSquare).setOnClickListener(v -> applyUnaryOperator("square"));
        findViewById(R.id.btnInverse).setOnClickListener(v -> applyUnaryOperator("inverse"));
        findViewById(R.id.btnPercent).setOnClickListener(v -> applyUnaryOperator("percent"));

        // Standard Utility commands
        findViewById(R.id.btnC).setOnClickListener(v -> clearAll());
        findViewById(R.id.btnDel).setOnClickListener(v -> deleteLastCharacter());
        findViewById(R.id.btnEqual).setOnClickListener(v -> performCalculation());

        // Back button action
        findViewById(R.id.btnBackCalc).setOnClickListener(v -> finish());

        // Memory operational buttons
        findViewById(R.id.btnMC).setOnClickListener(v -> {
            memoryValue = 0.0;
            Toast.makeText(this, "Memory Cleared", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnMR).setOnClickListener(v -> {
            currentInput = format.format(memoryValue);
            tvCalcValue.setText(currentInput);
        });

        findViewById(R.id.btnMPlus).setOnClickListener(v -> {
            try {
                double activeVal = Double.parseDouble(tvCalcValue.getText().toString());
                memoryValue += activeVal;
                Toast.makeText(this, "M+ added: " + format.format(activeVal), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error in numerical input", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnMMinus).setOnClickListener(v -> {
            try {
                double activeVal = Double.parseDouble(tvCalcValue.getText().toString());
                memoryValue -= activeVal;
                Toast.makeText(this, "M- subtracted: " + format.format(activeVal), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error in numerical input", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectOperation(String op) {
        try {
            if (!currentInput.isEmpty()) {
                firstValue = Double.parseDouble(currentInput);
                pendingOperation = op;
                tvCalcResult.setText(format.format(firstValue) + " " + pendingOperation);
                currentInput = "";
                tvCalcValue.setText("0");
            }
        } catch (NumberFormatException e) {
            tvCalcValue.setText("Error");
        }
    }

    private void applyUnaryOperator(String type) {
        try {
            double value = Double.parseDouble(tvCalcValue.getText().toString());
            double result = 0.0;
            String expressionText = "";

            switch (type) {
                case "sqrt":
                    if (value < 0) {
                        tvCalcValue.setText("Error");
                        return;
                    }
                    result = Math.sqrt(value);
                    expressionText = "√(" + format.format(value) + ")";
                    break;
                case "square":
                    result = value * value;
                    expressionText = "(" + format.format(value) + ")²";
                    break;
                case "inverse":
                    if (value == 0) {
                        tvCalcValue.setText("Error");
                        return;
                    }
                    result = 1.0 / value;
                    expressionText = "1/(" + format.format(value) + ")";
                    break;
                case "percent":
                    result = value / 100.0;
                    expressionText = format.format(value) + "%";
                    break;
            }

            currentInput = format.format(result);
            tvCalcValue.setText(currentInput);
            tvCalcResult.setText(expressionText + " =");
            
            // Save inside prefs history immediately
            saveToHistory(expressionText + " = " + currentInput);

        } catch (NumberFormatException e) {
            tvCalcValue.setText("Error");
        }
    }

    private void performCalculation() {
        if (Double.isNaN(firstValue) || pendingOperation.isEmpty() || currentInput.isEmpty()) {
            return;
        }

        try {
            double secondValue = Double.parseDouble(currentInput);
            double result = 0.0;
            boolean error = false;

            switch (pendingOperation) {
                case "+":
                    result = firstValue + secondValue;
                    break;
                case "-":
                    result = firstValue - secondValue;
                    break;
                case "×":
                    result = firstValue * secondValue;
                    break;
                case "/":
                    if (secondValue == 0) {
                        error = true;
                    } else {
                        result = firstValue / secondValue;
                    }
                    break;
            }

            if (error) {
                tvCalcValue.setText("Error");
                tvCalcResult.setText("");
            } else {
                String formula = format.format(firstValue) + " " + pendingOperation + " " + format.format(secondValue);
                String resultStr = format.format(result);
                tvCalcResult.setText(formula + " =");
                tvCalcValue.setText(resultStr);

                // Save calculation info persistence using SharedPreferences
                saveToHistory(formula + " = " + resultStr);

                currentInput = resultStr;
                firstValue = Double.NaN;
                pendingOperation = "";
            }
        } catch (Exception e) {
            tvCalcValue.setText("Error");
        }
    }

    private void saveToHistory(String stepFormula) {
        String historyStr = "History: " + stepFormula;
        tvCalcHistory.setText(historyStr);

        SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_LAST_CALC, historyStr);
        editor.apply();
    }

    private void clearAll() {
        currentInput = "";
        firstValue = Double.NaN;
        pendingOperation = "";
        tvCalcResult.setText("");
        tvCalcValue.setText("0");
    }

    private void deleteLastCharacter() {
        if (!currentInput.isEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
            tvCalcValue.setText(currentInput.isEmpty() ? "0" : currentInput);
        }
    }
}
