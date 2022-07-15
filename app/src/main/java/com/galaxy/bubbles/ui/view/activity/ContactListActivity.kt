package com.galaxy.bubbles.ui.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.galaxy.bubbles.R
import com.galaxy.bubbles.data.ContactItem
import com.galaxy.bubbles.data.FolderItem

class ContactListActivity: AppCompatActivity()  {
    var groups:Map<String,List<ContactItem>> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)
        val contactsList = findViewById<RecyclerView>(R.id.idGRV)

        var contacts = intent.getSerializableExtra("contacts") as ArrayList<ContactItem>

        groups = contacts.groupBy { it.Number!! }


        val listAdapter = ContactGroupRecyclerAdapter()
        contactsList.adapter = listAdapter
        contactsList.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)

        val serverListResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                contacts =
                    result.data?.getSerializableExtra("contacts") as ArrayList<ContactItem>
                groups = contacts.groupBy { it.Number!! }
                listAdapter.notifyDataSetChanged()
            }
        }

        listAdapter.onItemClick = {
            val intent = Intent(this, ContactsActivity::class.java)
            intent.putParcelableArrayListExtra("contacts", it)
            serverListResult.launch(intent)
        }
    }


    inner class ContactGroupRecyclerAdapter : RecyclerView.Adapter<ContactGroupRecyclerAdapter.ListViewHolder>() {
        inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val count: TextView = itemView.findViewById(R.id.count)
            val phone: TextView = itemView.findViewById(R.id.phone)
        }

        var onItemClick: ((ArrayList<ContactItem>) -> Unit)? = null

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): ContactGroupRecyclerAdapter.ListViewHolder {
            return ListViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_contact_group, parent, false))
        }

        override fun onBindViewHolder(
            holder: ContactGroupRecyclerAdapter.ListViewHolder,
            position: Int
        ) {
            holder.itemView.setOnClickListener {
                val contats = ArrayList<ContactItem>()
                contats.addAll(groups.values.toList()[position])
                onItemClick?.invoke(contats)
            }
            holder.count.text = "${groups.values.toList()[position].size} identical contacts"
            holder.phone.text = groups.values.toList()[position][0].Number
        }

        override fun getItemCount() = groups.values.size
    }

}