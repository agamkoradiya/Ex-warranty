package com.example.expiredcalculator.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.expiredcalculator.R
import com.example.expiredcalculator.model.Model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.timeline_items.view.*
import java.text.DateFormatSymbols
import java.util.*

class TimelineAdapter(val context: Context, val arrayList: ArrayList<Model>) : RecyclerView.Adapter<TimelineAdapter.TimelineItemViewHolder>() {
    var cal1: Calendar = Calendar.getInstance()
    var cal2: Calendar = Calendar.getInstance()

    private var databaseReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    inner class TimelineItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineItemViewHolder {
        return TimelineItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.timeline_items,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: TimelineItemViewHolder, position: Int) {


        // Set the date for both of the calendar instance

        // Set the date for both of the calendar instance
        cal1[arrayList[position].purchasedYear , arrayList[position].purchasedMonth] = arrayList[position].purchasedDate
        cal2[arrayList[position].expYear, arrayList[position].expMonth] = arrayList[position].expDate

        // Get the represented date in milliseconds

        // Get the represented date in milliseconds
        val millis1 = cal1.timeInMillis
        val millis2 = cal2.timeInMillis

        // Calculate difference in milliseconds

        // Calculate difference in milliseconds
        val diff = millis2 - millis1
        val diffDays = diff / (24 * 60 * 60 * 1000)


        Glide.with(context).load(arrayList[position].productIconUrl).centerCrop().into(holder.itemView.resultItemIcon)
        holder.itemView.resultProductName.text = arrayList[position].productName
        holder.itemView.resultLeftDays.text = "${diffDays - 1} days left"
        holder.itemView.resultPurchaseDate.text = "${arrayList[position].purchasedDate} ${getMonth(arrayList[position].purchasedMonth)} ${arrayList[position].purchasedYear}"
        holder.itemView.resultExpireDate.text = "${arrayList[position].expDate} ${getMonth(arrayList[position].expMonth)} ${arrayList[position].expYear}"

        holder.itemView.resultDelete.setOnClickListener {
            if (arrayList[position].productIconUrl != "null"){
                val photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(arrayList[position].productIconUrl)
                photoRef.delete()
            }
            Toast.makeText(context,"Deleted",Toast.LENGTH_SHORT).show()
            databaseReference =
                FirebaseDatabase.getInstance().reference.child(mAuth.currentUser?.uid.toString())
                    .child("Item${arrayList[position].itemId}")
            databaseReference!!.setValue(null)
            arrayList.clear()
        }
    }

    private fun getMonth(month: Int): String? {
        return DateFormatSymbols().months[month]
    }

}

