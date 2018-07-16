package com.dvinfosys.listviewfilters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private RecyclerView recyclerView;
    private String CompanyID, Link, Nature = "1";
    private List<CategoryGetSet> list;
    private CategoryAdapter adapter;
    private ImageView imgSortFilter, imgSearch;
    private EditText edtSearch;
    private LinearLayoutManager linearLayoutManager;
    private boolean filter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        list = new ArrayList<>();

        Nature = "1";
        Link = "http://192.168.1.12/StoreAPI/api/Industry/GetIndustry";

        recyclerView = (RecyclerView) findViewById(R.id.list_recycler);
        imgSortFilter = (ImageView) findViewById(R.id.img_ascorder);
        imgSearch = (ImageView) findViewById(R.id.img_search);
        edtSearch = (EditText) findViewById(R.id.edt_search);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        if (Common.isInternetAvailable(context)) {
            new GetCategory().execute(Link);
        } else {
            Common.showToast(context, "Please Check Your Internet Connection...");
        }

        imgSortFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filter) {
                    filter = false;
                    adapter.sortNameByAsc();
                } else {
                    filter = true;
                    adapter.sortNameByDesc();
                }
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtSearch.getVisibility() == View.VISIBLE) {
                    edtSearch.setVisibility(View.GONE);
                } else {
                    edtSearch.setVisibility(View.VISIBLE);
                }
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = String.valueOf(s);
                if (text.equals("")) {
                    text.isEmpty();
                    adapter.filter(text);
                } else {
                    adapter.filter(text);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int ID = item.getItemId();
        if (ID == R.id.nav_refrash) {
            adapter = new CategoryAdapter(context, list);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    private String GetCate(String string) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(string);
            String json = "";
            JSONObject jsonObject = new JSONObject();
/*
            jsonObject.accumulate("CompanyID", CompanyID);
            jsonObject.accumulate("FinancialYearID", FinancialYearID);*/

            jsonObject.accumulate("Nature", Nature);
            //jsonObject.accumulate(Constants.PARAM_Country, "India");
            //params.put(Constants.PARAM_Country, "India");


            json = jsonObject.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpClient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Dit Not Word";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Exit?")
                .setCancelable(false)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //A.this.finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        try {
            while ((line = bufferedReader.readLine()) != null)
                result += line;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private class GetCategory extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Common.showProgressDialog(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            return GetCate(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Common.dismissProgressDialog();

            if (s != null) {
                if (s.equals("[]")) {
                    Common.showToast(context, "Category No Available.");
                } else {
                    try {
                        JSONArray array = new JSONArray(s);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = (JSONObject) array.get(i);
                            CategoryGetSet getSet = new CategoryGetSet();

                            getSet.setIndustryID(jsonObject.getString("IndustryID"));
                            getSet.setIndustryName(jsonObject.getString("IndustryName"));
                            getSet.setNature(jsonObject.getString("Nature"));
                            getSet.setCount(jsonObject.getString("Count"));
                            list.add(getSet);
                        }

                        adapter = new CategoryAdapter(context, list);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Common.showToast(context, "Server Error.s");
            }

        }
    }
}
