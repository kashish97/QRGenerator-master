package androidmads.example;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidmads.example.Utils.constants;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button login;
    String es, ps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.editText2);
        password = findViewById(R.id.editText3);
        login = findViewById(R.id.button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                es = email.getText().toString().trim();
                ps = password.getText().toString().trim();
                create();
            }
        });
    }

    void create(){
        if(es.equalsIgnoreCase("admin@rtek.com")&& ps.equalsIgnoreCase("rtek123")){
            Toast.makeText(getApplicationContext(), "Logged in as admin", Toast.LENGTH_LONG).show();
            constants.sess = "admin";
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        else if(es.equalsIgnoreCase("superadmin@rtek.com")&& ps.equalsIgnoreCase("123rtek")){
            Toast.makeText(getApplicationContext(), "Logged in as Superadmin", Toast.LENGTH_LONG).show();
            constants.sess = "superadmin";
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        else if(es.equalsIgnoreCase("user@rtek.com")&& ps.equalsIgnoreCase("123rtek")){
            Toast.makeText(getApplicationContext(), "Logged in as Superadmin", Toast.LENGTH_LONG).show();
            constants.sess = "user";
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(getApplicationContext(), "Logged in failed", Toast.LENGTH_LONG).show();
        }
    }
}
