package com.example.expiredcalculator.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.expiredcalculator.R
import com.example.expiredcalculator.adapter.TimelineAdapter
import com.example.expiredcalculator.model.Model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_timeline.*


class TimelineFragment : Fragment(R.layout.fragment_timeline) {

    var auth: FirebaseAuth? = null
    var reference: DatabaseReference? = null
    var list: ArrayList<Model>? = null
    var adapter: TimelineAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list = ArrayList<Model>()

        auth = FirebaseAuth.getInstance()
        val user = auth!!.currentUser
        Log.i("TAG1", "current user : -->  $user")
        reference = FirebaseDatabase.getInstance().reference.child(user!!.uid)
        Log.i("TAG1", "reference : -->  $reference")
        reference!!.keepSynced(true)

        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("TAG1", "onDataChange: SNAPSHOT")
                for (dataSnapshot1 in snapshot.children.iterator()) {
                    Log.i("TAG1", "For loop")

                    val model = dataSnapshot1.getValue(Model::class.java)
                    if (model != null) {
                        list!!.add(model)
                    }
                }
                Log.i("TAG1", list.toString())
                adapter = TimelineAdapter(requireContext(),list!!)
                recyclerView.adapter = adapter

            }

        })
    }
}