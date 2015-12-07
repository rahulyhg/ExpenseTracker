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
import android.support.design.widget.TextInputLayout;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.expensetracker.db.DBBitmapUtility;
import com.expensetracker.db.DBHelper;
import com.expensetracker.modal.Account;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Krush on 11-Nov-15.
 */
public class AccountFragment extends Fragment {
    public static final int RESULT_PHOTO = 1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "FragmentAccount";
    private OnFragmentInteractionListener mListener;
    private DBHelper dbhelper;
    private FloatingActionButton fab;
    private RecyclerView mRecyclerView;
    private TextView emptyView;
    private ImageView image;
    private AccountAdapter mAdapter;
    private byte[] imageArray = null;
    private List<Account> listAccount = new ArrayList<>();
    private EditText name;
    private TextInputLayout input_name;
    private TextView error;
    private boolean IMAGE_SET;
    /*

    public static Fragment newInstance(Context context) {
        AccountFragment f = new AccountFragment();
        return f;
    }*/
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
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
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mListener.onFragmentInteraction("Account Section");
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_account, null);

        AdView adView = (AdView) root.findViewById(R.id.adView);
        if (NetworkState.getNetworkState(getActivity())) {
            adView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder()
                    .setRequestAgent("android_studio:ad_template").build();
            adView.loadAd(adRequest);
        } else
            adView.setVisibility(View.GONE);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.rv_account);
        fab = (FloatingActionButton) root.findViewById(R.id.fab);
        emptyView = (TextView) root.findViewById(R.id.empty_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        listAccount = getListAccount();

        isEmpty();

        mAdapter = new AccountAdapter(listAccount, R.layout.cardview_account);
        mRecyclerView.setAdapter(mAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertAccount();
            }
        });
        return root;
    }

    // Check Blank DataSet
    private void isEmpty() {
        if (listAccount.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    //Get All Account info from our Database
    private List<Account> getListAccount() {
        dbhelper = new DBHelper(getActivity());
        listAccount = new ArrayList<>();
        Cursor c = dbhelper.getAllAccounts();
        if (c.moveToNext()) {
            c.moveToFirst();
            do {
                Account account = new Account();
                account.id = c.getInt(c.getColumnIndex(DBHelper.ACCOUNT_COLUMN_ID));
                account.isSelected = c.getInt(c.getColumnIndex(DBHelper.ACCOUNT_COLUMN_SELECTED));
                account.name = c.getString(c.getColumnIndex(DBHelper.ACCOUNT_COLUMN_NAME));
                account.image = c.getBlob(c.getColumnIndex(DBHelper.ACCOUNT_COLUMN_IMAGE));
                listAccount.add(account);
            }
            while (c.moveToNext());
            dbhelper.close();
        }
        return listAccount;
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
    private void insertAccount() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (getActivity()).getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.dialog_add_account, null);
        builder.setView(alertView);
        input_name = (TextInputLayout) alertView.findViewById(R.id.input_et_name);
        name = (EditText) alertView.findViewById(R.id.et_name);
        error = (TextView) alertView.findViewById(R.id.tv_err_msg);

        builder.setTitle("Insert Account");
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

        name = (EditText) alertView.findViewById(R.id.et_name);
        error = (TextView) alertView.findViewById(R.id.tv_err_msg);
        image = (ImageView) alertView.findViewById(R.id.iv_image);

        name.setText(c.getString(c.getColumnIndex(DBHelper.ACCOUNT_COLUMN_NAME)));
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

    // Custom Adapter Class for Account
    public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {
        private List<Account> accounts = Collections.emptyList();
        private int rowLayout;

        public AccountAdapter(List<Account> accounts, int rowLayout) {
            this.accounts = accounts;
            this.rowLayout = rowLayout;

        }

        public void updateList(List<Account> accounts) {
            this.accounts = accounts;
            notifyDataSetChanged();
            isEmpty();
        }

        public void addItem(int position, Account account) {
            accounts.add(position, account);
            notifyItemInserted(position);
            isEmpty();
        }

        public void removeItem(int position) {
            accounts.remove(position);
            notifyItemRemoved(position);
            isEmpty();
        }

        @Override
        public AccountAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(AccountAdapter.ViewHolder holder, int position) {
            Account account = accounts.get(position);
            holder.accountName.setText(account.name);
            holder.accountId.setText(String.valueOf(account.id));
            if (account.image == null)
                holder.accountPhoto.setImageResource(R.drawable.ic_profile);
            else
                holder.accountPhoto.setImageBitmap(BitmapFactory.decodeByteArray(account.image, 0,
                        account.image.length));
            holder.isSelected.setChecked(account.isSelected == 1 ? true : false);
        }

        @Override
        public int getItemCount() {
            return accounts == null ? 0 : accounts.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView accountName;
            private TextView accountId;
            private ImageView accountPhoto;
            private CheckBox isSelected;

            public ViewHolder(final View itemView) {
                super(itemView);
                accountName = (TextView) itemView.findViewById(R.id.tv_account_name);
                accountId = (TextView) itemView.findViewById(R.id.tv_account_id);
                accountPhoto = (ImageView) itemView.findViewById(R.id.iv_account_photo);
                isSelected = (CheckBox) itemView.findViewById(R.id.cb_selected);


                isSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            Log.d(TAG, "onCheckedListner" + isChecked + accountId.getText().toString() + accountName.getText().toString());
                            dbhelper.updateAccount(Integer.parseInt(accountId.getText().toString()), accountName.getText().toString(), 1);
                        } else {
                            Log.d(TAG, "onCheckedListner" + isChecked + accountId.getText().toString() + accountName.getText().toString());
                            dbhelper.updateAccount(Integer.parseInt(accountId.getText().toString()), accountName.getText().toString(), 0);
                        }
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(accountId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
                accountName.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(accountId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
                accountPhoto.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(accountId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
            }

        }
    } //End of Account Adapter

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
                case 0:
                    if (name.getText().toString().trim().equalsIgnoreCase("") ||
                            name.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setText("Please Enter Account Name ! ! !");
                    } else {
                        Account dataToAdd;
                        if (IMAGE_SET) {
                            dbhelper.insertAccount(name.getText().toString().trim(), imageArray, 0);
                            dataToAdd = new Account(dbhelper.getAccount(name.getText().toString()),
                                    imageArray, name.getText().toString(), 0);
                        } else {
                            dbhelper.insertAccount(name.getText().toString().trim(), null, 0);
                            dataToAdd = new Account(dbhelper.getAccount(name.getText().toString()),
                                    null, name.getText().toString(), 0);
                        }
                        mAdapter.addItem(listAccount.size(), dataToAdd);
                        ((MainActivity) getActivity()).getAccounts();
                        dialog.dismiss();
                    }
                    break;
                case 1:
                    if (name.getText().toString().trim().equalsIgnoreCase("") ||
                            name.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setText("Please Enter Account Name ! ! !");
                    } else {
                        Account dataToAdd;
                        if (IMAGE_SET) {
                            dbhelper.updateAccountWithImage(id,
                                    name.getText().toString().trim(), imageArray);
                            dataToAdd = listAccount.get(position);
                            dataToAdd.setImage(imageArray);
                            dataToAdd.setName(name.getText().toString().trim());
                        } else {
                            dbhelper.updateAccountWithImage(id,
                                    name.getText().toString().trim(), null);
                            dataToAdd = listAccount.get(position);
                            dataToAdd.setImage(null);
                            dataToAdd.setName(name.getText().toString().trim());
                        }
                        listAccount.set(position, dataToAdd);
                        mAdapter.updateList(listAccount);
                        mAdapter.notifyDataSetChanged();
                        ((MainActivity) getActivity()).getAccounts();
                        dialog.dismiss();
                    }
                    break;
            }
        }
    } // End of Custom Listener
}
