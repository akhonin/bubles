package com.galaxy.bubbles.ui.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentProviderOperation
import android.content.Context
import android.content.Intent
import android.content.OperationApplicationException
import android.os.Build.ID
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.RemoteException
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.RawContacts
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.galaxy.bubbles.R
import com.galaxy.bubbles.data.ContactItem


class ContactsActivity: AppCompatActivity()  {
    var contacts:ArrayList<ContactItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)
        val contactsList = findViewById<RecyclerView>(R.id.idGRV)

        contacts = intent.getSerializableExtra("contacts") as ArrayList<ContactItem>


        val listAdapter = ContactGroupRecyclerAdapter()
        contactsList.adapter = listAdapter
        contactsList.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)

        listAdapter.onItemClick ={
            deleteContact(contacts[it].Id!!)
            if(contacts.size>1) {
                contacts.removeAt(it)
                listAdapter.notifyItemRemoved(it)
            }else{
                prepareCloseData()
            }
        }
    }

    override fun onBackPressed() {
       prepareCloseData()
    }

    private fun prepareCloseData() {
        val intent = Intent()
        intent.putExtra("contacts", contacts)
        setResult(Activity.RESULT_OK, intent)
        Handler(Looper.getMainLooper()).postDelayed({ finish() }, 100L)
    }

    inner class ContactGroupRecyclerAdapter : RecyclerView.Adapter<ContactGroupRecyclerAdapter.ListViewHolder>() {
        inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val name: TextView = itemView.findViewById(R.id.name)
            val phone: TextView = itemView.findViewById(R.id.phone)
            val close: ImageView = itemView.findViewById(R.id.close)
        }

        var onItemClick: ((Int) -> Unit)? = null

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): ContactGroupRecyclerAdapter.ListViewHolder {
            return ListViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_contact, parent, false))
        }

        override fun onBindViewHolder(
            holder: ContactGroupRecyclerAdapter.ListViewHolder,
            position: Int,
        ) {
            holder.close.setOnClickListener {
                onItemClick?.invoke(position)
            }
            holder.name.text = contacts[position].Name
            holder.phone.text = contacts[position].Number
        }

        override fun getItemCount() = contacts.size
    }

    private fun deleteContact(rawContactId:Long) {
        // First select raw contact id by given name and family name.
        val contentResolver = contentResolver

        //******************************* delete data table related data ****************************************
        // Data table content process uri.
        val dataContentUri = ContactsContract.Data.CONTENT_URI

        // Create data table where clause.
        val dataWhereClauseBuf = StringBuffer()
        dataWhereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID)
        dataWhereClauseBuf.append(" = ")
        dataWhereClauseBuf.append(rawContactId)

        // Delete all this contact related data in data table.
        contentResolver.delete(dataContentUri, dataWhereClauseBuf.toString(), null)


        //******************************** delete raw_contacts table related data ***************************************
        // raw_contacts table content process uri.
        val rawContactUri = ContactsContract.RawContacts.CONTENT_URI

        // Create raw_contacts table where clause.
        val rawContactWhereClause = StringBuffer()
        rawContactWhereClause.append(ContactsContract.RawContacts._ID)
        rawContactWhereClause.append(" = ")
        rawContactWhereClause.append(rawContactId)

        // Delete raw_contacts table related data.
        contentResolver.delete(rawContactUri, rawContactWhereClause.toString(), null)

        //******************************** delete contacts table related data ***************************************
        // contacts table content process uri.
        val contactUri = ContactsContract.Contacts.CONTENT_URI

        // Create contacts table where clause.
        val contactWhereClause = StringBuffer()
        contactWhereClause.append(ContactsContract.Contacts._ID)
        contactWhereClause.append(" = ")
        contactWhereClause.append(rawContactId)

        // Delete raw_contacts table related data.
        contentResolver.delete(contactUri, contactWhereClause.toString(), null)
    }
}