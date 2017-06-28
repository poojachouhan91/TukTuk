package com.hextech.tuktukdriver;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utilites.APIMethods;
import utilites.ServerConnection;

/**
 * Created by SERVER on 5/25/2017.
 */
public class Driver_Detail_Fragment extends Fragment{

    private View rootView;
    Button submitBtn;
    String[] EnterData;
    EditText FirstName, LastName, Email, Contact, Password, City, ReferralCode;
    Spinner Vehicle_spinner, VehicleStatus_spinner, State_spinner;
    String DriverContact, StateId, VehicleID, VehicleStatusID, CountryID, Country;
    ArrayAdapter<String> adapterState;
    ArrayAdapter<String> adapterVehicle;
    ArrayAdapter<String> adapterVehicleStatus;

    ArrayList<EditText> Array_DriverDetail = new ArrayList<EditText>();

    ArrayList<String> array_state = new ArrayList<String>();
    ArrayList<String> array_stateId = new ArrayList<String>();
    ArrayList<String> array_Vehicle_spinner = new ArrayList<String>();
    ArrayList<String> array_Vehicle_spinnerID = new ArrayList<String>();
    ArrayList<String> array_VehicleStatus_spinner = new ArrayList<String>();
    ArrayList<String> array_VehicleStatus_spinnerID = new ArrayList<String>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_drive_detail, container, false);

        submitBtn = (Button) rootView.findViewById(R.id.submit_btn);

        FirstName = (EditText) rootView.findViewById(R.id.first_name);
        Array_DriverDetail.add(FirstName);
        LastName = (EditText) rootView.findViewById(R.id.second_name);
        Array_DriverDetail.add(LastName);
        Email = (EditText) rootView.findViewById(R.id.email);
        Array_DriverDetail.add(Email);
        State_spinner = (Spinner) rootView.findViewById(R.id.state);
        City = (EditText) rootView.findViewById(R.id.city);
        Array_DriverDetail.add(City);
        Contact = (EditText) rootView.findViewById(R.id.contact);
        Password = (EditText) rootView.findViewById(R.id.password);
        Array_DriverDetail.add(Password);
        Vehicle_spinner = (Spinner) rootView.findViewById(R.id.vehicle);
        VehicleStatus_spinner = (Spinner) rootView.findViewById(R.id.vehicle_status);
        ReferralCode = (EditText) rootView.findViewById(R.id.referral);

        EnterData = getResources().getStringArray(R.array.DriverDetailArray);

        DriverContact = SingleTon.getSelectCountrycode() +"-"+ SingleTon.getSelectContact();
        Contact.setText(DriverContact);

        Country = SingleTon.getSelectCountryName();

        getActivity().setTitle("Driver Detail");

        if (InternetConnection.checkinternetconnection(getActivity())) {
                GetSpinnerDataRetrofit();
        } else {
            Toast.makeText(getActivity(), "Internet connection is not working",
                    Toast.LENGTH_SHORT).show();
        }

        adapterState = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, array_state);
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        State_spinner.setAdapter(adapterState);
       

       State_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               if(!(array_stateId.isEmpty())){
                   StateId = array_stateId.get(position);
               }
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });

        adapterVehicle = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, array_Vehicle_spinner);
        adapterVehicle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Vehicle_spinner.setAdapter(adapterVehicle);

        Vehicle_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!(array_Vehicle_spinnerID.isEmpty())) {
                    VehicleID = array_Vehicle_spinnerID.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapterVehicleStatus = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, array_VehicleStatus_spinner);
        adapterVehicleStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        VehicleStatus_spinner.setAdapter(adapterVehicleStatus);

        VehicleStatus_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!(array_VehicleStatus_spinnerID.isEmpty())) {
                    VehicleStatusID = array_VehicleStatus_spinnerID.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    Boolean AllDataFiled = true;
                for(int i = 0; i < Array_DriverDetail.size(); i++){
                    if((Array_DriverDetail.get(i).getText().toString().equals(""))){
                        AllDataFiled = false;
                        String error = "Please Enter " + EnterData[i];
                        Toast.makeText(getActivity(), error,
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if(AllDataFiled) {
                    if (InternetConnection.checkinternetconnection(getActivity())) {
                        DriverDetailRetrofit();
                    } else {
                        Toast.makeText(getActivity(), "Internet connection is not working",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return rootView;
    }

    public void GetSpinnerDataRetrofit(){

        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Loading...");
        dialog.setCancelable(false);

        APIMethods builder = ServerConnection.getClient().create(APIMethods.class);
        final Call<JsonObject> call = builder.getSpinnerData("masterListing", Country);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                dialog.dismiss();
                if (jsonObject.get("type").getAsString().equals("1")) {
                    // TODO Auto-generated method stub
                    JsonObject jsonArray = jsonObject.getAsJsonObject("master_listing");
                    JsonArray jsonCountry = jsonArray.getAsJsonArray("country");
                    JsonArray jsonVehicle = jsonArray.getAsJsonArray("vehicle");
                    JsonArray jsonVehicleStatus = jsonArray.getAsJsonArray("vehicle_status");

                    for (int i = 0; i < jsonCountry.size(); i++) {
                        JSONObject jsonObject2;
                        try {
                            jsonObject2 = new JSONObject(jsonCountry.get(i).toString());
                            CountryID = (jsonObject2.getString("country_id"));
                            JSONArray jsonState = jsonObject2.getJSONArray("states");
                            for (int k = 0; k < jsonState.length(); k++) {
                                JSONObject jsonObject3;
                                try {
                                    jsonObject3 = new JSONObject(jsonState.get(k).toString());
                                    array_stateId.add(jsonObject3.getString("state_id"));
                                    array_state.add(jsonObject3.getString("state_name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    for (int l = 0; l < jsonVehicle.size(); l++) {
                        try {
                            JSONObject jsonObject4 = new JSONObject(jsonVehicle.get(l).toString());
                            array_Vehicle_spinnerID.add(jsonObject4.getString("vehicle_id"));
                            array_Vehicle_spinner.add(jsonObject4.getString("vehicle_type"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    for (int m = 0; m < jsonVehicleStatus.size(); m++) {
                        try {
                            JSONObject jsonObject5 = new JSONObject(jsonVehicleStatus.get(m).toString());
                            array_VehicleStatus_spinnerID.add(jsonObject5.getString("vehicle_status_id"));
                            array_VehicleStatus_spinner.add(jsonObject5.getString("vehicle_status"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapterState.notifyDataSetChanged();
                    adapterVehicle.notifyDataSetChanged();
                    adapterVehicleStatus.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Test",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public void DriverDetailRetrofit() {

        final ProgressDialog dailog = ProgressDialog.show(getActivity(), "", "Loading...");
        dailog.setCancelable(false);

        String CompleteNumber = SingleTon.getSelectCountrycode() + SingleTon.getSelectContact();

        APIMethods builder = ServerConnection.getClient().create(APIMethods.class);
        final Call<JsonObject> call = builder.driverdetail("driverSignUp", FirstName.getText().toString(), LastName.getText().toString(),
                Email.getText().toString(), CountryID, StateId, CompleteNumber, Password.getText().toString(),
                City.getText().toString(), VehicleID, VehicleStatusID, ReferralCode.getText().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                dailog.dismiss();
                if (jsonObject.get("type").getAsString().equals("1")) {
                    // TODO Auto-generated method stub
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.container, new Document_Upload_Fragment(), "fragment_screen");
                    ft.addToBackStack(null);
                    ft.commit();
                        Toast.makeText(getActivity(), jsonObject.get("status").getAsString(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), jsonObject.get("status").getAsString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
                dailog.dismiss();
            }
        });
    }
//    public final static boolean isValidEmail(CharSequence target) {
//        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
//    }
}