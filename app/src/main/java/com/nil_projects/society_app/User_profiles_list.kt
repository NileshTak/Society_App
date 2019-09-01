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
        actionbar!!.setDisplayHomeAsUpEnabled(true)

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
            Glide.with(this@User_profiles_list).load(FinaluserList.Profile_Pic_url).into(viewHolder.itemView.profile_list_image)


            viewHolder.itemView.setOnClickListener {
                var int = Intent(applicationContext,User_Profile :: class.java)
                int.putExtra("UserNameExtra",viewHolder.itemView.profile_list_username.text.toString())
                int.putExtra("ProfileExtra",FinaluserList.Profile_Pic_url)
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
