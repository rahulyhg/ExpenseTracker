package com.expensetracker.expensetracker;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.expensetracker.db.DBHelper;
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
public class ReportFragmentTab2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "FragmentReportTab2";
    private static final int THIS_MONTH = 0;
    private static final int LAST_MONTH = -1;
    private static final int LAST3MONTH = -3;
    private static final int LAST6MONTH = -6;
    private static DateUtili dateUtili = new DateUtili();
    private Spinner CategorySpinner;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private BarChart mChart;
    private DBHelper dbhelper;

    public ReportFragmentTab2() {
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
    public static ReportFragmentTab2 newInstance(String param1, String param2) {
        ReportFragmentTab2 fragment = new ReportFragmentTab2();
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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_report_income, null);

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

        ArrayAdapter<String> spinerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                new String[]{"Income By Month", "Income By Month & Category", "Income By Category"});
        CategorySpinner.setAdapter(spinerAdapter);

        CategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        incomeByMonth();
                        break;
                    case 1:
                        incomeByMonthCategory();
                        break;
                    case 2:
                        incomeByCategory();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return root;
    }

    private void incomeByMonth() {
        dbhelper = new DBHelper(getActivity());

        Cursor c = dbhelper.getSumIncome(LAST6MONTH);
        c.moveToFirst();

        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<String>();

        do {
            yVals1.add(new BarEntry(c.getFloat(1), c.getPosition()));
            xVals.add(c.getString(0));
        } while (c.moveToNext());

        c.close();
        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        /*for (int i = 0; i < count + 1; i++) {
            yVals1.add(new Entry((float) (Math.random() * mult) + mult / 5, i));
        }*/

        BarDataSet dataSet = new BarDataSet(yVals1, "Month");
        BarData barData = new BarData(xVals, dataSet);
        mChart.setDescription("Income By Months");
        mChart.setData(barData);
        mChart.invalidate();
    }

    private void incomeByMonthCategory() {
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
        mChart.setDescription("Income By Months & Category");
        mChart.setData(barData);
        mChart.invalidate();

    }

    private void incomeByCategory() {
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        yVals1.add(new BarEntry(350, 0));
        yVals1.add(new BarEntry(600, 1));
        yVals1.add(new BarEntry(755, 2));
        yVals1.add(new BarEntry(705, 3));
        yVals1.add(new BarEntry(305, 4));
        yVals1.add(new BarEntry(650, 5));

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        /*for (int i = 0; i < count + 1; i++) {
            yVals1.add(new Entry((float) (Math.random() * mult) + mult / 5, i));
        }*/

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Dec");
        xVals.add("Jan");
        xVals.add("Feb");
        xVals.add("Jun");
        xVals.add("July");
        xVals.add("Aug");
        BarDataSet dataSet = new BarDataSet(yVals1, "Month");
        BarData barData = new BarData(xVals, dataSet);
        mChart.setDescription("Income By Category");
        mChart.setData(barData);
        mChart.invalidate();

    }
}
