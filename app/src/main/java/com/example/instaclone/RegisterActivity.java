package com.example.instaclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText username,name,password,email,phone;
    private TextView login;
    private Button loginuser;


    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        username=findViewById(R.id.username);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        loginuser=findViewById(R.id.register);
        login=findViewById(R.id.login_user);
        phone=findViewById(R.id.phone);



        mRootRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });



        loginuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUsername=username.getText().toString();
                String txtName=name.getText().toString();
                String txtEmail=email.getText().toString();
                String txtPassword=password.getText().toString();
                String txtPhone=phone.getText().toString();

                if(TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtName) || TextUtils.isEmpty(txtPassword) || TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtPhone)){
                    Toast.makeText(RegisterActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                }
                else if(txtPassword.length()<6){
                    Toast.makeText(RegisterActivity.this, "Password To short", Toast.LENGTH_SHORT).show();
                }
                else if(txtPhone.length() !=10){
                    Toast.makeText(RegisterActivity.this, "Enter valid 10 digit Number", Toast.LENGTH_SHORT).show();
                }
                else {
                    registeruser(txtUsername,txtName,txtEmail,txtPassword,txtPhone);
                }

            }
        });





    }

    private void registeruser(final String Username, final String Name, final String Email, String Password,final String PhoneN) {

        pd.setMessage("Please wait!");
        pd.show();

        mAuth.createUserWithEmailAndPassword(Email,Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String,Object> map=new HashMap<>();
                map.put("name",Name);
                map.put("email",Email);
                map.put("username",Username);
                map.put("phone",PhoneN);
                map.put("bio","");
                map.put("imageurl","default");
                map.put("id",mAuth.getCurrentUser().getUid());

                mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "Update the profile"+
                                    " for Better expereince", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(RegisterActivity.this,Main2Activity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
