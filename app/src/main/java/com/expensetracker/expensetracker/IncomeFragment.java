package com.expensetracker.expensetracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.expensetracker.db.DBBitmapUtility;
import com.expensetracker.db.DBHelper;
import com.expensetracker.modal.Income;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Krush on 04-Dec-15.
 */
public class IncomeFragment extends Fragment {

    public static final int RESULT_PHOTO = 1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "FragmentIncome";
    private static final int THIS_MONTH = 0;
    private static final int LAST_MONTH = 1;
    private static final int LAST3MONTH = 3;
    private static final int LAST6MONTH = 6;
    private static int SPINNER_SELECTED;
    private DBHelper dbhelper;
    private FloatingActionButton fab;
    private RecyclerView mRecyclerView;
    private TextView emptyView;
    private ImageView image, calender_image;
    private IncomeAdapter mAdapter;
    private byte[] imageArray = null;
    private List<Income> listIncome = new ArrayList<>();
    private EditText title, amount, date;
    private Spinner spinner_category, spinner_acount;
    private TextView error;
    private boolean IMAGE_SET;
    private static int accountID, categoryID;
    private static String accountName, categoryName;

    private OnFragmentInteractionListener mListener;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IncomeFragment() {
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
    public static IncomeFragment newInstance(String param1, String param2) {
        IncomeFragment fragment = new IncomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mListener.onFragmentInteraction("Income Section");
        Log.e(TAG, "OnStart");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        Log.e(TAG, "OnAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.e(TAG, "OnCreate");
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_category, menu);

        MenuItem item = menu.findItem(R.id.menu_spinner);
        Spinner spinner_menu = (Spinner) MenuItemCompat.getActionView(item);
        ArrayList<String> list = new ArrayList<String>();
        list.add("Current Month");
        list.add("Last Month");
        list.add("Last 3 Month");
        list.add("Last 6 Month");

        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getActivity(), list);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,list);
        spinner_menu.setAdapter(spinnerAdapter);

        spinner_menu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        SPINNER_SELECTED = THIS_MONTH;
                        // Showing selected spinner item
                        listIncome = getListIncome(THIS_MONTH);

                        isEmpty();

                        mAdapter = new IncomeAdapter(listIncome, R.layout.cardview_category);
                        mRecyclerView.setAdapter(mAdapter);

                        Toast.makeText(getActivity(), "This Month",
                                Toast.LENGTH_LONG).show();

                        break;

                    case 1:
                        SPINNER_SELECTED = LAST_MONTH;
                        // Showing selected spinner item
                        listIncome = getListIncome(LAST_MONTH);

                        isEmpty();

                        mAdapter = new IncomeAdapter(listIncome, R.layout.cardview_category);
                        mRecyclerView.setAdapter(mAdapter);

