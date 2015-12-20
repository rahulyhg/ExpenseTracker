package com.expensetracker.expensetracker;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

/**
 * Created by Krush on 18-Dec-15.
 */
public class ReportFragmentTab1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "FragmentReportTab1";
    private Spinner CategorySpinner;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private BarChart mChart;

    public ReportFragmentTab1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFragmentTab1 newInstance(String param1, String param2) {
        ReportFragmentTab1 fragment = new ReportFragmentTab1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "OnStart");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "OnAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.e(TAG, "OnCreate");
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_report_expense, null);

        AdView adView = (AdView) root.findViewById(R.id.adView);
        if (NetworkState.getNetworkState(getActivity())) {
            adView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder()
                    .setRequestAgent("android_studio:ad_template").build();
            adView.loadAd(adRequest);
        } else
            adView.setVisibility(View.GONE);

        CategorySpinner = (Spinner) root.findViewById(R.id.SPNcategory);
        mChart = (BarChart) root.findViewById(R.id.chart1);
        mChart.setPinchZoom(true);
        mChart.setDragEnabled(false);

        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        yVals1.add(new BarEntry(3500, 0));
        yVals1.add(new BarEntry(6500, 1));
        yVals1.add(new BarEntry(3755, 2));
        yVals1.add(new BarEntry(7005, 3));
        yVals1.add(new BarEntry(3005, 4));
        yVals1.add(new BarEntry(6500, 5));

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        /*for (int i = 0; i < count + 1; i++) {
            yVals1.add(new Entry((float) (Math.random() * mult) + mult / 5, i));
        }*/

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Dec");
        xVals.add("Jan");
        xVals.add("Dec");
        xVals.add("Jan");
        xVals.add("Dec");
        xVals.add("Jan");
        BarDataSet dataSet = new BarDataSet(yVals1, "Month");
        BarData barData = new BarData(xVals, dataSet);
        mChart.setData(barData);

        ArrayAdapter<String> spinerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, new String[]{"By Month", "By Month & Category"});
        CategorySpinner.setAdapter(spinerAdapter);

        return root;
    }
}
