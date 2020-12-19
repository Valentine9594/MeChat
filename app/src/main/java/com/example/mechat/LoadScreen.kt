package com.example.mechat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LoadScreen: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.loading_screen)

        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.

            val uid = FirebaseAuth.getInstance().uid

                if (uid == null) {
                    //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    //pre load for Latest Messages


                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            /*val intent = Intent(this, LatestMessagesActivity::class.java)
            startActivity(intent)
            finish()
            */

    }


}



