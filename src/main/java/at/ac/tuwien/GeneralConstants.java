package at.ac.tuwien;

import java.util.ArrayList;
import java.util.List;

public class GeneralConstants {

    public static List<String> getBlacklistedKeys() {
        List<String> alreadyListet = new ArrayList<String>();
        alreadyListet.add("UUID");
        alreadyListet.add("birthday_year");
        alreadyListet.add("birthday_month_alpha");
        alreadyListet.add("birthday_month_without_null");
        alreadyListet.add("birthday_date_without_null");
        alreadyListet.add("birthday_date");
        alreadyListet.add("birthday_month");
        return alreadyListet;
    }

    public static List<String> getRequiredKeys() {
        List<String> alreadyListet = new ArrayList<String>();
        alreadyListet.add("prename");
        alreadyListet.add("surname");
        alreadyListet.add("email");
        alreadyListet.add("password");
        alreadyListet.add("birthday");
        alreadyListet.add("UUID");
        return alreadyListet;
    }

}
