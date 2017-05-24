package edu.nccu.mis.passpair;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sinch.android.rtc.Sinch;

import edu.nccu.mis.passpair.RandomCall.CallSelectActivity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ManuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ManuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;

    private View v;

    public ManuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManuFragment.
     */
//    // TODO: Rename and change types and number of parameters
//    public static ManuFragment newInstance(String param1, String param2) {
//        ManuFragment fragment = new ManuFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_manu, container, false);

        final String MyUID = this.getArguments().getString("ManuUID").toString();


        ImageButton call = (ImageButton)v.findViewById(R.id.callmanuhome);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do something.
                Toast.makeText(view.getContext(), "這是一個Toast......", Toast.LENGTH_LONG).show();
                Intent intentCall = new Intent();

                //以bundle物件進行打包
                Bundle bundle=new Bundle();
                bundle.putString("UID", MyUID);
                intentCall.putExtras(bundle);

                intentCall.setClass(getActivity(),CallSelectActivity.class);
                startActivity(intentCall);

            }
        });

        ImageButton match = (ImageButton)v.findViewById(R.id.matchmanuhome);
        match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do something.
               // Toast.makeText(view.getContext(), "這是一個Toast......", Toast.LENGTH_LONG).show();


                Intent intentMatch = new Intent();

                //以bundle物件進行打包
                Bundle bundle=new Bundle();
                bundle.putString("UID", MyUID);
                intentMatch.putExtras(bundle);

                intentMatch.setClass(getActivity(),Match.class);
                startActivity(intentMatch);
            }
        });

        ImageButton pass = (ImageButton)v.findViewById(R.id.passmanuhome);
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do something.
                //Toast.makeText(view.getContext(), "這是一個Toast......", Toast.LENGTH_LONG).show();

                Intent intentPass = new Intent();

                //以bundle物件進行打包
                Bundle bundle=new Bundle();
                bundle.putString("UID", MyUID);
                intentPass.putExtras(bundle);

                intentPass.setClass(getActivity(),MapsActivity.class);
                startActivity(intentPass);

            }
        });

        ImageButton setting = (ImageButton)v.findViewById(R.id.settingmanuhome);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do something.
                Toast.makeText(view.getContext(), "這是一個Toast......", Toast.LENGTH_LONG).show();

            }
        });

        ImageButton fanP = (ImageButton)v.findViewById(R.id.fanpage);
        fanP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do something.
                //Toast.makeText(view.getContext(), "這是一個Toast......", Toast.LENGTH_LONG).show();

                Intent intentFP = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/Pass-Pair-178834782622464/"));
                startActivity(intentFP);

            }
        });

        ImageButton taskM = (ImageButton)v.findViewById(R.id.task);
        taskM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do something.
                Toast.makeText(view.getContext(), "這是一個Toast......", Toast.LENGTH_LONG).show();

            }
        });


        return v;

    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
