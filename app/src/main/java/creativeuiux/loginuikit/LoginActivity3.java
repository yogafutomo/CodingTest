package creativeuiux.loginuikit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import creativeuiux.loginuikit.KeyValue;

import java.util.ArrayList;

import adapter.LoginListAdapter;
import modalclass.ModalClass;

public class MainActivity extends AppCompatActivity {

    Button bt_login;
    EditText et_username, et_password;
    Gson gson;
    SharedPreferences sharedPreferences;
    String URL = "https://jsonblob.com/api/ad31aaa7-0204-11eb-9f82-67ccf3e534c1";

    public static final String Sp_Status = "Status";
    public static final String MyPref = "MyPref";
    static int mStatusCode = 0;
    public String username, password;
    private Boolean exit = false;


    private ArrayList<ModalClass> modalClasses;

    private RecyclerView recyclerView;
    private LoginListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login3);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        bt_login = (Button) findViewById(R.id.bt_login);
        OnClick();
        sharedPreferences = getSharedPreferences(MyPref, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(MainActivity.Sp_Status,"").matches("LoggedIn")){
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        }

    }

        private void OnClick(){
            bt_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    username = et_username.getText().toString().trim();
                    password = et_password.getText().toString().trim();
                    if (username.length() >= 1) {
                        if (password.length() >= 1) {
                            loginapi();
                        } else {
                            et_password.setError("Please enter Password");
                        }
                    } else {
                        et_username.setError("Please enter Username");
                    }

                }
            });
        }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }
    private void loginapi() {
        ArrayList params = new ArrayList();
        params.add(new KeyValue("UserName", username));
        params.add(new KeyValue("Password", password));
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener() {
                    @Override
                    public void onResponse(String response) {
                        sharedPreferences = getSharedPreferences(MyPref, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        gson = new Gson();
                        switch (mStatusCode) {
                            case 200:
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    gson.fromJson(jsonObject.toString(), KeyValue.class);
                                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    editor.putString(Sp_Status, "LoggedIn");
                                    editor.commit();
                                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(MainActivity.this, "Server Down", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Response parseNetworkResponse(NetworkResponse response) {
                mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        stringRequest.setRetryPolicy(new
                DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        requestQueue.add(stringRequest);
    }
}

//        // Use of  Login List Adapter
//        mAdapter = new LoginListAdapter(MainActivity.this,modalClasses);
//
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(mAdapter);
   
