package com.hextech.tuktukdriver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utilites.APIMethods;
import utilites.ApiClient;
import utilites.ServerConnection;

/**
 * Created by SERVER on 5/29/2017.
 */

public class Document_Upload_Fragment extends Fragment {

    View rootView;
    Button submitBtn;
    ImageView LicenceDoc, PhotoDoc, PassbookDoc, RCdoc;

    ArrayList<Image> DiverLicence;
    ArrayList<Image> DriverPhoto;
    ArrayList<Image> Passbook;
    ArrayList<Image> DriverRC;

    ArrayList<Uri> DiverLicence_uri;
    ArrayList<Uri> DriverPhoto_uri;
    ArrayList<Uri> Passbook_uri;
    ArrayList<Uri> DriverRC_uri;

    int OpenView = 0;
    private Uri realUri;
    private String TAG = Document_Upload_Fragment.class.getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_upload_document, container, false);
        submitBtn = (Button) rootView.findViewById(R.id.submit_btn);
        LicenceDoc = (ImageView) rootView.findViewById(R.id.licence_doc);
        PhotoDoc = (ImageView) rootView.findViewById(R.id.photo_doc);
        PassbookDoc = (ImageView) rootView.findViewById(R.id.passbook_doc);
        RCdoc = (ImageView) rootView.findViewById(R.id.rc_doc);

        getActivity().setTitle("Upload Document");

        LicenceDoc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PicDocRetrofit(2);
              //CaptureRetrofit();
                OpenView =0;
            }
        });

        PhotoDoc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PicDocRetrofit(1);
               // CaptureRetrofit();
                OpenView = 1;
            }
        });

        PassbookDoc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PicDocRetrofit(2);
              //  CaptureRetrofit();
                OpenView = 2;
            }
        });

        RCdoc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PicDocRetrofit(2);
             //   CaptureRetrofit();
                OpenView = 3;
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if((DiverLicence_uri != null) && (DriverPhoto_uri != null) && (Passbook_uri != null) && (DriverRC_uri != null)) {
                    AllDocumentRetrofit();
                }else {
                    Toast.makeText(getActivity(), "Please upload all document",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
      return rootView;
    }

      public void AllDocumentRetrofit() {

          final ProgressDialog dailog = ProgressDialog.show(getActivity(), "", "Uploading...");
          dailog.setCancelable(false);

          Map<String, RequestBody> params = new HashMap<>();
          params.put("requestType", ApiClient.createRequestBody("driverDocument"));
          params.put("driver_id", ApiClient.createRequestBody("1"));

          Map<String, RequestBody> files = new HashMap<>();
          for (int pos = 0; pos < DiverLicence_uri.size(); pos++) {
              if (!TextUtils.isEmpty(DiverLicence_uri.get(pos).getPath())) {
                  RequestBody requestBody = ApiClient.createRequestBody(new File(DiverLicence_uri.get(pos).getPath()));
                  // fix is right here
                  String key = String.format("driving_l[]\", filename=\"%1$s", "photo_" + String.valueOf(pos + 1));
                  files.put(key, requestBody);
              }
          }

          for (int pos = 0; pos < DriverPhoto_uri.size(); pos++) {
              if (!TextUtils.isEmpty(DriverPhoto_uri.get(pos).getPath())) {
                  RequestBody requestBody = ApiClient.createRequestBody(new File(DriverPhoto_uri.get(pos).getPath()));
                  // fix is right here
                  String key = String.format("driving_p[]\", filename=\"%1$s", "photo_" + String.valueOf(pos + 1));
                  files.put(key, requestBody);
              }
          }

          for (int pos = 0; pos < Passbook_uri.size(); pos++) {
              if (!TextUtils.isEmpty(Passbook_uri.get(pos).getPath())) {
                  RequestBody requestBody = ApiClient.createRequestBody(new File(Passbook_uri.get(pos).getPath()));
                  // fix is right here
                  String key = String.format("pass_book[]\", filename=\"%1$s", "photo_" + String.valueOf(pos + 1));
                  files.put(key, requestBody);
              }
          }

          for (int pos = 0; pos < DriverRC_uri.size(); pos++) {
              if (!TextUtils.isEmpty(DriverRC_uri.get(pos).getPath())) {
                  RequestBody requestBody = ApiClient.createRequestBody(new File(DriverRC_uri.get(pos).getPath()));
                  // fix is right here
                  String key = String.format("vehicle_rc[]\", filename=\"%1$s", "photo_" + String.valueOf(pos + 1));
                  files.put(key, requestBody);
              }
          }

          ApiClient.FileUploadService builder = ServerConnection.doUpload().create(ApiClient.FileUploadService.class);
          final Call<JsonObject> call = builder.driverdocumentupload(params, files);

             call.enqueue(new Callback<JsonObject>() {
                 @Override
                 public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                     JsonObject jsonObject = response.body();
                     dailog.dismiss();
                     if (jsonObject.get("type").getAsString().equals("1")) {
                         // TODO Auto-generated method stub
                         Intent show = new Intent(getActivity(), CurrentLocMap.class);
                         startActivity(show);
                     } else {
                         Toast.makeText(getActivity(), "Test",
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantsCustomGallery.REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // The array list has the image paths of the selected images
            ArrayList<Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
            handleResponseIntent(data);
            for (int i = 0; i < images.size(); i++) {
                Uri realUri = Uri.fromFile(new File(images.get(i).path));
                System.out.println("url >>." + realUri);
                // start play with image uri
            }
        }
    }

    public void PicDocRetrofit(int max) {
        Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
        intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, max); // set limit for image selection
        startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
    }

//    public void CaptureRetrofit() {
//        Intent intent = new Intent(getActivity(), MultiCameraActivity.class);
//        Params params = new Params();
//        params.setCaptureLimit(10);
//        params.setToolbarColor(R.color.colorOrange);
//        params.setActionButtonColor(R.color.colorOrange);
//        params.setButtonTextColor(R.color.colorOrange);
//        intent.putExtra(Constants.KEY_PARAMS, params);
//        startActivityForResult(intent, Constants.TYPE_MULTI_CAPTURE);
//    }

    private void handleResponseIntent(Intent intent) {

        if(OpenView == 0) {
            DiverLicence = intent.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
            DiverLicence_uri = new ArrayList<Uri>();
            for (int i = 0; i < DiverLicence.size(); i++) {
                Uri realUri = Uri.fromFile(new File(DiverLicence.get(i).path));
                DiverLicence_uri.add(realUri);
            }
            LicenceDoc.setImageResource(R.mipmap.upload_img);
        }else if(OpenView == 1) {
            DriverPhoto = intent.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
            DriverPhoto_uri = new ArrayList<Uri>();
            for (int i = 0; i < DriverPhoto.size(); i++) {
                Uri realUri = Uri.fromFile(new File(DriverPhoto.get(i).path));
                DriverPhoto_uri.add(realUri);
            }
            PhotoDoc.setImageResource(R.mipmap.upload_img);
        }else if(OpenView == 2) {
            Passbook = intent.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
            Passbook_uri = new ArrayList<Uri>();
            for (int i = 0; i < Passbook.size(); i++) {
                Uri realUri = Uri.fromFile(new File(Passbook.get(i).path));
                Passbook_uri.add(realUri);
            }
            PassbookDoc.setImageResource(R.mipmap.upload_img);
        }else if(OpenView == 3){
            DriverRC = intent.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
            DriverRC_uri = new ArrayList<Uri>();
            for (int i = 0; i < DriverRC.size(); i++) {
                Uri realUri = Uri.fromFile(new File(DriverRC.get(i).path));
                DriverRC_uri.add(realUri);
            }
            RCdoc.setImageResource(R.mipmap.upload_img);
        }
    }
}