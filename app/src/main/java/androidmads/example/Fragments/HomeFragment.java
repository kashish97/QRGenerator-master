package androidmads.example.Fragments;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidmads.example.LoginActivity;
import androidmads.example.NewActivity;
import androidmads.example.R;
import androidmads.example.Utils.Upload;
import androidmads.example.Utils.constants;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.WINDOW_SERVICE;

public class HomeFragment extends Fragment {

    String TAG = "GenerateQRCode";
    EditText edtValue;
    ImageView qrImage;
    Button start, save;
    String inputValue;
    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    private static final int PICK_IMAGE_REQUEST = 234;

    private Button buttonSubmit;
    private Button buttonChoose;
    private Button btnretr;


    private EditText eventName;
    private EditText eventType;
    private EditText eventDesc;
    private ImageView imageEvent;
    private Uri filePath;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private DatabaseReference mDatabase;

    int Image_Request_Code = 7;


    public HomeFragment() {
        // Required empty public constructor
    }
    void init() {

        progressDialog = new ProgressDialog(getActivity());

        storageReference = FirebaseStorage.getInstance().getReference("image");
        mDatabase = FirebaseDatabase.getInstance().getReference("data");



        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Creating intent.
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);

            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Calling method to upload selected image on Firebase storage.
                UploadImageFileToFirebaseStorage();

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);

                // Setting up bitmap selected image into ImageView.
                imageEvent.setImageBitmap(bitmap);

                // After selecting image change choose button above text.
                buttonChoose.setText("Image Selected");

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }


    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (filePath != null) {

            // Setting progressDialog Title.
            progressDialog.setTitle("Image is Uploading...");

            // Showing progressDialog.
            progressDialog.show();

            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child("image" + System.currentTimeMillis() + "." + getFileExtension(filePath));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Getting image name from EditText and store into string variable.
                            String TempeventName = eventName.getText().toString().trim();
                            String TempType = eventType.getText().toString().trim();
                            String TempDesc = eventDesc.getText().toString().trim();


                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();

                            // Showing toast message after done uploading.
                            Toast.makeText(getActivity()

                                    , "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();


                            @SuppressWarnings("VisibleForTests")
                            Upload uploadInfo = new Upload(TempeventName,TempType, TempDesc, taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());

                            // Getting image upload ID.
                            String eventId = mDatabase.push().getKey();

                            // Adding image upload id s child element into databaseReference.
                            mDatabase.child(eventId).setValue(uploadInfo);
                            JSONObject jsonObject = new JSONObject();
                            JSONArray ja = new JSONArray();

                            try {
                                jsonObject.put("lot_no", eventId);
                                jsonObject.put("location", TempeventName);
                                jsonObject.put("date", TempType);
                                jsonObject.put("qty", TempDesc);
                                ja.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(getActivity(), NewActivity.class);
                            intent.putExtra("lotno", eventId);
                            intent.putExtra("value", ja.toString());
                            startActivity(intent);

                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            progressDialog.setTitle("Image is Uploading...");

                        }
                    });
        }
        else {

            Toast.makeText(getActivity(), "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }




    public String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        if(!constants.sess.equalsIgnoreCase("admin")){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        buttonSubmit = (Button) v.findViewById(R.id.btnEvent);
        buttonChoose = (Button) v.findViewById(R.id.btnChoose);
        init();
        imageEvent = (ImageView) v.findViewById(R.id.eventt);

        eventName = (EditText) v.findViewById(R.id.txtname);
        eventType = (EditText) v.findViewById(R.id.txttype);
        eventDesc = (EditText) v.findViewById(R.id.txtdesc);


        progressDialog = new ProgressDialog(getActivity());
        qrImage = (ImageView) v.findViewById(R.id.QR_Image);
        edtValue = (EditText) v.findViewById(R.id.edt_value);
        start = (Button) v.findViewById(R.id.start);
        save = (Button) v.findViewById(R.id.save);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputValue = edtValue.getText().toString().trim()+ "Hello world bye world";
                if (inputValue.length() > 0) {
                    WindowManager manager = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerDimension = width < height ? width : height;
                    smallerDimension = smallerDimension * 3 / 4;

                    qrgEncoder = new QRGEncoder(
                            inputValue, null,
                            QRGContents.Type.TEXT,
                            smallerDimension);
                    try {
                        bitmap = qrgEncoder.encodeAsBitmap();
                        qrImage.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        Log.v(TAG, e.toString());
                    }
                } else {
                    edtValue.setError("Required");
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean save;
                String result;
                try {
                    save = QRGSaver.save(savePath, edtValue.getText().toString().trim(), bitmap, QRGContents.ImageType.IMAGE_JPEG);
                    result = save ? "Image Saved" : "Image Not Saved";
                    Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
    }

}
