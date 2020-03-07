package com.nil_projects.society_app


import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Pair as UtilPair
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.EditText
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_user_profiles_list.*
import kotlinx.android.synthetic.main.custom_records_layout.view.*
import kotlinx.android.synthetic.main.custom_user_profile_list.view.*
import okhttp3.internal.waitNanos
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class User_profiles_list : AppCompatActivity() {

    lateinit var edSearch : EditText



    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profiles_list)
        supportActionBar!!.title = "Active Users"
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        edSearch = findViewById<EditText>(R.id.edSearch)

        edSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fetchuserListfromFirebase(s.toString())
            }

        })


        fetchuserListfromFirebase("")
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun fetchuserListfromFirebase(data : String)
    {
        val adapter = GroupAdapter<ViewHolder>()

        var db = FirebaseFirestore.getInstance()
        db.collection("FlatUsers")
                .get()
                .addOnSuccessListener { documentSnapshot ->

                        val city = documentSnapshot.toObjects(UserClass::class.java)
                        for (document in city) {
                            if (data.isEmpty()) {
                                adapter.add(customLayout(document))
                            }else if(data.equals(document.FlatNo)) {
                                adapter.add(customLayout(document))
                            }

                            runAnimation(recyclerview_xml_list_userprof,2)
                            recyclerview_xml_list_userprof.adapter = adapter
                            recyclerview_xml_list_userprof.adapter!!.notifyDataSetChanged()
                            recyclerview_xml_list_userprof.scheduleLayoutAnimation()
                        }
                    }

                .addOnFailureListener { exception ->
                    Log.w("SocietyFirestore", "Error getting documents.", exception)
                }
    }

    private fun runAnimation(recyclerview_xml_list_userprof: RecyclerView?, type : Int) {
        var context = recyclerview_xml_list_userprof!!.context
        lateinit var controller : LayoutAnimationController

        if(type == 2)
            controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_slide_from_right)

        recyclerview_xml_list_userprof.layoutAnimation = controller
    }

    inner class customLayout(var FinaluserList : UserClass) : Item<ViewHolder>()
    {

        override fun getLayout(): Int {
            return R.layout.custom_user_profile_list
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.profile_list_wingName.text = FinaluserList.Wing
            viewHolder.itemView.profile_list_flatNo.text = FinaluserList.FlatNo
            viewHolder.itemView.profile_list_username.text = FinaluserList.UserName
           // Picasso.get().load(FinaluserList.Profile_Pic_url).into(viewHolder.itemView.profile_list_image)

//            Glide.with(this@User_profiles_list).load(FinaluserList.Profile_Pic_url).into(viewHolder.itemView.profile_list_image)


            Glide.with(this@User_profiles_list).load("data:image/jpeg;base64,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").into(viewHolder.itemView.profile_list_image)

            viewHolder.itemView.setOnClickListener {
                var int = Intent(applicationContext,User_Profile :: class.java)
                int.putExtra("UserNameExtra",viewHolder.itemView.profile_list_username.text.toString())
                int.putExtra("ProfileExtra","data:image/jpeg;base64,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")
                int.putExtra("roleExtra",FinaluserList.UserRelation)
                int.putExtra("flatnoExtra",FinaluserList.FlatNo)
                int.putExtra("wingnameExtra",FinaluserList.Wing)
                int.putExtra("numberExtra",FinaluserList.MobileNumber)

                val imageViewPair  = UtilPair.create<View, String>(viewHolder.itemView.profile_list_image, "transitionPicture")
                val textViewPair = UtilPair.create<View, String>(viewHolder.itemView.profile_list_username, "transitionUserName")
                val flatnoPair = UtilPair.create<View, String>(viewHolder.itemView.profile_list_username, "transitionflatno")
                val wingnamePair = UtilPair.create<View, String>(viewHolder.itemView.profile_list_username, "transitionwingname")
                val options = ActivityOptions.makeSceneTransitionAnimation(this@User_profiles_list, imageViewPair, textViewPair, flatnoPair, wingnamePair)
                startActivity(int,options.toBundle())
            }
        }
    }
}
