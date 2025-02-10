package com.example.aplikacjeprzemyslowe.utility;
import org.springframework.stereotype.Component;

@Component("utilityClass")
public class UtilityClass {
    public static String formatTwoDecimals(Double value) {
        if (value == null) {
            return "N/A";
        }
        if (value == 0.0){
            return "N/A";
        }
        return String.format("%.2f", value);
    }
}
