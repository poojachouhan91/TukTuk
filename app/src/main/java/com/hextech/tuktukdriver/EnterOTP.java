package com.hextech.tuktukdriver;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utilites.APIMethods;
import utilites.ServerConnection;

/**
 * Created by SERVER on 5/23/2017.
 */

public class EnterOTP extends AppCompatActivity  {

    private SmsVerifyCatcher smsVerifyCatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Enter OTP");
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.colorOrange)));

        setStatusBarColor();

        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                EnterOTPFragment.fourotp_field.setText(code);
                AutoFillOtp(code);
                // etCode.setText(code);//set code in edit text
                //then you can send verification code to server
            }
        });
       // actionbar.setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            Fragment newFragment = new EnterOTPFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.container, newFragment).commit();
        }
    }

    public void AutoFillOtp(String otp){
        if (InternetConnection.checkinternetconnection(this)) {
                doEnterOTPRetrofit(otp);
        } else {
            Toast.makeText(this, "Internet connection is not working",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void doEnterOTPRetrofit(String otp) {
        final ProgressDialog dailog = ProgressDialog.show(this, "", "Loading...");
        dailog.setCancelable(true);

        String CompeleteNumber = SingleTon.getSelectCountrycode()+ SingleTon.getSelectContact();

        APIMethods builder = ServerConnection.getClient().create(APIMethods.class);
        final Call<JsonObject> call = builder.enterotp("driverOtpVerification", CompeleteNumber, otp);

        call.enqueue(new Callback<JsonObject>() {
            @Override
               public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject jsonObject = response.body();
                dailog.dismiss();
                if (jsonObject.get("type").getAsString().equals("1")) {
                    // TODO Auto-generated method stub
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.container, new Driver_Detail_Fragment(), "fragment_screen");
                    ft.addToBackStack(null);
                    ft.commit();

//                    Intent show = new Intent(EnterOTP.this, CurrentLocMap.class);
//                    startActivity(show);

                    Toast.makeText(EnterOTP.this, jsonObject.get("status").getAsString(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EnterOTP.this, jsonObject.get("status").getAsString(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dailog.dismiss();
                Toast.makeText(EnterOTP.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
       // setTitle("Enter OTP");
    }

    @Override
    protected void onPause() {
        super.onPause();
        smsVerifyCatcher.onStop();
    }

    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void setStatusBarColor() {
        Window window =  getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorOrange));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class EnterOTPFragment extends Fragment {
        View rootView;
        Button submitBtn;
        static PinView fourotp_field;
        String DriverContact, CountryCode;

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.activity_enter_otp, container, false);
            submitBtn = (Button) rootView.findViewById(R.id.submit_btn);
            fourotp_field = (PinView) rootView.findViewById(R.id.pinView_otp);
            DriverContact = SingleTon.getSelectContact();
            CountryCode = SingleTon.getSelectCountrycode();

            submitBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                        if (InternetConnection.checkinternetconnection(getActivity())) {
                                if((fourotp_field.getText().toString().length()  == 4)) {
                                    EnterOTPRetrofill();
                                }else{
                                    Toast.makeText(getActivity(), "Please enter the otp",
                                            Toast.LENGTH_SHORT).show();
                                }
                         }else {
                        Toast.makeText(getActivity(), "Internet connection is not working",
                                Toast.LENGTH_SHORT).show();
                         }
                    }
                });
            return rootView;
        }

        public void EnterOTPRetrofill() {

            final ProgressDialog dailog = ProgressDialog.show(getActivity(), "", "Loading...");
            dailog.setCancelable(false);

            String CompleteNumber = CountryCode + SingleTon.getSelectContact();

            APIMethods builder = ServerConnection.getClient().create(APIMethods.class);
            final Call<JsonObject> call = builder.enterotpfornewuser("driverOtpVerification", CompleteNumber,
                    fourotp_field.getText().toString());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    JsonObject jsonObject = response.body();
                    dailog.dismiss();
                    if (jsonObject.get("type").getAsString().equals("1")) {
                        // TODO Auto-generated method stub
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.container, new Driver_Detail_Fragment(), "fragment_screen");
                        ft.addToBackStack(null);
                        ft.commit();
                    } else {
                        Toast.makeText(getActivity(), jsonObject.get("status").getAsString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    dailog.dismiss();
                    Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}