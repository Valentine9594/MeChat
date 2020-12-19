package com.example.mechat

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<GroupieViewHolder>(){

    var chatPartnerUser: User? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_textView_latest_message.text = chatMessage.text
        Log.d("ROWMESSAGE", "HERE WE ARE")

        val chatPartnerId: String
        if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatPartnerId = chatMessage.toId
        }
        else{
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/Users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                viewHolder.itemView.username_textView_latest_message.text = chatPartnerUser?.username

                val targetView = viewHolder.itemView.imageView_latest_message
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetView)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}