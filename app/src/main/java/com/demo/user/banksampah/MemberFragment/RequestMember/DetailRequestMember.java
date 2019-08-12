package com.demo.user.banksampah.MemberFragment.RequestMember;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.user.banksampah.R;
import com.squareup.picasso.Picasso;

public class DetailRequestMember extends AppCompatActivity {

    private Button btTerimaMember, btTolakMember;
    private ImageView imgPictureReq;
    private TextView tvNamaReqMember, tvIdReqMember, tvAlamatReqMember, tvTanggalCreateReqMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_request_member);
        btTerimaMember = findViewById(R.id.btTambahmember);
        btTolakMember = findViewById(R.id.btTolakMember);
        imgPictureReq = findViewById(R.id.imgPictureDetailReqMember);
        tvNamaReqMember = findViewById(R.id.tvNamaDetailReqMember);
        tvAlamatReqMember = findViewById(R.id.tvAlamatDetailReqMember);
        tvIdReqMember = findViewById(R.id.tvIdDetailReqMember);
        tvTanggalCreateReqMember = findViewById(R.id.tvTanggalBuatDetailReqMember);

        String strDetailPhotoReqmember = getIntent().getStringExtra("foto");
        String strNamaDetailReqMember = getIntent().getStringExtra("nama_member");
        String strAlamatDetailReqMember = getIntent().getStringExtra("alamat");
        String strIdReqMember = getIntent().getStringExtra("id_member");
        String strTanggalCreateReqMember = getIntent().getStringExtra("creation");
        String strIdBankSampah = getIntent().getStringExtra("id");

        tvNamaReqMember.setText("" + strNamaDetailReqMember);
        tvAlamatReqMember.setText("" + strAlamatDetailReqMember);
        tvIdReqMember.setText("" + strIdReqMember);
        tvTanggalCreateReqMember.setText("" + strTanggalCreateReqMember);

        Picasso.get().load((strDetailPhotoReqmember)).into(imgPictureReq);

        final ImageView image = new ImageView(this);
        Picasso.get().load((strDetailPhotoReqmember)).into(image);
    }
}
