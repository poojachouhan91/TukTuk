package com.hextech.tuktukdriver;

/**
 * Created by SERVER on 5/29/2017.
 */
public class SingleTon {

    static String SelectContact = "", SelectCountrycode = "", SelectCountryName = "";

    public static String getAppUrl(){
        return "http://tuktuk.hextechnologies.in/";
    }

    /*** Driver Contact Number  ***/
    public static void setSelectContact(String selectcontact) {
        SelectContact = selectcontact;
    }

    public static String getSelectContact() {
        return SelectContact;
    }

    /*** Country Code  ***/
    public static void setSelectCountrycode(String selectcountrycode) {
        SelectCountrycode = selectcountrycode;
    }

    public static String getSelectCountrycode() {
        return SelectCountrycode;
    }

    /*** Country Name  ***/
    public static void setSelectCountryName(String selectcountryName) {
        SelectCountryName = selectcountryName;
    }

    public static String getSelectCountryName() {
        return SelectCountryName;
    }

}