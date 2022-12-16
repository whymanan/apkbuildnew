package com.vitefinetechapp.vitefinetech.Aeps2.Aeps2New;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paysprint.onboardinglib.activities.HostActivity;
import com.vitefinetechapp.vitefinetech.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HistoryAeps2Fragment extends Fragment {

    private ProgressDialog progressDialog;

    RecyclerView aepsHisRecy;
    private ArrayList<AepsHistory> historyList;
    public static final String SHARED_PREFS = "shared_prefs";
    SharedPreferences sharedPreferences;

    String member_id;
    int x=0,y=10;

    Button btnPre,btnNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_history_aeps2, container, false);

        btnPre=view.findViewById(R.id.btnPre);
        btnNext=view.findViewById(R.id.btnNext);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        member_id = sharedPreferences.getString("member_id", null);

        aepsHisRecy=view.findViewById(R.id.aepsHisRecy);

        fetchHistory();


        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x!=0 && y!=10) {
                    x = x - 10;
                    y = y - 10;
                    Log.d("AepsHisBtnN", x + ":" + y);
                    fetchHistory();
                }
                else{
                    Toast.makeText(getContext(), "No more previous data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x=x+10;
                y=y+10;
                Log.d("AepsHisBtnN", x+":"+y);
                fetchHistory();
            }
        });

        return view;
    }

    private void fetchHistory() {
        historyList=new ArrayList<>();
        String url="http://pe2earns.com/pay2earn/pay/transectionlist?member_id="+member_id+"&api_key=PTEA001&length="+y+"&start="+x;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("AepsHis", response.toString());

                    JSONObject obj = null;
                    try {
                        progressDialog.dismiss();
                        obj = new JSONObject(response);
                        if(obj.getBoolean("status")==true){
                            try{
                                JSONArray data= obj.getJSONArray("data");
                                for(int i=0;i< data.length();i++){
                                    JSONObject data1=data.getJSONObject(i);
                                    Log.d("aepsHisFetch", data1.toString());
//                                String s=dataEach.getString("transection_response");
//                                s=s.replace("\\n","");
//                                s=s.replace("\\","");
//                                JSONObject obj1=new JSONObject(s);

                                    historyList.add(new AepsHistory(
                                            data1.getString("transection_id"),
                                            data1.getString("transection_type"),
                                            data1.getString("transection_msg"),
                                            data1.getString("transection_mobile"),
                                            data1.getString("transection_amount"),
                                            data1.getString("transection_bank_code"),
                                            data1.getString("transection_bank_ifsc"),
                                            data1.getString("created")));
                                }
                                AepsHistoryAdapter adapter=new AepsHistoryAdapter(historyList);
                                RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
                                aepsHisRecy.setLayoutManager(layoutManager);
                                aepsHisRecy.setItemAnimator(new DefaultItemAnimator());
                                aepsHisRecy.setAdapter(adapter);
                            }
                            catch (JSONException e){
                                String dataStr=obj.getString("data");
                                Toast.makeText(getContext(), "End of the History", Toast.LENGTH_SHORT).show();
                                if(x!=0 && y!=10) {
                                x = x - 10;
                                y = y - 10;
                                }
                            }


                        }

                    } catch (JSONException e) {
                        Log.d("AepsHisErr", "error:"+e);
                        e.printStackTrace();
                    }

                },
                error -> {
                    progressDialog.dismiss();
                    Log.d("ElecMakeRechargeError", "error:" + error.getMessage());

                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}