package com.osi.uconectdriver;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.osi.uconectdriver.AsyncTask.CallAPI;
import com.osi.uconectdriver.Dataset.HistoryData;
import com.osi.uconectdriver.RecyclerviewAdapter.HistoryAdapter;
import com.osi.uconectdriver.Util.Util;
import com.osi.uconectdriver.dialogs.ProgressDialogView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shadab.s on 14-01-2016.
 */
public class HistoryActivity extends ParentActivity implements View.OnClickListener {

    TextView headername;
    ImageView ic_back;
    HistoryAdapter historyAdapter;
    ArrayList<HistoryData> Historylist = new ArrayList<>();
    RecyclerView mrecycler_score;
    private TextView noHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        progressdialog = new ProgressDialogView(HistoryActivity.this, "");
        BindView(null, savedInstanceState);
    }

    @Override
    public void BindView(View view, Bundle savedInstanceState) {
        super.BindView(view, savedInstanceState);
        headername = (TextView) findViewById(R.id.headername);
        ic_back = (ImageView) findViewById(R.id.ic_back);
        noHistory = (TextView) findViewById(R.id.nohistory);

        mrecycler_score = (RecyclerView) findViewById(R.id.recyclerview_list);
        mrecycler_score.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(
                HistoryActivity.this);
        mrecycler_score.setLayoutManager(mLayoutManager);

        headername.setText("HISTORY");
        SetOnclicklistener();
    }

    @Override
    public void SetOnclicklistener() {
        super.SetOnclicklistener();
        ic_back.setOnClickListener(this);
//        for (int i = 0; i < 10; i++) {
//            HistoryData historyData = new HistoryData();
//            historyData.bookingId = ("bookingId");
//            historyData.customerId = ("customerId");
//            historyData.sourceLatitude = ("sourceLatitude");
//            historyData.sourceLongitude = ("sourceLongitude");
//            historyData.destLatitude = ("destLatitude");
//            historyData.paymentId = ("paymentId");
//            historyData.vehicleType = ("vehicleType");
//            historyData.status = ("status");
//            historyData.createDate = ("createDate");
//            historyData.modifiedDate = ("modifiedDate");
//            historyData.corpCustId = ("corpCustId");
//            historyData.srcPlace = ("srcPlace");
//            historyData.destPlace = ("destPlace");
//            historyData.finalFare = ("finalFare");
//            historyData.driverName = ("driverName");
//            historyData.driverPhoto = ("driverPhoto");
//            historyData.accountType = ("accountType");
//
//            Historylist.add(historyData);
//        }
//
//
//        historyAdapter = new HistoryAdapter(Historylist, HistoryActivity.this);
//        mrecycler_score.setAdapter(historyAdapter);
        try {
            JSONObject jsonObject_main = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            jsonObject_main = getCommontHeaderParams();
            jsonObject.put("driverId", /*Session.getUserID(HistoryActivity.this)*/"1");
            jsonObject_main.put("body", jsonObject);
            CallAPI(jsonObject_main);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ic_back:
                finish();
                break;
        }

    }

    public void CallAPI(JSONObject params) {
        if (Util.isNetworkConnected(HistoryActivity.this)) {
            try {
                if (progressdialog.isShowing())
                    progressdialog.dismiss();
                progressdialog.show();
                new CallAPI(GETHISTORY, "GETHISTORY", params, HistoryActivity.this, GetDetails_Handler, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            progressdialog.dismissanimation(ProgressDialogView.ERROR);
            Util.ShowToast(HistoryActivity.this, getString(R.string.nointernetmessage));
        }
    }

    Handler GetDetails_Handler = new Handler() {
        public void handleMessage(Message msg) {

            PrintMessage("Handler " + msg.getData().toString());
            if (msg.getData().getBoolean("flag")) {
                if (msg.getData().getInt("code") == SUCCESS) {
                    progressdialog.dismissanimation(ProgressDialogView.ERROR);

                    try {
                        JSONArray jsonArray = new JSONArray(msg.getData().getString("responce"));
                        noHistory.setVisibility(View.GONE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            HistoryData historyData = new HistoryData();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            historyData.bookingId = jsonObject.getString("bookingId");
                            historyData.driverId = jsonObject.getString("customerId");
                            historyData.sourceLatitude = jsonObject.getString("sourceLatitude");
                            historyData.sourceLongitude = jsonObject.getString("sourceLongitude");
                            historyData.destLatitude = jsonObject.getString("destLatitude");
                            historyData.paymentId = jsonObject.getString("paymentId");
                            historyData.vehicleType = jsonObject.getString("vehicleType");
                            historyData.status = jsonObject.getString("status");
                            historyData.createDate = jsonObject.getString("createDate");
                            historyData.modifiedDate = jsonObject.getString("modifiedDate");
                            historyData.srcPlace = jsonObject.getString("srcPlace");
                            historyData.destPlace = jsonObject.getString("destPlace");
                            historyData.finalFare = jsonObject.getString("rideTotalAmt");
                            //historyData.driverName = jsonObject.getString("driverName");
                            //historyData.driverPhoto = jsonObject.getString("driverPhoto");
                            historyData.accountType = jsonObject.getString("accountType");
                            historyData.car = jsonObject.getString("carNumber")+" "+jsonObject.getString("make")+"-"+jsonObject.getString("model");

                            Historylist.add(historyData);
                        }


                        historyAdapter = new HistoryAdapter(Historylist, HistoryActivity.this);
                        mrecycler_score.setAdapter(historyAdapter);
                        if (Historylist.size() == 0) {
                            noHistory.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        noHistory.setVisibility(View.VISIBLE);
                        noHistory.setText(msg.getData().getString("responce"));

                    }
                } else if (msg.getData().getInt("code") == FROMGENERATETOKEN) {
                    ParseSessionDetails(msg.getData().getString("responce"));
                    try {
                        CallAPI(new JSONObject(msg.getData()
                                .getString("mExtraParam")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.getData().getInt("code") == SESSIONEXPIRE) {
                    if (Util.isNetworkConnected(HistoryActivity.this)) {
                        CallSessionID(GetDetails_Handler, msg.getData()
                                .getString("mExtraParam"));
                    } else {
                        progressdialog.dismissanimation(ProgressDialogView.ERROR);
                        Util.ShowToast(HistoryActivity.this, getString(R.string.nointernetmessage));
                    }
                } else {
                    progressdialog.dismissanimation(ProgressDialogView.ERROR);
                    Util.ShowToast(HistoryActivity.this, msg.getData().getString("msg"));
                }
            } else {
                progressdialog.dismissanimation(ProgressDialogView.ERROR);
                Util.ShowToast(HistoryActivity.this, msg.getData().getString("msg"));
            }
        }
    };
}