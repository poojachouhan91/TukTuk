package utilites;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by SERVER on 6/2/2017.
 */
public interface APIMethods
{

    /**  Driver login **/
    @FormUrlEncoded
    @POST("webservice-driver.php")
    Call<JsonObject> DriverLogin(@Field("requestType") String requestType, @Field("contact") String contact);


    /** Enter Driver OTP **/
    @FormUrlEncoded
    @POST("webservice-driver.php")
    Call<JsonObject> enterotp(@Field("requestType") String driverOtpVerification, @Field("contact") String contact,
                  @Field("driver_otp") String otp);


    /** Enter OTP for new User **/
    @FormUrlEncoded
    @POST("webservice-driver.php")
    Call<JsonObject> enterotpfornewuser(@Field("requestType") String OTPverify, @Field("contact") String contact,
                  @Field("driver_otp") String otp);


    /** Enter Password for registered driver account **/
    @FormUrlEncoded
    @POST("webservice-driver.php")
    Call<JsonObject> driverloginpassword(@Field("requestType") String driverlogin, @Field("contact") String contact,
                            @Field("password") String passwd);



    /** Get Countryid, stateid, vehicle and vehicletype in spinners Driver Detail **/
    @FormUrlEncoded
    @POST("webservice-driver.php")
    Call<JsonObject> getSpinnerData(@Field("requestType") String spinnerlisting, @Field("country_name") String CountryName);


    /** Enter Driver Detail **/
    @FormUrlEncoded
    @POST("webservice-driver.php")
    Call<JsonObject> driverdetail(@Field("requestType") String enterdetail, @Field("first_name") String firstname,
                      @Field("last_name") String lastname, @Field("email") String email,
                      @Field("country_id") String countryid, @Field("state_id") String stateid,
                      @Field("contact") String contact, @Field("password") String password,
                      @Field("city") String city, @Field("vehicle") String vehicle,
                      @Field("vehicle_status") String vehiclestatus, @Field("driver_referral") String Referral);

}
