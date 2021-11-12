package com.lockhart.yoursbank.Support;

import android.text.Spanned;
import android.util.Log;

public class AmountFilter implements android.text.InputFilter {

    private final int max;

    public AmountFilter(Long max) {
        this.max = Integer.parseInt(String.valueOf(max));
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(max, input))
                return null;
        } catch (Exception e) {
            Log.d("FILTER", e.toString());
        }
        return "";
    }

    private boolean isInRange(int b, int c) {
        return b > 0 ? c >= 0 && c <= b : c >= b && c <= 0;
    }
}