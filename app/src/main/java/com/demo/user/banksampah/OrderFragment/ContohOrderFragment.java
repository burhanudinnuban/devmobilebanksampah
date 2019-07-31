package com.demo.user.banksampah.OrderFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demo.user.banksampah.R;


public class ContohOrderFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView =inflater.inflate(R.layout.fragment_contoh_order, container, false);

        /*Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();

        String nama_lain = extras.getString("namaLain_Sampah");
        String deskripsi = extras.getString("deskripsi_Sampah");
        String harga = extras.getString("harga_Sampah");

        Toast.makeText(getContext(),nama_lain ,Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(),deskripsi ,Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(),harga ,Toast.LENGTH_SHORT).show();
        Log.e("log", nama_lain + " " + deskripsi + " " + harga );

        EditText etNamaLain =  (EditText) rootView.findViewById(R.id.etNamaLain);
        EditText etDeskripsi = (EditText) rootView.findViewById(R.id.etDeskripsi);
        EditText etHarga = (EditText) rootView.findViewById(R.id.etHarga);

        etNamaLain.setText(nama_lain);*/
//        etDeskripsi.setText(deskripsi);
//        etHarga.setText(harga);

         return rootView;
    }


}
