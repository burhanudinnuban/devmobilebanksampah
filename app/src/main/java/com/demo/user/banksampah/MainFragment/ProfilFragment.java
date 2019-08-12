//package com.demo.user.banksampah.MainFragment;
//
//
//import android.annotation.SuppressLint;
//import android.app.Dialog;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.demo.user.erecycle.Activities.MainActivity;
//import com.demo.user.erecycle.Adapter.PrefManager;
//import com.demo.user.erecycle.R;
//import com.squareup.picasso.Picasso;
//
//public class ProfilFragment extends Fragment {
//
//    //Session Class
//    PrefManager session;
//
//    private TextView tvNama, tvPoints, tvNoHp, tvLogOut;
//    private ImageView imgProfile;
//
//    private String url_foto;
//
//    //PopUp Image Dialog
//    Dialog myDialog;
//
//    public static ProfilFragment newInstance() {
//        ProfilFragment fragment = new ProfilFragment();
//        return fragment;
//    }
//
//    @SuppressLint("WrongViewCast")
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        final View rootView = inflater.inflate(R.layout.fragment_profil, container, false);
//
//        url_foto = "http://dev-erpnext.pracicointiutama.id";
//
//        //Session Instance
//        session = new PrefManager(getContext());
//        //session.checkLogin();
//
//        tvLogOut = rootView.findViewById(R.id.tvLogOut);
//        tvNama = rootView.findViewById(R.id.tvNama_Profil);
//        tvNoHp = rootView.findViewById(R.id.tvNoHp_Profil);
//        tvPoints = rootView.findViewById(R.id.tvPoints_Profil);
//
//        imgProfile = rootView.findViewById(R.id.imgPicture_Profil);
//
//        Picasso.get()
//                .load(url_foto + MainActivity.strFoto)
//                .error(R.drawable.ic_no_data)
//                .resize(200,200)
//                .into(imgProfile);
//
//        //Get Data From SharedPref
//        tvNama.setText(MainActivity.strNama);
//        //Cek Number
//        //tvNoHp.setText(modifNumber(MainActivity.strNoHP));
//        //tvNoHp.setText(MainActivity.strNoHP);
//        tvPoints.setText(MainActivity.strPoints + " Points");
//
//        //Create myDialog
//        myDialog = new Dialog(getActivity());
//
//        imgProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ProfilePopUp(v);
//            }
//        });
//
//        tvLogOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                session.logoutUser();
//            }
//        });
//
//        return rootView;
//    }
//
//    public void ProfilePopUp(View v){
//        //Initiate Variable Image
//        ImageView imgProfilCache;
//
//        myDialog.setContentView(R.layout.pop_up_image);
//
//        imgProfilCache = (ImageView)myDialog.findViewById(R.id.imgPicture_Cache);
//
//        Picasso.get()
//                .load(url_foto + MainActivity.strFoto)
//                .error(R.drawable.ic_no_data)
//                .resize(800,800)
//                .centerCrop()
//                .into(imgProfilCache);
//
//        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        myDialog.show();
//    }
//
//    //If Phone Number Start with 08...
//    //Change to +628...
//    private String modifNumber (String num){
//        if (num.startsWith("08")){
//            num = num.replaceFirst("08", "+628");
//        } else if (num.startsWith("62")){
//            num = num.replaceFirst("62", "+62");
//        }
//        return num;
//    }
//}
