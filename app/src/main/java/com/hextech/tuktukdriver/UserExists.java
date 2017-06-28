package com.hextech.tuktukdriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utilites.APIMethods;
import utilites.ServerConnection;

/**
 * Created by SERVER on 5/29/2017.
 */
public class UserExists extends AppCompatActivity {
    EditText pass;
    Button SubmitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);
        setTitle("Enter Password");

        SubmitBtn = (Button) findViewById(R.id.submit_btn);
        pass = (EditText) findViewById(R.id.password);

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.colorOrange)));

        setStatusBarColor();

        //actionbar.setDisplayHomeAsUpEnabled(true);
        final String contact = SingleTon.getSelectContact();

        SubmitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if((!(pass.getText().toString().equals(""))) && (!(pass.getText().toString().equals(null)))) {
                        if (InternetConnection.checkinternetconnection(UserExists.this)) {
                            DriverLoginRetrofit();
                        }
                    }else{
                        Toast.makeText(UserExists.this, "Password Incorrect",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

            public void DriverLoginRetrofit(){

                final ProgressDialog dailog = ProgressDialog.show(this, "", "Loading...");
                dailog.setCancelable(false);

                String CompleteNumber = SingleTon.getSelectCountrycode() + SingleTon.getSelectContact();

                APIMethods builder = ServerConnection.getClient().create(APIMethods.class);
                final Call<JsonObject> call = builder.driverloginpassword("driverLogin", CompleteNumber,
                        pass.getText().toString());
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject jsonObject = response.body();
                        dailog.dismiss();
                        if (jsonObject.get("type").getAsString().equals("1")) {
                            // TODO Auto-generated method stub
                            Toast.makeText(UserExists.this, jsonObject.get("status").getAsString(),
                                    Toast.LENGTH_SHORT).show();
                            Intent show = new Intent(UserExists.this, CurrentLocMap.class);
                            startActivity(show);
                        } else {
                            Toast.makeText(UserExists.this, jsonObject.get("status").getAsString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        dailog.dismiss();
                        Toast.makeText(UserExists.this, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
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
}