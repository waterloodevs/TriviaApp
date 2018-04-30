package waterloodevs.triviaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//import android.app.ProgressDialog;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    //defining view objects
    private EditText input_name;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup;
    private TextView textViewSignin;
    private EditText editTextWalletAddress;
    private DatabaseReference database;

    //private ProgressDialog progressDialog;

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initializing firebase auth and datbase object
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        //if getCurrentUser does not return null
        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), CountDownActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }

        //initializing views
        input_name = (EditText) findViewById(R.id.input_name);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);
        buttonSignup = (Button) findViewById(R.id.buttonSignup);
        editTextWalletAddress = (EditText) findViewById(R.id.editTextWalletAddress);

        //progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    private void registerUser(){

        //getting email, name, password and wallet address from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
        String name = input_name.getText().toString().trim();
        String walletaddress  = editTextWalletAddress.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter your email",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your password",Toast.LENGTH_LONG).show();
            return;
        }
        //checking if email and passwords are empty
        if(TextUtils.isEmpty(walletaddress)){
            Toast.makeText(this,"Please enter your public ethereum address",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        //progressDialog.setMessage("Registering Please Wait...");
        //progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            //Add to database
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            UserInfo userinfo = new UserInfo(name, email, password, walletaddress);
                            database.child(user.getUid()).setValue(userinfo);
                            //Move along
                            startActivity(new Intent(getApplicationContext(), CountDownActivity.class));
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }else{
                            //display some message here
                            Toast.makeText(RegisterActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        //progressDialog.dismiss();
                    }
                });

        //Store public address and initial balance of 0 to database

    }

    @Override
    public void onClick(View view) {

        if(view == buttonSignup){
            registerUser();
        }
        if(view == textViewSignin){
            //open login activity
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }
}
