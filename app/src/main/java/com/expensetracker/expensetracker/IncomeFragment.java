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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Krush on 04-Dec-15.
 */
public class IncomeFragment extends Fragment {

    public static final int RESULT_PHOTO = 1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "FragmentAccount";
    private DBHelper dbhelper;
    private FloatingActionButton fab;
    private RecyclerView mRecyclerView;
    private TextView emptyView;
    private ImageView image;
    private IncomeAdapter mAdapter;
    private byte[] imageArray = null;
    private List<Income> listIncome = new ArrayList<>();
    private EditText title, amount, date;
    private Spinner spinner_category, spinner_acount;
    private TextView error;
    private boolean IMAGE_SET;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_income, null);

        AdView adView = (AdView) root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.rv_income);
        fab = (FloatingActionButton) root.findViewById(R.id.fab);
        emptyView = (TextView) root.findViewById(R.id.empty_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        listIncome = getListIncome();

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
    private List<Income> getListIncome() {
        dbhelper = new DBHelper(getActivity());
        listIncome = new ArrayList<>();
        Cursor c = dbhelper.getAllIncomes();
        if (c.moveToNext()) {
            c.moveToFirst();
            do {
                Income income = new Income();
                income.id = c.getInt(c.getColumnIndex(DBHelper.INCOME_COLUMN_ID));
                income.title = c.getString(c.getColumnIndex(DBHelper.INCOME_COLUMN_TITLE));
                income.amount = c.getFloat(c.getColumnIndex(DBHelper.INCOME_COLUMN_AMOUNT));
                income.image = c.getBlob(c.getColumnIndex(DBHelper.INCOME_COLUMN_IMAGE));
                income.account = c.getInt(c.getColumnIndex(DBHelper.INCOME_COLUMN_ACCOUNT));
                income.category = c.getInt(c.getColumnIndex(DBHelper.INCOME_COLUMN_CATEGORY));
                income.date = c.getString(c.getColumnIndex(DBHelper.INCOME_COLUMN_DATE));
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
                        updateAccount(id, position);
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
        builder.setTitle("Are You Sure to Delete Account ? ? ?");
        builder.setCancelable(true);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int result = dbhelper.deleteAccount(Integer.parseInt(id));
                if (result > 0) {
                    Toast.makeText(getActivity(), "Account Successfully Deleted", Toast.LENGTH_LONG).show();
                    mAdapter.removeItem(position);
                    ((MainActivity) getActivity()).getAccounts();
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

        title = (EditText) alertView.findViewById(R.id.et_name);
        title = (EditText) alertView.findViewById(R.id.et_title);
        amount = (EditText) alertView.findViewById(R.id.et_amount);
        date = (EditText) alertView.findViewById(R.id.et_date);
        error = (TextView) alertView.findViewById(R.id.tv_err_msg);
        image = (ImageView) alertView.findViewById(R.id.iv_image);
        spinner_acount = (Spinner) alertView.findViewById(R.id.spn_account);
        spinner_category = (Spinner) alertView.findViewById(R.id.spn_category);
        List<HashMap<String, String>> listAccount = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();
        ArrayAdapter<HashMap<String, String>> adapter = new ArrayAdapter<HashMap<String, String>>(getActivity(), R.layout.spinner_hashmap_row);

        dbhelper = new DBHelper(getActivity());
        Cursor c = dbhelper.getAllAccounts();
        if (c.moveToNext()) {
            c.moveToFirst();
            do {
                hashMap.put("id", String.valueOf(c.getInt(c.getColumnIndex(DBHelper.ACCOUNT_COLUMN_ID))));
                hashMap.put("name", c.getString(c.getColumnIndex(DBHelper.ACCOUNT_COLUMN_NAME)));
                adapter.add(hashMap);
            } while (c.moveToNext());
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_acount.setAdapter(adapter);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DateDialog(v);
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

    // Insert Account information inside our Account Table
    private void updateAccount(String id, int position) {

        Cursor c = dbhelper.getAccount(Integer.parseInt(id));
        c.moveToFirst();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (getActivity()).getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.dialog_add_account, null);
        builder.setView(alertView);

        title = (EditText) alertView.findViewById(R.id.et_title);
        amount = (EditText) alertView.findViewById(R.id.et_amount);
        date = (EditText) alertView.findViewById(R.id.et_date);
        error = (TextView) alertView.findViewById(R.id.tv_err_msg);
        image = (ImageView) alertView.findViewById(R.id.iv_image);

        title.setText(c.getString(c.getColumnIndex(DBHelper.ACCOUNT_COLUMN_NAME)));
        if (c.getBlob(c.getColumnIndex(DBHelper.ACCOUNT_COLUMN_IMAGE)) == null)
            image.setImageResource(R.drawable.ic_profile);
        else
            image.setImageBitmap(DBBitmapUtility.getImage(c.getBlob(c.getColumnIndex(DBHelper.ACCOUNT_COLUMN_IMAGE))));

        builder.setTitle("Update Account");
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
            holder.incomeAmount.setText(String.valueOf(income.amount));
            holder.incomeCategory.setText(String.valueOf(income.category));
            holder.incomeDate.setText(String.valueOf(income.date));
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
            private TextView incomeTitle, incomeAmount, incomeDate, incomeCategory;
            private TextView incomeId;
            private ImageView incomePhoto;

            public ViewHolder(final View itemView) {
                super(itemView);
                incomeTitle = (TextView) itemView.findViewById(R.id.tv_income_title);
                incomeId = (TextView) itemView.findViewById(R.id.tv_income_id);
                incomePhoto = (ImageView) itemView.findViewById(R.id.iv_income_photo);
                incomeAmount = (TextView) itemView.findViewById(R.id.tv_income_amount);
                incomeCategory = (TextView) itemView.findViewById(R.id.tv_income_category);
                incomeDate = (TextView) itemView.findViewById(R.id.tv_income_date);


                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(incomeId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
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

        }
    } // End of Custom Listener
}
