package com.chema.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.chema.myapplication.Clases.SinglentonVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class DetaisAccountActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private EditText nombre, apellidos, correo, username;
    private TextView changePass;
    private Button btnSave;
    private String id;

    private Context mContext;
    private RequestQueue fRequestQueue;
    private SinglentonVolley volley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detais_account);

        mContext = this.getApplicationContext();
        volley = SinglentonVolley.getInstance(this);
        fRequestQueue = volley.getRequestQueue();

        nombre = findViewById(R.id.EtNombre);
        apellidos = findViewById(R.id.EtApellidos);
        correo = findViewById(R.id.EtCorreo);
        username = findViewById(R.id.EtUsername);

        prefs = getSharedPreferences("Preferences" , Context.MODE_PRIVATE);
        id = prefs.getString("id" , null);

        nombre.setText(prefs.getString("nombre" , null));
        apellidos.setText(prefs.getString("apellidos" , null));
        correo.setText(prefs.getString("correo" , null));
        username.setText(prefs.getString("nombre_login" , null));

        changePass = findViewById(R.id.tvChangePass);

        btnSave = findViewById(R.id.btnSaveChanges);

        //Save action button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nombre.getText().toString().isEmpty() ||
                        apellidos.getText().toString().isEmpty() ||
                        correo.getText().toString().isEmpty() ||
                        username.getText().toString().isEmpty()) {

                    Toast.makeText(DetaisAccountActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();

                } else {

                    if(Patterns.EMAIL_ADDRESS.matcher(correo.getText().toString()).matches() && correo.length() > 0){

                        JSONObject post = new JSONObject();
                        JSONObject usuario = new JSONObject();

                        try {
                            usuario.put("id", id);
                            usuario.put("nombre", nombre.getText().toString());
                            usuario.put("apellidos", apellidos.getText().toString());
                            usuario.put("correo", correo.getText().toString());
                            usuario.put("username", username.getText().toString());
                            post.put("usuario",usuario);
                            postRequestLogin(post);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        //Toast.makeText(DetaisAccountActivity.this, "Actualiza", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(DetaisAccountActivity.this, "Debe de indicar una dirección de correo válida", Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });

        //ChangePass action button
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resetPass = new Intent(DetaisAccountActivity.this , ResetPasswordActivity.class);
                startActivity(resetPass);
            }
        });

    }

    private void postRequestLogin(JSONObject data) {
        fRequestQueue = volley.getRequestQueue();
        String url = "https://compras.informehoras.es/saveAccount.php";

        JsonObjectRequest jsonRequestLogin=new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (Boolean.valueOf(response.getString("Autenticacion"))){

                                Toast.makeText(DetaisAccountActivity.this, response.getString("MSG"), Toast.LENGTH_LONG).show();

                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("correo" , correo.getText().toString());
                                editor.putString("nombre_login" , username.getText().toString());
                                editor.putString("nombre" , nombre.getText().toString());
                                editor.putString("apellidos" , apellidos.getText().toString());
                                editor.commit(); //sincrono
                                editor.apply();     //asincrono

                                Intent login = new Intent(DetaisAccountActivity.this, PrincipalActivity.class);
                                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(login);

                            } else {
                                Toast.makeText(DetaisAccountActivity.this, response.getString("MSG"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Handle error
                Log.v("RESPUESTAERROR", error.toString());

                if (error instanceof TimeoutError) {
                    //Toast.makeText(mContext,mContext.getString(R.string.error_network_timeout),Toast.LENGTH_LONG).show();
                    Toast.makeText(mContext, "Timeout error...", Toast.LENGTH_LONG).show();
                }else if(error instanceof NoConnectionError){
                    //Toast.makeText(mContext,mContext.getString(R.string.error_network_timeout),Toast.LENGTH_LONG).show();
                    Toast.makeText(mContext, "No connection...", Toast.LENGTH_LONG).show();

                } else if (error instanceof AuthFailureError) {
                    try {
                        Toast.makeText(mContext, "Login incorrecto...", Toast.LENGTH_LONG).show();
                        Log.v("RESPUESTAERRORATH", new String(error.networkResponse.data, "UTF-8"));
                        onStart();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //TODO
                } else if (error instanceof ServerError) {
                    //TODO
                    Log.v("RESPUESTAERROR.ServerE", ".");
                } else if (error instanceof NetworkError) {
                    Log.v("RESPUESTAERROR.NetworkE", ".");
                    //TODO
                } else if (error instanceof ParseError) {
                    //TODO
                    Log.v("RESPUESTAERROR", "ParseError");
                }
            }
        });
        jsonRequestLogin.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 5,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        fRequestQueue.add(jsonRequestLogin);

    }
}
