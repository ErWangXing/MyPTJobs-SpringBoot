package com.MyPTJobs.Class;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class function {
    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    public static String getCurrentDate(){
        LocalDate currentDate = LocalDate.now();

        // Format the current date to match the format of the jobDate field
        String currentDateFormatted = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return currentDateFormatted;
    }
}
