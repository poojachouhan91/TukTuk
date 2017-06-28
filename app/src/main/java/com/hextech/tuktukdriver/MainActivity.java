package com.hextech.tuktukdriver;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonObject;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.CountryPickerListener;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utilites.APIMethods;
import utilites.ServerConnection;

public class MainActivity extends FragmentActivity {

    String CountryCode = "+91";
    String CountryName = "India";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            Fragment newFragment = new LoginViewFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.container, newFragment).commit();
        }
    }

    public class LoginViewFragment extends Fragment {
        private View rootView;
        Button LoginBtn;
        EditText MOB;
        TextView Country;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_login, container, false);
            LoginBtn = (Button)rootView.findViewById(R.id.login_btn);
            MOB = (EditText)rootView.findViewById(R.id.edit_mob);
            Country = (TextView) rootView.findViewById(R.id.country_code);
            setStatusBarTranslucent(true);
            MOB.setText("9988643917");

            Country.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
                    picker.setListener(new CountryPickerListener() {
                        @Override
                        public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                                    picker.dismiss();
                                    CountryCode = (dialCode.toString());
                                    CountryName = (name.toString());
                                    Country.setText(CountryCode);
                        }
                    });
                    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                }
            });

            LoginBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if((MOB.getText().toString()).length() == 10) {
                        if (InternetConnection.checkinternetconnection(getActivity())) {
                          dologinRetrofit();
                              }
                    } else {
                        Toast.makeText(getActivity(), "Please enter the correct number",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return rootView;
        }

        public void dologinRetrofit()  {

            final ProgressDialog dailog = ProgressDialog.show(getActivity(), "", "Loading...");
            dailog.setCancelable(false);

            String CompleteNumber = CountryCode + MOB.getText().toString();

            APIMethods builder = ServerConnection.getClient().create(APIMethods.class);
            final Call<JsonObject> call = builder.DriverLogin("driverStatus", CompleteNumber);

           call.enqueue(new Callback<JsonObject>() {
               @Override
               public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                   dailog.dismiss();
                   JsonObject jsonObject = response.body();
                   SingleTon.setSelectContact(MOB.getText().toString());
                   SingleTon.setSelectCountrycode(CountryCode);
                   SingleTon.setSelectCountryName(CountryName);
                   if (jsonObject.get("type").getAsString().equals("1")) {
                    // TODO Auto-generated method stub
                    Intent show = new Intent(getActivity(), UserExists.class);
                    startActivity(show);
                   } else {
                    Intent show = new Intent(getActivity(), EnterOTP.class);
                    startActivity(show);
                   }
               }

               @Override
               public void onFailure(Call<JsonObject> call, Throwable t) {
                   Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
                   dailog.dismiss();
               }
           });
        }

        protected void setStatusBarTranslucent(boolean makeTranslucent) {
            if (makeTranslucent) {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }
}