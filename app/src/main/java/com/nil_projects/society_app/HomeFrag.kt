package com.nil_projects.society_app

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFrag : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        fetch()

        return view
    }

    private fun fetch() {
        var ref = FirebaseDatabase.getInstance().getReference("/Status")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("SocietyLogs","Data Change")
                p0.children.forEach {
                    Log.d("SocietyLogs","p0")

                    val status = it.getValue(statusclass :: class.java)
                    Log.d("SocietyLogs","class")

                    Log.d("SocietyLogs","value")
                }
            }
        })
    }
}

class statusclass(var id : String,var status : String)
{
    constructor() : this("","")
}