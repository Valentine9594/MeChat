package com.example.mechat

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //getWindow().setBackgroundDrawableResource(R.drawable.mechatbg)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_textView.setOnClickListener {
            // Log.d("MainActivity", "Try to show Login activity!")

            // launch login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        selectphoto_button_register.setOnClickListener {
            Log.d("RegisterActivity", "Photo is Here!!")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)

        }

    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            add_photo_textView.text = ""
            selectphoto_imageview_register.setImageBitmap(bitmap)
            selectphoto_button_register.alpha = 0f

            //val bitmapDrawable = BitmapDrawable(bitmap)
            //selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }



    private fun performRegister(){
        val username = username_edittext_register.text.toString()
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()
        val confirm = confirm_password_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()){
            Toast.makeText(this, "Please Fill out all Details", Toast.LENGTH_SHORT).show()
            return
        }
        if (confirm != password){
            Toast.makeText(this, "Confirm Password does not match Password",Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity", "Email is : " + email)
        Log.d("RegisterActivity", "Password is : $password")

        //Firebase Authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //if success
                Log.d("RegisterActivity", "User Successfully Registered : ${it.result?.user?.uid}")
                Toast.makeText(this, "Successfully Registered", Toast.LENGTH_SHORT).show()
                UploadImageToFireBaseStorage()
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to Register User : ${it.message}")
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
            }
    }

    private fun UploadImageToFireBaseStorage(){
        if(selectedPhotoUri == null){
            selectedPhotoUri = Uri.parse("android.resource://com.example.mechat/drawable/default_person");
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)

            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully Uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "File Location : $it")

                    saveUserToFireBaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                //do some logging
            }
    }

    private fun saveUserToFireBaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")

        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Finally we saved User to Firebase to Database")

                //Log User into account
                Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to Register User : ${it.message}")
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
            }
    }

}
