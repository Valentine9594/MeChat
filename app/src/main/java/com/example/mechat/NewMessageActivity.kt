package com.example.mechat
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*


class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

     //   val adapter = GroupAdapter<GroupieViewHolder>()
     //   adapter.add(UserItem())

     //   recycleview_newmessage.adapter = adapter
        fetchUsers()
    }


    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance().getReference("/Users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()


                snapshot.children.forEach {
                    Log.d("NewMessage", it.toString())

                    val user = it.getValue(User::class.java)
                    if( user != null){

                        if(user.uid != FirebaseAuth.getInstance().uid){
                            //Log.d("NewMessage", "This is User: ${user.uid}")
                            adapter.add(UserItem(user))
                        }

                    }
                }
                
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                //    intent.putExtra(USER_KEY ,userItem.user.username)
                    intent.putExtra(USER_KEY ,userItem.user)
                    startActivity(intent)

                    finish()
                }
                
                recycleview_newmessage.adapter = adapter
                //recycle_review_lastest_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}

class UserItem(val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_textview_messages.text = user.username

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_new_message)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}

//class customAdapter: RecyclerView.Adapter<viewHolder> {
//    override fun onBindViewHolder(holder: viewHolder, position: Int) {
//        TODO("Not yet implemented")
//    }
//}