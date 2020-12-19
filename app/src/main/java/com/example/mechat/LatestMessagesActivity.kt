package com.example.mechat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.mechat.NewMessageActivity.Companion.USER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object{
        var currentUser: User? = null
        val TAG = "LatestMessages"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        Log.d("ROWMESSAGE", "HERE is Before Activity")
        recycle_review_lastest_messages.adapter = adapter
        recycle_review_lastest_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        Log.d("ROWMESSAGE", "HERE is after Activity")

        //set item click listener on adapter
        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG,"123")
            val intent = Intent(this, ChatLogActivity::class.java)

            val row = item as LatestMessageRow

            intent.putExtra(USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }


        //setDummyRows()
        listenForLatestMessages()
        fetchCurrentUser()

        verifyUserIsLoggedIn()
    }




    val latestMessageMap = HashMap<String, ChatMessage>()

    private fun refreshRecycleReviewMessages(){
        adapter.clear()
        latestMessageMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/Latest Messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return

                latestMessageMap[snapshot.key!!] = chatMessage
                refreshRecycleReviewMessages()

                //adapter.add(LatestMessageRow(chatMessage))
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue((ChatMessage::class.java)) ?: return
                latestMessageMap[snapshot.key!!] = chatMessage
                refreshRecycleReviewMessages()
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
    }




    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d("LatestMessages", "Current User : ${currentUser?.username}")
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}