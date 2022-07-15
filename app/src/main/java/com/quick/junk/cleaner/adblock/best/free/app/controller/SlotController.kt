package com.quick.junk.cleaner.adblock.best.free.app.controller

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.KEY_SELECTED_SLOT_POSITION
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.KEY_SLOT_LIST
import com.quick.junk.cleaner.adblock.best.free.app.data.Config.Companion.PREFS
import com.quick.junk.cleaner.adblock.best.free.app.data.Slot
import com.quick.junk.cleaner.adblock.best.free.app.util.SharedSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class SlotController(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS, MODE_PRIVATE)
    private val editor = prefs.edit()

    fun putSlotInfoLinkedList(slotLinkedList: LinkedList<Slot>) {
        SharedSettings.setString(KEY_SLOT_LIST, Gson().toJson(slotLinkedList))
    }

    fun getSlotInfoLinkedList(): LinkedList<Slot> {
        val gson = Gson()
        val json: String = SharedSettings.getString(KEY_SLOT_LIST)

        return if (json.isNullOrEmpty()||json.length<5)
            LinkedList()
        else {
            val type = object : TypeToken<LinkedList<Slot>>() {}.type
            gson.fromJson(json, type)
        }
    }

    fun putSelectedSlotInfoPosition(position: Int) {
        editor.putInt(KEY_SELECTED_SLOT_POSITION, position)
        editor.apply()
    }

    fun getSelectedSlotInfoPosition(): Int {
        return prefs.getInt(KEY_SELECTED_SLOT_POSITION, 0)
    }

    fun putSelectedSlotInfo(slot: Slot) {
        val slotInfoLinkedList = getSlotInfoLinkedList()
        val selectedSlotPosition = getSelectedSlotInfoPosition()
        slotInfoLinkedList[selectedSlotPosition] = slot
        putSlotInfoLinkedList(slotInfoLinkedList)
    }

    fun getSelectedSlot(): Slot? {
        val slotLinkedList = getSlotInfoLinkedList()
        return if (slotLinkedList.size == 0) null else slotLinkedList[getSelectedSlotInfoPosition()]
    }
}