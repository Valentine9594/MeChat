package com.example.mechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object{
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        getWindow().setBackgroundDrawableResource(R.drawable.mechatbg)

        recycleview_chat_log.adapter = adapter

        //val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username

        //setupDummyData()
        ListenForMessages()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message!!")
            performSendMessage()
        }
    }



    private fun ListenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/User-Messages/$fromId/$toId")

        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if(chatMessage != null){
                    Log.d(TAG, chatMessage.text)

                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        // from me to others
                        //val toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
                        //if(toUser == null) return

                        val currentUser = LatestMessagesActivity.currentUser
                        adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                    }
                    else{
                        // from others to me

                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }


                recycleview_chat_log.scrollToPosition(adapter.itemCount-1)
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })

    }

    private fun performSendMessage(){
        //send message to firebase

        val text = edittext_chat_log.text.toString()
        if (text != ""){
            edittext_chat_log.text.clear()

            val fromId = FirebaseAuth.getInstance().uid
            val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
            val toId = user?.uid
            //edittext_chat_log.text.clear()

            //val reference = FirebaseDatabase.getInstance().getReference("/Messages").push()
            val reference = FirebaseDatabase.getInstance().getReference("/User-Messages/$fromId/$toId").push()
            val toreference = FirebaseDatabase.getInstance().getReference("/User-Messages/$toId/$fromId").push()

            if(fromId == null) return
            val chatMessage = ChatMessage(reference.key!!, text, fromId, toId!!, System.currentTimeMillis()/1000)

            reference.setValue(chatMessage)
                    .addOnSuccessListener {
                        Log.d(TAG, "Saved our chat message: ${reference.key}")

                        recycleview_chat_log.scrollToPosition(adapter.itemCount-1)
                    }

            toreference.setValue(chatMessage)

            val latestMessageRef = FirebaseDatabase.getInstance().getReference("/Latest Messages/$fromId/$toId")
            latestMessageRef.setValue(chatMessage)

            val latestMessagetoRef = FirebaseDatabase.getInstance().getReference("/Latest Messages/$toId/$fromId")
            latestMessagetoRef.setValue(chatMessage)
        }

    }



}


class ChatFromItem(val text: String, val user: User) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text = text

        // load profile images in chats
        val uri = user.profileImageUrl
        val targetView = viewHolder.itemView.image_from_row
        Picasso.get().load(uri).into(targetView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text

        // load profile images in chats
        val uri = user.profileImageUrl
        val targetView = viewHolder.itemView.image_to_row
        Picasso.get().load(uri).into(targetView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}