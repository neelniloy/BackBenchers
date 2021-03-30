package com.sarker.backbenchersextended;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sarker.backbenchersextended.model.CourseInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class NeedBlood extends AppCompatActivity {

    private EditText etBloodGroup;
    private EditText etQuantity;
    private EditText etLocation;
    private EditText etContact;
    private EditText etDeadline;
    private String current_user_id, classCode,temp = " ";
    private EditText mEditTextMessage;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef, emailRef;
    private ImageView back;
    private String adminEmail, adminPass,bloodGroup,quantity,location,deadline,messages,contact;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_need_blood);

        etBloodGroup = findViewById(R.id.et_blood_group);
        etQuantity = findViewById(R.id.et_quantity);
        etLocation = findViewById(R.id.et_location);
        etContact = findViewById(R.id.et_contact_number);
        etDeadline = findViewById(R.id.et_deadline);

        mEditTextMessage = findViewById(R.id.edit_text_message);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();

        Intent i = getIntent();
        classCode = i.getStringExtra("classCode");

        userRef = FirebaseDatabase.getInstance().getReference().child("UsersData");
        emailRef = FirebaseDatabase.getInstance().getReference("AdminEmail");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if(dataSnapshot.exists()){


                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        temp += dataSnapshot.child(postSnapshot.getKey()).child("email").getValue().toString()+" , ";

                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NeedBlood.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        emailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                adminEmail = snapshot.child("email").getValue(String.class);
                adminPass = snapshot.child("password").getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button buttonSend = findViewById(R.id.btn_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bloodGroup = etBloodGroup.getText().toString();
                quantity = etQuantity.getText().toString();
                location = etLocation.getText().toString();
                contact = etContact.getText().toString();
                deadline = etDeadline.getText().toString();
                messages = mEditTextMessage.getText().toString();

                if (bloodGroup.isEmpty()) {
                    etBloodGroup.setError("empty blood group");
                    etBloodGroup.requestFocus();
                } else if (quantity.isEmpty()) {
                    etQuantity.setError("empty quantity");
                    etQuantity.requestFocus();
                } else if (location.isEmpty()) {
                    etLocation.setError("empty location");
                    etLocation.requestFocus();
                } else if (contact.isEmpty()) {
                    etContact.setError("empty number");
                    etContact.requestFocus();
                } else if (deadline.isEmpty()) {
                    etDeadline.setError("empty deadline");
                    etDeadline.requestFocus();
                } else if (!(bloodGroup.isEmpty() && quantity.isEmpty() && location.isEmpty() && contact.isEmpty() && deadline.isEmpty())) {

                    progressDialog = new ProgressDialog(NeedBlood.this);
                    progressDialog.show();
                    progressDialog.setMessage("Sending Mail...");
                    progressDialog.setCanceledOnTouchOutside(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            sendEmail();

                        }
                    },1000);



                } else {
                    Toast.makeText(NeedBlood.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    public void sendEmail() {


        final String username = adminEmail;
        final String password = adminPass;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));

            String to = temp.substring(0,temp.length()-2);
            InternetAddress[] parse = InternetAddress.parse(to , true);
            message.setRecipients(javax.mail.Message.RecipientType.TO,  parse);


            message.setSubject("Someone Needs Blood Nearby You - Team BackBenchers");

            MimeBodyPart messageBodyPart = new MimeBodyPart();

            Multipart multipart = new MimeMultipart();

            messageBodyPart = new MimeBodyPart();

            messageBodyPart.setText("Blood Group : "+ bloodGroup +"\nQuantity : "+ quantity +"\nLocation : "+ location +"\nContact : "+ contact +"\nDeadline : "+ deadline +"\n\n"+ messages + "\n\nIf you cannot give blood yourself, share it with your friends. Maybe one of your shares will save someone's life.\n" +
                    "Thank you.");
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            Transport.send(message);


            progressDialog.dismiss();
            Toast.makeText(NeedBlood.this, "Email was Successfully Sent", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(NeedBlood.this, Dashboard.class);
            startActivity(intent);
            finish();


        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
