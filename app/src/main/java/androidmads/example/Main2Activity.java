package androidmads.example;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidmads.example.Adapter.TrackAdapter;
import androidmads.example.Utils.Upload;

public class Main2Activity extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    String lotno;
    public String barcode;
    DatabaseReference databaseReference;

    // Creating RecyclerView.
    public RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.

    // Creating Progress dialog
    public ProgressDialog progressDialog;

    // Creating List of ImageUploadInfo class.
    List<Upload> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Main2Activity.this));

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                Main2Activity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Main2Activity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        barcode =  result.getText().toString();
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(barcode);
                            JSONObject jsonObject = jsonArray.optJSONObject(0);
                            lotno = jsonObject.optString("lot_no");

                            if(barcode!=null){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                                builder.setTitle("Scan Result");
                                builder.setIcon(R.mipmap.ic_launcher);
                                builder.setMessage("" + barcode);
                                final AlertDialog alert1 = builder.create();
                                alert1.setButton(DialogInterface.BUTTON_POSITIVE, "Done", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alert1.dismiss();
                                        progressDialog = new ProgressDialog(Main2Activity.this);
                                        progressDialog.setMessage("Loading");
                                        progressDialog.show();
                                        databaseReference = FirebaseDatabase.getInstance().getReference("update").child(lotno).child("update");
                                        databaseReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {

                                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                                                    Upload upload = postSnapshot.getValue(Upload.class);

                                                    list.add(upload);
                                                }

                                                TrackAdapter adapter = new TrackAdapter(Main2Activity.this.getApplicationContext(), list);

                                                recyclerView.setAdapter(adapter);

                                                progressDialog.dismiss();
                                            }



                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                                Toast.makeText(Main2Activity.this, "Error Loading events", Toast.LENGTH_LONG).show();
                                                progressDialog.dismiss();

                                            }
                                        });


                                    }
                                });

                                alert1.setCanceledOnTouchOutside(false);

                                alert1.show();}
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                                builder.setTitle("Scan Result");
                                builder.setIcon(R.mipmap.ic_launcher);
                                builder.setMessage("Nothing found try a different image or try again");
                                AlertDialog alert1 = builder.create();
                                alert1.setButton(DialogInterface.BUTTON_POSITIVE, "Done", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent (Main2Activity.this,HomeActivity.class);
                                        startActivity(i);
                                    }
                                });

                                alert1.setCanceledOnTouchOutside(false);

                                alert1.show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}
