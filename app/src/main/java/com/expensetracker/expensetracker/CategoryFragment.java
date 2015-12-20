package com.expensetracker.expensetracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.expensetracker.db.DBBitmapUtility;
import com.expensetracker.db.DBHelper;
import com.expensetracker.modal.CategoryExpense;
import com.expensetracker.modal.CategoryIncome;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {
    public static final int RESULT_PHOTO = 1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "FragmentCategory";
    private static final int INCOME = 0;
    private static final int EXPENSE = 1;
    private static int SPINNER_SELECTED;
    private OnFragmentInteractionListener mListener;
    private DBHelper dbhelper;
    private FloatingActionButton fab;
    private RecyclerView mRecyclerView;
    private TextView emptyView;
    private ImageView image;
    private CategoryIAdapter mIAdapter;
    private CategoryEAdapter mEAdapter;
    private byte[] imageArray = null;
    private List<CategoryIncome> listCategoryI = new ArrayList<>();
    private List<CategoryExpense> listCategoryE = new ArrayList<>();
    private EditText name, desc, defValue;
    private TextView error;
    private CheckBox constant, variable;
    private boolean IMAGE_SET;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CategoryFragment() {
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
    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mListener.onFragmentInteraction("Category");
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
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onpause");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "ondetach");
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_category, menu);

        MenuItem item = menu.findItem(R.id.menu_spinner);
        Spinner spinner_menu = (Spinner) MenuItemCompat.getActionView(item);
        ArrayList<String> list = new ArrayList<String>();
        list.add("Income");
        list.add("Expense");

        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getActivity(), list);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,list);
        spinner_menu.setAdapter(spinnerAdapter);

        spinner_menu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        SPINNER_SELECTED = INCOME;
                        // Showing selected spinner item
                        listCategoryI = getListCategoryI();

                        isEmpty();

                        mIAdapter = new CategoryIAdapter(listCategoryI, R.layout.cardview_category);
                        mRecyclerView.setAdapter(mIAdapter);

                        Toast.makeText(getActivity(), "Income",
                                Toast.LENGTH_LONG).show();

                        break;

                    case 1:
                        SPINNER_SELECTED = EXPENSE;
                        // Showing selected spinner item
                        listCategoryE = getListCategoryE();

                        isEmpty();

                        mEAdapter = new CategoryEAdapter(listCategoryE, R.layout.cardview_category);
                        mRecyclerView.setAdapter(mEAdapter);

                        Toast.makeText(getActivity(), "Expense",
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_category, null);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.rv_category);
        fab = (FloatingActionButton) root.findViewById(R.id.fab);
        emptyView = (TextView) root.findViewById(R.id.empty_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SPINNER_SELECTED == INCOME)
                    insertICategory();
                else
                    insertECategory();
            }
        });
        return root;
    }

    // Check Blank DataSet
    private void isEmpty() {
        if (listCategoryI.isEmpty() && SPINNER_SELECTED == INCOME) {
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            return;
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        if (listCategoryE.isEmpty() && SPINNER_SELECTED == EXPENSE) {
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    //Get All Income Category info from our Database
    private List<CategoryIncome> getListCategoryI() {
        dbhelper = new DBHelper(getActivity());
        listCategoryI = new ArrayList<>();
        Cursor c = dbhelper.getAllCategoriesI();
        if (c.moveToNext()) {
            c.moveToFirst();
            do {
                CategoryIncome category = new CategoryIncome();
                category.id = c.getInt(c.getColumnIndex(DBHelper.ICATEGORY_COLUMN_ID));
                category.name = c.getString(c.getColumnIndex(DBHelper.ICATEGORY_COLUMN_NAME));
                category.image = c.getBlob(c.getColumnIndex(DBHelper.ICATEGORY_COLUMN_IMAGE));
                category.defValue = c.getFloat(c.getColumnIndex(DBHelper.ICATEGORY_COLUMN_DEFVAL));
                listCategoryI.add(category);
            }
            while (c.moveToNext());
            dbhelper.close();
        }
        return listCategoryI;
    }

    //Get All Expense Category info from our Database
    private List<CategoryExpense> getListCategoryE() {
        dbhelper = new DBHelper(getActivity());
        listCategoryE = new ArrayList<>();
        Cursor c = dbhelper.getAllCategoriesE();
        if (c.moveToNext()) {
            c.moveToFirst();
            do {
                CategoryExpense category = new CategoryExpense();
                category.id = c.getInt(c.getColumnIndex(DBHelper.ECATEGORY_COLUMN_ID));
                category.name = c.getString(c.getColumnIndex(DBHelper.ECATEGORY_COLUMN_NAME));
                category.image = c.getBlob(c.getColumnIndex(DBHelper.ECATEGORY_COLUMN_IMAGE));
                category.defValue = c.getFloat(c.getColumnIndex(DBHelper.ECATEGORY_COLUMN_DEFVAL));
                category.constant_expense = c.getInt(c.getColumnIndex(DBHelper.ECATEGORY_COLUMN_CONSTANT));
                category.variable_expense = c.getInt(c.getColumnIndex(DBHelper.ECATEGORY_COLUMN_VARIABLE));
                listCategoryE.add(category);
            }
            while (c.moveToNext());
            dbhelper.close();
        }
        return listCategoryE;
    }

    // Bottom Menu for Delete and Update Option on long press of category
    private void showBottomMenu(final String id, final int position) {
        new BottomSheet.Builder(getActivity()).title("Choose Action").sheet(R.menu.bottom_menu).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.edit:
                        if (SPINNER_SELECTED == INCOME)
                            updateICategory(id, position);
                        else
                            updateECategory(id, position);
                        break;
                    case R.id.delete:
                        if (SPINNER_SELECTED == INCOME)
                            deleteIConfirmation(id, position);
                        else
                            deleteEConfirmation(id, position);
                        break;
                }
            }
        }).show();
    }

    // Delete confirmation for Income Category
    private void deleteIConfirmation(final String id, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are You Sure to Delete Category ? ? ?");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int result = dbhelper.deleteCategoryI(Integer.parseInt(id));
                if (result > 0) {
                    Toast.makeText(getActivity(), "Category Successfully Deleted", Toast.LENGTH_LONG).show();
                    mIAdapter.removeItem(position);
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

    // Delete confirmation for Expense Category
    private void deleteEConfirmation(final String id, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are You Sure to Delete Category ? ? ?");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int result = dbhelper.deleteCategoryE(Integer.parseInt(id));
                if (result > 0) {
                    Toast.makeText(getActivity(), "Category Successfully Deleted", Toast.LENGTH_LONG).show();
                    mEAdapter.removeItem(position);
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

    // Insert Income Category information inside our Category Table
    private void insertICategory() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (getActivity()).getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.dialog_add_category_income, null);
        builder.setView(alertView);
        name = (EditText) alertView.findViewById(R.id.et_name);
        desc = (EditText) alertView.findViewById(R.id.et_description);
        defValue = (EditText) alertView.findViewById(R.id.et_defvalue);
        error = (TextView) alertView.findViewById(R.id.tv_err_msg);

        builder.setTitle("Insert Category");
        builder.setCancelable(false);
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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
        btnPositive.setOnClickListener(new DialogIListner(alertDialog, 0, 0, 0));
        //public DialogListner(AlertDialog alertDialog, int type, int id, int position)

    }

    // Insert Expense Category information inside our Category Table
    private void insertECategory() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (getActivity()).getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.dialog_add_category_expense, null);
        builder.setView(alertView);
        name = (EditText) alertView.findViewById(R.id.et_name);
        desc = (EditText) alertView.findViewById(R.id.et_description);
        defValue = (EditText) alertView.findViewById(R.id.et_defvalue);
        constant = (CheckBox) alertView.findViewById(R.id.cb_constant);
        variable = (CheckBox) alertView.findViewById(R.id.cb_variable);
        error = (TextView) alertView.findViewById(R.id.tv_err_msg);

        builder.setTitle("Insert Expense Category");
        builder.setCancelable(false);
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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
        btnPositive.setOnClickListener(new DialogEListner(alertDialog, 0, 0, 0));
        //public DialogListner(AlertDialog alertDialog, int type, int id, int position)

    }

    // Update Income Category information inside our Category Table
    private void updateICategory(String id, int position) {

        Cursor c = dbhelper.getCategoryI(Integer.parseInt(id));
        c.moveToFirst();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (getActivity()).getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.dialog_add_category_income, null);
        builder.setView(alertView);

        name = (EditText) alertView.findViewById(R.id.et_name);
        desc = (EditText) alertView.findViewById(R.id.et_description);
        defValue = (EditText) alertView.findViewById(R.id.et_defvalue);
        image = (ImageView) alertView.findViewById(R.id.iv_image);

        name.setText(c.getString(c.getColumnIndex(DBHelper.ICATEGORY_COLUMN_NAME)));
        desc.setText(c.getString(c.getColumnIndex(DBHelper.ICATEGORY_COLUMN_DESC)));
        defValue.setText(String.valueOf(c.getFloat(c.getColumnIndex(DBHelper.ICATEGORY_COLUMN_DEFVAL))));

        if (c.getBlob(c.getColumnIndex(DBHelper.ICATEGORY_COLUMN_IMAGE)) == null)
            image.setImageResource(R.drawable.ic_profile);
        else
            image.setImageBitmap(DBBitmapUtility.getImage(c.getBlob(c.getColumnIndex(DBHelper.ICATEGORY_COLUMN_IMAGE))));

        builder.setTitle("Update Category");
        builder.setCancelable(false);
        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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
        btnPositive.setOnClickListener(new DialogIListner(alertDialog, 1, Integer.parseInt(id), position));

    }

    // Update Expense Category information inside our Category Table
    private void updateECategory(String id, int position) {

        Cursor c = dbhelper.getCategoryE(Integer.parseInt(id));
        c.moveToFirst();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (getActivity()).getLayoutInflater();
        final View alertView = inflater.inflate(R.layout.dialog_add_category_expense, null);
        builder.setView(alertView);

        name = (EditText) alertView.findViewById(R.id.et_name);
        desc = (EditText) alertView.findViewById(R.id.et_description);
        defValue = (EditText) alertView.findViewById(R.id.et_defvalue);
        constant = (CheckBox) alertView.findViewById(R.id.cb_constant);
        variable = (CheckBox) alertView.findViewById(R.id.cb_variable);
        image = (ImageView) alertView.findViewById(R.id.iv_image);

        name.setText(c.getString(c.getColumnIndex(DBHelper.ECATEGORY_COLUMN_NAME)));
        desc.setText(c.getString(c.getColumnIndex(DBHelper.ECATEGORY_COLUMN_DESC)));
        defValue.setText(String.valueOf(c.getFloat(c.getColumnIndex(DBHelper.ECATEGORY_COLUMN_DEFVAL))));
        constant.setChecked(c.getInt(c.getColumnIndex(DBHelper.ECATEGORY_COLUMN_CONSTANT)) == 1 ? true : false);
        variable.setChecked(c.getInt(c.getColumnIndex(DBHelper.ECATEGORY_COLUMN_VARIABLE)) == 1 ? true : false);

        if (c.getBlob(c.getColumnIndex(DBHelper.ECATEGORY_COLUMN_IMAGE)) == null)
            image.setImageResource(R.drawable.ic_profile);
        else
            image.setImageBitmap(DBBitmapUtility.getImage(c.getBlob(c.getColumnIndex(DBHelper.ECATEGORY_COLUMN_IMAGE))));

        builder.setTitle("Update Expense Category");
        builder.setCancelable(false);
        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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
        btnPositive.setOnClickListener(new DialogEListner(alertDialog, 1, Integer.parseInt(id), position));

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

    // Custom Adapter Class for Income Category
    public class CategoryIAdapter extends RecyclerView.Adapter<CategoryIAdapter.ViewHolder> {
        private List<CategoryIncome> categoryIncomes = Collections.emptyList();
        private int rowLayout;

        public CategoryIAdapter(List<CategoryIncome> categoryIncomes, int rowLayout) {
            this.categoryIncomes = categoryIncomes;
            this.rowLayout = rowLayout;

        }

        public void updateList(List<CategoryIncome> categoryIncomes) {
            this.categoryIncomes = categoryIncomes;
            notifyDataSetChanged();
            isEmpty();
        }

        public void addItem(int position, CategoryIncome categoryIncome) {
            categoryIncomes.add(position, categoryIncome);
            notifyItemInserted(position);
            isEmpty();
        }

        public void removeItem(int position) {
            categoryIncomes.remove(position);
            notifyItemRemoved(position);
            isEmpty();
        }

        @Override
        public CategoryIAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CategoryIAdapter.ViewHolder holder, int position) {
            CategoryIncome categoryIncome = categoryIncomes.get(position);
            holder.categoryName.setText(categoryIncome.name);
            holder.categoryDefValue.setText(String.valueOf(categoryIncome.defValue));
            holder.categoryId.setText(String.valueOf(categoryIncome.id));
            if (categoryIncome.image == null)
                holder.categoryPhoto.setImageResource(R.drawable.ic_profile);
            else
                holder.categoryPhoto.setImageBitmap(BitmapFactory.decodeByteArray(categoryIncome.image, 0,
                        categoryIncome.image.length));
        }

        @Override
        public int getItemCount() {
            return categoryIncomes == null ? 0 : categoryIncomes.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView categoryName;
            private TextView categoryId;
            private TextView categoryDefValue;
            private ImageView categoryPhoto;

            public ViewHolder(final View itemView) {
                super(itemView);
                categoryName = (TextView) itemView.findViewById(R.id.tv_category_name);
                categoryId = (TextView) itemView.findViewById(R.id.tv_category_id);
                categoryPhoto = (ImageView) itemView.findViewById(R.id.iv_category_photo);
                categoryDefValue = (TextView) itemView.findViewById(R.id.tv_category_defValue);


                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(categoryId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });

                categoryName.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(categoryId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
                categoryPhoto.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(categoryId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
                categoryDefValue.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(categoryId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
            }

        }
    } //End of Category Adapter

    // Custom Adapter Class for Income Category
    public class CategoryEAdapter extends RecyclerView.Adapter<CategoryEAdapter.ViewHolder> {
        private List<CategoryExpense> categoryExpenses = Collections.emptyList();
        private int rowLayout;

        public CategoryEAdapter(List<CategoryExpense> categoryExpenses, int rowLayout) {
            this.categoryExpenses = categoryExpenses;
            this.rowLayout = rowLayout;
        }

        public void updateList(List<CategoryExpense> categoryExpenses) {
            this.categoryExpenses = categoryExpenses;
            notifyDataSetChanged();
            isEmpty();
        }

        public void addItem(int position, CategoryExpense categoryExpense) {
            categoryExpenses.add(position, categoryExpense);
            notifyItemInserted(position);
            isEmpty();
        }

        public void removeItem(int position) {
            categoryExpenses.remove(position);
            notifyItemRemoved(position);
            isEmpty();
        }

        @Override
        public CategoryEAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CategoryEAdapter.ViewHolder holder, int position) {
            CategoryExpense categoryExpense = categoryExpenses.get(position);
            holder.categoryName.setText(categoryExpense.name);
            holder.categoryDefValue.setText(String.valueOf(categoryExpense.defValue));
            holder.categoryId.setText(String.valueOf(categoryExpense.id));
            if (categoryExpense.image == null)
                holder.categoryPhoto.setImageResource(R.drawable.ic_profile);
            else
                holder.categoryPhoto.setImageBitmap(BitmapFactory.decodeByteArray(categoryExpense.image, 0,
                        categoryExpense.image.length));
        }

        @Override
        public int getItemCount() {
            return categoryExpenses == null ? 0 : categoryExpenses.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView categoryName;
            private TextView categoryId;
            private TextView categoryDefValue;
            private ImageView categoryPhoto;

            public ViewHolder(final View itemView) {
                super(itemView);
                categoryName = (TextView) itemView.findViewById(R.id.tv_category_name);
                categoryId = (TextView) itemView.findViewById(R.id.tv_category_id);
                categoryPhoto = (ImageView) itemView.findViewById(R.id.iv_category_photo);
                categoryDefValue = (TextView) itemView.findViewById(R.id.tv_category_defValue);

                categoryName.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(categoryId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
                categoryPhoto.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(categoryId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
                categoryDefValue.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(categoryId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBottomMenu(categoryId.getText().toString(), getAdapterPosition());
                        return false;
                    }
                });
            }

        }
    } //End of Expense Category Adapter

    // Custom Dialog Listener for Positive Button of our Add Category Alert Dialog Box
    private class DialogIListner implements View.OnClickListener {
        private final AlertDialog dialog;
        private int type;
        private int id, position;

        // Type = 0 for insert, Type = 1 for update
        public DialogIListner(AlertDialog alertDialog, int type, int id, int position) {
            this.dialog = alertDialog;
            this.type = type;
            this.id = id;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (type) {
                case 0: // for inserting
                    if (name.getText().toString().trim().equalsIgnoreCase("") ||
                            name.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setText("Please Enter Category Name ! ! !");
                    } else if (desc.getText().toString().trim().equalsIgnoreCase("") ||
                            desc.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setText("Please Enter Category Description ! ! !");
                    } else if (defValue.getText().toString().trim().equalsIgnoreCase("") ||
                            defValue.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setText("Default Value should be zero or higher ! ! !");
                    } else {
                        CategoryIncome dataToAdd;
                        if (IMAGE_SET) {
                            dbhelper.insertCategoryI(name.getText().toString().trim(),
                                    desc.getText().toString().trim(), imageArray, Float.parseFloat(defValue.getText().toString().trim()));
                            dataToAdd = new CategoryIncome(dbhelper.getCategoryI(name.getText().toString()),
                                    imageArray, name.getText().toString(), desc.getText().toString(),
                                    Float.parseFloat(defValue.getText().toString().trim()));
                        } else {
                            dbhelper.insertCategoryI(name.getText().toString().trim(),
                                    desc.getText().toString().trim(), null, Float.parseFloat(defValue.getText().toString().trim()));
                            dataToAdd = new CategoryIncome(dbhelper.getCategoryI(name.getText().toString()),
                                    null, name.getText().toString(), desc.getText().toString(),
                                    Float.parseFloat(defValue.getText().toString().trim()));
                        }
                        mIAdapter.addItem(listCategoryI.size(), dataToAdd);
                        dialog.dismiss();
                    }

                    break;
                case 1: // for updating
                    if (name.getText().toString().trim().equalsIgnoreCase("") ||
                            name.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setVisibility(View.VISIBLE);
                        error.setText("Please Enter Category Name ! ! !");
                    } else if (desc.getText().toString().trim().equalsIgnoreCase("") ||
                            desc.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setVisibility(View.VISIBLE);
                        error.setText("Please Enter Category Description ! ! !");
                    } else if (defValue.getText().toString().trim().equalsIgnoreCase("") ||
                            defValue.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setText("Default Value should be zero or higher ! ! !");
                    } else {
                        CategoryIncome dataToAdd;
                        error.setVisibility(View.INVISIBLE);
                        if (IMAGE_SET) {
                            dbhelper.updateCategoryIWithImage(id,
                                    name.getText().toString().trim(), desc.getText().toString().trim(),
                                    imageArray, Float.parseFloat(defValue.getText().toString().trim()));
                            dataToAdd = listCategoryI.get(position);
                            dataToAdd.setImage(imageArray);
                            dataToAdd.setName(name.getText().toString().trim());
                        } else {
                            dbhelper.updateCategoryIWithImage(id,
                                    name.getText().toString().trim(), desc.getText().toString().trim(),
                                    null, Float.parseFloat(defValue.getText().toString().trim()));
                            Log.e(TAG, id + name.getText().toString());
                            dataToAdd = listCategoryI.get(position);
                            dataToAdd.setImage(null);
                            dataToAdd.setName(name.getText().toString().trim());
                        }
                        listCategoryI.set(position, dataToAdd);
                        mIAdapter.updateList(listCategoryI);
                        mIAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                    break;

            }
        }
    } // End of Custom Listener

    // Custom Dialog Listener for Positive Button of our Add Category Alert Dialog Box
    private class DialogEListner implements View.OnClickListener {
        private final AlertDialog dialog;
        private int type;
        private int id, position;

        // Type = 0 for insert, Type = 1 for update
        public DialogEListner(AlertDialog alertDialog, int type, int id, int position) {
            this.dialog = alertDialog;
            this.type = type;
            this.id = id;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (type) {
                case 0: // for inserting
                    if (name.getText().toString().trim().equalsIgnoreCase("") ||
                            name.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setText("Please Enter Category Name ! ! !");
                    } else if (desc.getText().toString().trim().equalsIgnoreCase("") ||
                            desc.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setText("Please Enter Category Description ! ! !");
                    } else if (defValue.getText().toString().trim().equalsIgnoreCase("") ||
                            defValue.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setText("Default Value should be zero or higher ! ! !");
                    } else {
                        CategoryExpense dataToAdd;
                        if (IMAGE_SET) {
                            dbhelper.insertCategoryE(name.getText().toString().trim(),
                                    desc.getText().toString().trim(), imageArray,
                                    Float.parseFloat(defValue.getText().toString().trim()),
                                    constant.isChecked() == true ? 1 : 0,
                                    variable.isChecked() == true ? 1 : 0);
                            dataToAdd = new CategoryExpense(dbhelper.getCategoryE(name.getText().toString()),
                                    imageArray, name.getText().toString(), desc.getText().toString(),
                                    Float.parseFloat(defValue.getText().toString().trim()),
                                    constant.isChecked() == true ? 1 : 0,
                                    variable.isChecked() == true ? 1 : 0);
                        } else {
                            dbhelper.insertCategoryE(name.getText().toString().trim(),
                                    desc.getText().toString().trim(), null,
                                    Float.parseFloat(defValue.getText().toString().trim()),
                                    constant.isChecked() == true ? 1 : 0,
                                    variable.isChecked() == true ? 1 : 0);
                            dataToAdd = new CategoryExpense(dbhelper.getCategoryE(name.getText().toString()),
                                    null, name.getText().toString(), desc.getText().toString(),
                                    Float.parseFloat(defValue.getText().toString().trim()),
                                    constant.isChecked() == true ? 1 : 0,
                                    variable.isChecked() == true ? 1 : 0);
                        }
                        mEAdapter.addItem(listCategoryE.size(), dataToAdd);
                        dialog.dismiss();
                    }

                    break;
                case 1: // for updating
                    if (name.getText().toString().trim().equalsIgnoreCase("") ||
                            name.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setVisibility(View.VISIBLE);
                        error.setText("Please Enter Category Name ! ! !");
                    } else if (desc.getText().toString().trim().equalsIgnoreCase("") ||
                            desc.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setVisibility(View.VISIBLE);
                        error.setText("Please Enter Category Description ! ! !");
                    } else if (defValue.getText().toString().trim().equalsIgnoreCase("") ||
                            defValue.getText().toString().trim().equalsIgnoreCase(null)) {
                        error.setText("Default Value should be zero or higher ! ! !");
                    } else {
                        CategoryExpense dataToAdd;
                        error.setVisibility(View.INVISIBLE);
                        if (IMAGE_SET) {
                            dbhelper.updateCategoryEWithImage(id,
                                    name.getText().toString().trim(), desc.getText().toString().trim(),
                                    imageArray, Float.parseFloat(defValue.getText().toString().trim()),
                                    constant.isChecked() == true ? 1 : 0,
                                    variable.isChecked() == true ? 1 : 0);
                            dataToAdd = listCategoryE.get(position);
                            dataToAdd.setImage(imageArray);
                            dataToAdd.setName(name.getText().toString().trim());
                        } else {
                            dbhelper.updateCategoryEWithImage(id,
                                    name.getText().toString().trim(), desc.getText().toString().trim(),
                                    null, Float.parseFloat(defValue.getText().toString().trim()),
                                    constant.isChecked() == true ? 1 : 0,
                                    variable.isChecked() == true ? 1 : 0);
                            dataToAdd = listCategoryE.get(position);
                            dataToAdd.setImage(null);
                            dataToAdd.setName(name.getText().toString().trim());
                        }
                        listCategoryE.set(position, dataToAdd);
                        mEAdapter.updateList(listCategoryE);
                        mEAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                    break;

            }
        }
    } // End of Custom Listener
}