                        Toast.makeText(getActivity(), "Last Month",
                                Toast.LENGTH_LONG).show();

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_income, null);

        AdView adView = (AdView) root.findViewById(R.id.adView);
        if (NetworkState.getNetworkState(getActivity())) {
            adView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder()
                    .setRequestAgent("android_studio:ad_template").build();
            adView.loadAd(adRequest);
        } else
            adView.setVisibility(View.GONE);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.rv_income);
        fab = (FloatingActionButton) root.findViewById(R.id.fab);
        emptyView = (TextView) root.findViewById(R.id.empty_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        listIncome = getListIncome(THIS_MONTH);

        isEmpty();

        mAdapter = new IncomeAdapter(listIncome, R.layout.cardview_income);

        mRecyclerView.setAdapter(mAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertIncome();
            }
        });
        return root;
    }

    // Check Blank DataSet
    private void isEmpty() {
        if (listIncome.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    //Get All Account info from our Database
    private List<Income> getListIncome(int month) {
        dbhelper = new DBHelper(getActivity());
        listIncome = new ArrayList<>();
        Cursor c = dbhelper.getAllIncomes(month);
        if (c.moveToFirst()) {
            do {
                Income income = new Income();
                income.setId(c.getInt(c.getColumnIndex(DBHelper.INCOME_TABLE_NAME + "." + DBHelper.INCOME_COLUMN_ID)));
                income.setTitle(c.getString(c.getColumnIndex(DBHelper.INCOME_TABLE_NAME + "." + DBHelper.INCOME_COLUMN_TITLE)));
                income.setAmount(c.getFloat(c.getColumnIndex(DBHelper.INCOME_TABLE_NAME + "." + DBHelper.INCOME_COLUMN_AMOUNT)));
                income.setImage(c.getBlob(c.getColumnIndex(DBHelper.INCOME_TABLE_NAME + "." + DBHelper.INCOME_COLUMN_IMAGE)));
                income.setAccount_name(c.getString(c.getColumnIndex(DBHelper.ACCOUNT_TABLE_NAME + "." + DBHelper.ACCOUNT_COLUMN_NAME)));
                income.setAccount(c.getInt(c.getColumnIndex(DBHelper.INCOME_COLUMN_ACCOUNT)));
                income.setCategory_name(c.getString(c.getColumnIndex(DBHelper.ICATEGORY_TABLE_NAME + "." + DBHelper.ICATEGORY_COLUMN_NAME)));
                income.setCategory(c.getInt(c.getColumnIndex(DBHelper.INCOME_COLUMN_CATEGORY)));
                income.setDate(c.getString(c.getColumnIndex(DBHelper.INCOME_TABLE_NAME + "." + DBHelper.INCOME_COLUMN_DATE)));
                Log.e(TAG, income.id + income.title + income.account + income.account_name + income.category_name);
                listIncome.add(income);
            }
            while (c.moveToNext());
            dbhelper.close();
        }
        return listIncome;
    }

    // Bottom Menu for Delete and Update Option on long press of account
    private void showBottomMenu(final String id, final int position) {
        new BottomSheet.Builder(getActivity()).title("Choose Action").sheet(R.menu.bottom_menu).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.edit:
                        updateIncome(id, position);
                        break;
                    case R.id.delete:
                        deleteConfirmation(id, position);
                        break;
                }
            }
        }).show();
    }

    // Delete confirmation for account deletion
    private void deleteConfirmation(final String id, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are You Sure to Delete Income ? ? ?");
        builder.setCancelable(true);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int result = dbhelper.deleteIncome(Integer.parseInt(id));
                if (result > 0) {
                    Toast.makeText(getActivity(), "Income Record Successfully Deleted", Toast.LENGTH_LONG).show();
                    mAdapter.removeItem(position);
                } else {
                    Toast.makeText(getActivity(), "Something wrong ! ! ! Please try again", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Insert Account information inside our Account Table
    private void insertIncome() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (getActivity()).getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.dialog_add_income, null);
        builder.setView(alertView);

        title = (EditText) alertView.findViewById(R.id.et_title);
        amount = (EditText) alertView.findViewById(R.id.et_amount);
        date = (EditText) alertView.findViewById(R.id.et_date);
        error = (TextView) alertView.findViewById(R.id.tv_err_msg);
        image = (ImageView) alertView.findViewById(R.id.iv_image);
        spinner_acount = (Spinner) alertView.findViewById(R.id.spn_account);
        spinner_category = (Spinner) alertView.findViewById(R.id.spn_category);
        calender_image = (ImageView) alertView.findViewById(R.id.iv_calendar);

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        spinner_acount.setSelection(0);
        spinner_category.setSelection(0);

        initSpinner(0, 0, 0);

        date.setText(day + "-" + (month + 1) + "-" + year);
        calender_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DateDialog(date);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        builder.setTitle("Insert Income");
        builder.setCancelable(true);
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        image = (ImageView) alertView.findViewById(R.id.iv_image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                in.putExtra("crop", "true");
                in.putExtra("outputX", 75);
                in.putExtra("outputY", 75);
                in.putExtra("scale", true);
                in.putExtra("return-data", true);
                startActivityForResult(in, RESULT_PHOTO);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button btnPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btnPositive.setOnClickListener(new DialogListner(alertDialog, 0, 0, 0));

    }

    private void initSpinner(int operation, int account, int category) { // operation =0 for insert, operation=1 for update
        final ArrayList<HashMap<String, String>> listAccount = new ArrayList<>();
        dbhelper = new DBHelper(getActivity());

        int accountSelection = 0, categorySelection = 0;
        HashMap<String, String> accountMap = new HashMap<>();
        accountMap.put("id", "0");
        accountMap.put("name", "Select Account");
        listAccount.add(accountMap);
        Cursor c = dbhelper.getAllAccounts();
        if (c.moveToNext()) {
            c.moveToFirst();
            do {
                accountMap = new HashMap<>();
                accountMap.put("id", String.valueOf(c.getInt(c.getColumnIndex(DBHelper.ACCOUNT_COLUMN_ID))));
                accountMap.put("name", c.getString(c.getColumnIndex(DBHelper.ACCOUNT_COLUMN_NAME)));
                if (operation == 1) {
                    if (c.getInt(c.getColumnIndex(DBHelper.ACCOUNT_COLUMN_ID)) == account)
                        accountSelection = c.getPosition()+1;
                }
                listAccount.add(accountMap);
            } while (c.moveToNext());
        }
        CustomSpinnerKVAdapter adapterAccount = new CustomSpinnerKVAdapter(getActivity(), listAccount);
        spinner_acount.setAdapter(adapterAccount);
        spinner_acount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                accountID = Integer.parseInt(listAccount.get(position).get("id"));
                accountName = listAccount.get(position).get("name").toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_acount.setSelection(accountSelection);

        final ArrayList<HashMap<String, String>> listCategory = new ArrayList<>();
        HashMap<String, String> categoryMap = new HashMap<>();
        categoryMap.put("id", "0");
        categoryMap.put("name", "Select Category");
        categoryMap.put("def", "0");
        listCategory.add(categoryMap);
        Cursor c1 = dbhelper.getAllCategoriesI();
        if (c1.moveToNext()) {
            c1.moveToFirst();
            do {
                categoryMap = new HashMap<>();
                categoryMap.put("id", String.valueOf(c1.getInt(c1.getColumnIndex(DBHelper.ICATEGORY_COLUMN_ID))));
                categoryMap.put("name", c1.getString(c1.getColumnIndex(DBHelper.ICATEGORY_COLUMN_NAME)));
                categoryMap.put("def", String.valueOf(c1.getFloat(c1.getColumnIndex(DBHelper.ICATEGORY_COLUMN_DEFVAL))));
                if (operation == 1) {
                    if (c1.getInt(c1.getColumnIndex(DBHelper.ICATEGORY_COLUMN_ID)) == category)
                        categorySelection = c1.getPosition()+1;
                }
                listCategory.add(categoryMap);
            } while (c1.moveToNext());
        }
        CustomSpinnerKVAdapter adapterCategory = new CustomSpinnerKVAdapter(getActivity(), listCategory);
        spinner_category.setAdapter(adapterCategory);
        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryID = Integer.parseInt(listCategory.get(position).get("id"));
                categoryName = listCategory.get(position).get("name").toString();
                amount.setText(listCategory.get(position).get("def").toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_category.setSelection(categorySelection);
        Log.e(TAG,categorySelection+":"+accountSelection);
    }

    // Update Income information inside our Account Table
    private void updateIncome(String id, int position) {

        Cursor c = dbhelper.getIncome(Integer.parseInt(id));
        c.moveToFirst();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (getActivity()).getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.dialog_add_income, null);
        builder.setView(alertView);

        title = (EditText) alertView.findViewById(R.id.et_title);
        amount = (EditText) alertView.findViewById(R.id.et_amount);
        date = (EditText) alertView.findViewById(R.id.et_date);
        error = (TextView) alertView.findViewById(R.id.tv_err_msg);
        image = (ImageView) alertView.findViewById(R.id.iv_image);
        spinner_acount = (Spinner) alertView.findViewById(R.id.spn_account);
        spinner_category = (Spinner) alertView.findViewById(R.id.spn_category);
        calender_image = (ImageView) alertView.findViewById(R.id.iv_calendar);

        title.setText(c.getString(c.getColumnIndex(DBHelper.INCOME_COLUMN_TITLE)));
        amount.setText(String.valueOf(c.getFloat(c.getColumnIndex(DBHelper.INCOME_COLUMN_AMOUNT))));

        date.setText(c.getString(c.getColumnIndex(DBHelper.INCOME_COLUMN_DATE)));
        if (c.getBlob(c.getColumnIndex(DBHelper.INCOME_COLUMN_IMAGE)) == null)
            image.setImageResource(R.drawable.ic_profile);
        else
            image.setImageBitmap(DBBitmapUtility.getImage(c.getBlob(c.getColumnIndex(DBHelper.INCOME_COLUMN_IMAGE))));

        initSpinner(1, c.getInt(c.getColumnIndex(DBHelper.INCOME_COLUMN_ACCOUNT)), c.getInt(c.getColumnIndex(DBHelper.INCOME_COLUMN_CATEGORY)));
        builder.setTitle("Update Income");
        builder.setCancelable(true);
        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                in.putExtra("crop", "true");
                in.putExtra("outputX", 75);
                in.putExtra("outputY", 75);
                in.putExtra("scale", true);
                in.putExtra("return-data", true);
                startActivityForResult(in, RESULT_PHOTO);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button btnPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btnPositive.setOnClickListener(new DialogListner(alertDialog, 1, Integer.parseInt(id), position));

    }

    //Return Image as result Activity for Account Image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_PHOTO && resultCode == getActivity().RESULT_OK && data != null) {

            Bitmap bmp = (Bitmap) data.getExtras().get("data");

            image.setImageBitmap(bmp);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] b = baos.toByteArray();
            String encodedImageString = Base64.encodeToString(b, Base64.DEFAULT);

            imageArray = Base64.decode(encodedImageString, Base64.DEFAULT);
            IMAGE_SET = true;
        }
    }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(CharSequence title);
    }

    // Custom Adapter Class for Income
    public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder> {
        private List<Income> incomes = Collections.emptyList();
        private int rowLayout;

        public IncomeAdapter(List<Income> incomes, int rowLayout) {
            this.incomes = incomes;
            this.rowLayout = rowLayout;

        }

        public void updateList(List<Income> incomes) {
            this.incomes = incomes;
            notifyDataSetChanged();
            isEmpty();
        }

        public void addItem(int position, Income income) {
            incomes.add(position, income);
            notifyItemInserted(position);
            isEmpty();
        }

        public void removeItem(int position) {
            incomes.remove(position);
            notifyItemRemoved(position);
            isEmpty();
        }

        @Override
        public IncomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(IncomeAdapter.ViewHolder holder, int position) {
            Income income = incomes.get(position);
            holder.incomeTitle.setText(income.title);
            holder.incomeId.setText(String.valueOf(income.id));
            holder.incomeAmount.setText("Amount : " + String.valueOf(income.amount));
            holder.incomeAccount.setText("Account : " + income.account_name);
            holder.incomeCategory.setText("Category : " + income.category_name);
            holder.incomeDate.setText("Date : " + String.valueOf(income.date));
            if (income.image == null)
                holder.incomePhoto.setImageResource(R.drawable.ic_profile);
            else
                holder.incomePhoto.setImageBitmap(BitmapFactory.decodeByteArray(income.image, 0,
                        income.image.length));
        }

        @Override
        public int getItemCount() {
            return incomes == null ? 0 : incomes.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView incomeTitle, incomeAmount, incomeDate, incomeCategory, incomeAccount;
            private TextView incomeId;
            private ImageView incomePhoto;

            public ViewHolder(final View itemView) {
                super(itemView);
                incomeTitle = (TextView) itemView.findViewById(R.id.tv_income_title);
                incomeId = (TextView) itemView.findViewById(R.id.tv_income_id);
                incomePhoto = (ImageView) itemView.findViewById(R.id.iv_income_photo);
                incomeAmount = (TextView) itemView.findViewById(R.id.tv_income_amount);
                incomeCategory = (TextView) itemView.findViewById(R.id.tv_income_category);
                incomeAccount = (TextView) itemView.findViewById(R.id.tv_income_account);
                incomeDate = (TextView) itemView.findViewById(R.id.tv_income_date);

                incomeTitle.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(incomeId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
                incomeCategory.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(incomeId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
                incomeDate.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(incomeId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
                incomeAmount.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(incomeId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
                incomePhoto.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(incomeId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(incomeId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
            }

        }
    } //End of Income Adapter

    // Custom Dialog Listener for Positive Button of our Add Account Alert Dialog Box
    private class DialogListner implements View.OnClickListener {
        private final AlertDialog dialog;
        private int type;
        private int id, position;

        // Type = 0 for insert, Type = 1 for update
        public DialogListner(AlertDialog alertDialog, int type, int id, int position) {
            this.dialog = alertDialog;
            this.type = type;
            this.id = id;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (type) {
                case 0: // for inserting
                    if (title.getText().toString().trim().equalsIgnoreCase("") ||
                            title.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setVisibility(View.VISIBLE);
                        error.setText("Please Enter Income Name ! ! !");
                    } else if (amount.getText().toString().trim().equalsIgnoreCase("") ||
                            amount.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setVisibility(View.VISIBLE);
                        error.setText("Please Enter Income Amount ! ! !");
                    } else if (spinner_acount.getSelectedItemPosition() < 1) {
                        error.setVisibility(View.VISIBLE);
                        error.setText("Please Select / Insert Account ! ! !");
                    } else if (spinner_category.getSelectedItemPosition() < 1) {
                        error.setVisibility(View.VISIBLE);
                        error.setText("Please Select / Insert Category ! ! !");
                    } else {
                        Income dataToAdd = null;
                        error.setVisibility(View.GONE);
                        if (IMAGE_SET) {
                            dbhelper.insertIncome(Float.parseFloat(amount.getText().toString().trim()),
                                    title.getText().toString().trim(), imageArray,
                                    accountID, categoryID, date.getText().toString());
                            dataToAdd = new Income(Float.parseFloat(amount.getText().toString().trim()), DBHelper.AUTO_ID,
                                    imageArray, title.getText().toString().trim(), accountID, accountName, categoryID, categoryName, date.getText().toString());
                        } else {
                            dbhelper.insertIncome(Float.parseFloat(amount.getText().toString().trim()),
                                    title.getText().toString().trim(), null,
                                    accountID, categoryID, date.getText().toString());
                            dataToAdd = new Income(Float.parseFloat(amount.getText().toString().trim()), DBHelper.AUTO_ID,
                                    imageArray, title.getText().toString().trim(), accountID, accountName, categoryID, categoryName, date.getText().toString());
                        }
                        mAdapter.addItem(listIncome.size(), dataToAdd);
                        dialog.dismiss();
                    }
                    break;
                case 1: // for updating
                    if (title.getText().toString().trim().equalsIgnoreCase("") ||
                            title.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setVisibility(View.VISIBLE);
                        error.setText("Please Enter Income Name ! ! !");
                    } else if (amount.getText().toString().trim().equalsIgnoreCase("") ||
                            amount.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setVisibility(View.VISIBLE);
                        error.setText("Please Enter Income Amount ! ! !");
                    } else if (spinner_acount.getSelectedItemPosition() < 1) {
                        error.setVisibility(View.VISIBLE);
                        error.setText("Please Select / Insert Account ! ! !");
                    } else if (spinner_category.getSelectedItemPosition() < 1) {
                        error.setVisibility(View.VISIBLE);
                        error.setText("Please Select / Insert Category ! ! !");
                    } else {
                        Income dataToAdd;
                        error.setVisibility(View.GONE);
                        if (IMAGE_SET) {
                            dbhelper.updateIncomeWithImage(id, Float.parseFloat(amount.getText().toString().trim()),
                                    title.getText().toString().trim(), imageArray,
                                    accountID, categoryID, date.getText().toString());
                            dataToAdd = listIncome.get(position);
                            dataToAdd.setAmount(Float.parseFloat(amount.getText().toString().trim()));
                            dataToAdd.setAccount_name(accountName);
                            dataToAdd.setCategory_name(categoryName);
                            dataToAdd.setDate(date.getText().toString());
                            dataToAdd.setImage(imageArray);
                        } else {
                            dbhelper.updateIncomeWithImage(id, Float.parseFloat(amount.getText().toString().trim()),
                                    title.getText().toString().trim(), null,
                                    accountID, categoryID, date.getText().toString());
                            dataToAdd = listIncome.get(position);
                            dataToAdd.setAmount(Float.parseFloat(amount.getText().toString().trim()));
                            dataToAdd.setAccount_name(accountName);
                            dataToAdd.setCategory_name(categoryName);
                            dataToAdd.setDate(date.getText().toString());
                            dataToAdd.setImage(null);
                        }
                        listIncome.set(position, dataToAdd);
                        mAdapter.updateList(listIncome);
                        mAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                    break;
            }
        }
    } // End of Custom Listener
}
