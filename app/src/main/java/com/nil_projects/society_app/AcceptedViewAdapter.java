package com.nil_projects.society_app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nil_projects.society_app.fragment.AcceptedFragment;
import com.nil_projects.society_app.fragment.Model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AcceptedViewAdapter extends FirestoreRecyclerAdapter<Model, AcceptedViewAdapter.ViewHolder> {

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("FlatUsers");


    public AcceptedViewAdapter(FirestoreRecyclerOptions<Model> options) {
        super(options);
    }


    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_HEADER;
            default:
                return TYPE_CELL;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_userreq, parent, false);
        return new ViewHolder(view) {
        };
    }

    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, int i, final Model model) {
        viewHolder.wingName.setText(model.getWing());
        viewHolder.flatNo.setText(model.getFlatNo());
        viewHolder.relation.setText(model.getUserRelation());
        viewHolder.userName.setText(model.getUserName());
        viewHolder.contactNo.setText(model.getMobileNumber());
        viewHolder.email.setText(model.getUserEmail());

        viewHolder.imgTick.setAnimation("tick.json");
        viewHolder.imgTick.playAnimation();
        viewHolder.imgTick.loop(true);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView wingName;
        public TextView flatNo;
        public TextView relation;
        public TextView userName;
        public TextView contactNo;
        public TextView email;
        public Button reqAcceptBtn;
        public LottieAnimationView imgTick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            wingName = (TextView) itemView.findViewById(R.id.cutom_userreq_wingname);
            flatNo = (TextView) itemView.findViewById(R.id.cutom_userreq_flatno);
            relation = (TextView) itemView.findViewById(R.id.cutom_userreq_relation);
            userName = (TextView) itemView.findViewById(R.id.cutom_userreq_name);
            contactNo = (TextView) itemView.findViewById(R.id.cutom_userreq_number);
            email = (TextView) itemView.findViewById(R.id.cutom_userreq_email);
            reqAcceptBtn = (Button) itemView.findViewById(R.id.custom_userreq_acceptbtn);

            imgTick = (LottieAnimationView) itemView.findViewById(R.id.imgTick);


        }
    }
}