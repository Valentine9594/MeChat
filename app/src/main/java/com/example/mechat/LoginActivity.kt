package com.example.mechat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //getWindow().setBackgroundDrawableResource(R.drawable.mechatbg)

        login_button_login.setOnClickListener {
            val emailLogin = email_edittext_login.text.toString()
            val passwordLogin = password_edittext_login.text.toString()

            if(emailLogin.isEmpty() || passwordLogin.isEmpty()){
                Toast.makeText(this, "Invalid User or Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("LoginActivity", "Email is : " + emailLogin)
            Log.d("LoginActivity", "Password is : $passwordLogin")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailLogin, passwordLogin)
                .addOnCompleteListener {
                    if(!it.isSuccessful) return@addOnCompleteListener

                    Log.d("LoginActivity","User Logged In")



                    //Log User into account
                    Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show()
                    val intent = Intent( this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d("LoginActivity","Failed to Log In")
                    Toast.makeText(this, "Failed to Log In", Toast.LENGTH_SHORT).show()
                }
        }

        back_to_register.setOnClickListener {
            finish()
        }

    }

}