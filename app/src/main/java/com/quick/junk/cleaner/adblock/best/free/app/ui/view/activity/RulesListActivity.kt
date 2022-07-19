package com.quick.junk.cleaner.adblock.best.free.app.ui.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quick.junk.cleaner.adblock.best.free.app.R
import com.quick.junk.cleaner.adblock.best.free.app.util.SharedSettings

class RulesListActivity: AppCompatActivity() {

    companion object {
        private const val SELECTED_RULES = "SELECTED_RULES"
    }

    var items = arrayListOf<Rules>(
        Rules("Block All",125,"All rules will be activated",false),
        Rules("Remove Ads",125,"Blocking ads in any form",false),
        Rules("Block Trackers",125,"Tracking software blocking",false),
        Rules("Block Adult",125,"Blocking ads of a sexual nature",false)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruls)

        findViewById<View>(R.id.icon).setOnClickListener {
            onBackPressed()
        }

        var rules_rw = findViewById<RecyclerView>(R.id.rules_list)

        var selected = SharedSettings.getString(SELECTED_RULES)

        if(selected.length<5){
            items[0].Selected = true
        }else{
            items.forEach {
                if(it.Name==selected){
                    it.Selected = true
                }
            }
        }

        val listAdapter = RulesGroupRecyclerAdapter()
        rules_rw.adapter = listAdapter
        rules_rw.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)

        listAdapter.onItemClick = {
            SharedSettings.setString(SELECTED_RULES, it)
            onBackPressed()
        }
    }


    inner class RulesGroupRecyclerAdapter : RecyclerView.Adapter<RulesGroupRecyclerAdapter.ListViewHolder>() {
        inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val name: TextView = itemView.findViewById(R.id.name)
            val count: TextView = itemView.findViewById(R.id.rules_count)
            val descr: TextView = itemView.findViewById(R.id.rules_descr)
            val selected: RadioButton = itemView.findViewById(R.id.selected)
        }

        var onItemClick: ((String) -> Unit)? = null

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): RulesGroupRecyclerAdapter.ListViewHolder {
            return ListViewHolder(
                LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rules, parent, false))
        }

        override fun onBindViewHolder(
            holder: RulesGroupRecyclerAdapter.ListViewHolder,
            position: Int,
        ) {
            holder.name.text = items[position].Name
            holder.descr.text = items[position].Desc
            holder.count.text = "${items[position].Count} Rules"
            holder.selected.isChecked = items[position].Selected
            holder.selected.setOnClickListener {
                onItemClick?.invoke(items[position].Name)
            }
        }

        override fun getItemCount() = items.size
    }

    data class Rules(
        var Name:String,
        var Count:Int,
        var Desc:String,
        var Selected:Boolean
    )
}