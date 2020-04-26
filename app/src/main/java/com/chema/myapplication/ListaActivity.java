package com.chema.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class ListaActivity extends AppCompatActivity {

    private ImageButton del;
    private ImageButton btnShare;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        TextView nombreCompra = (TextView) findViewById(R.id.tvNombreLista);
        TextView fecha = (TextView) findViewById(R.id.tvFecha);
        TextView descripcion = (TextView) findViewById(R.id.tvCompra);

        Intent intent = getIntent();
        b = intent.getExtras();

        if(b!=null) {
            nombreCompra.setText(b.getString("NOM"));
            fecha.setText(b.getString("FEC"));
            descripcion.setText(b.getString("DESC"));
        }

        del = (ImageButton) findViewById(R.id.btnDel);
        btnShare = (ImageButton) findViewById(R.id.btnShare);

        //Al hacer clic en borrar
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCustomDialog().show();
            }
        });

        //Al hacer clic en whatsapp
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCustomDialogwassap().show();
            }
        });

    }

    public AlertDialog createCustomDialog(){
        final AlertDialog alertDialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(ListaActivity.this);
        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_signin, null);
        Button btnFire = (Button) v.findViewById(R.id.AlertDialog_Negativo);
        Button btnCancel = (Button) v.findViewById(R.id.AlertDialog_Positivo);
        builder.setView(v);
        alertDialog = builder.create();

        btnFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ListaActivity.this, "Borrar lista de la compra", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        return alertDialog;
    }

    public AlertDialog createCustomDialogwassap(){
        final AlertDialog alertDialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(ListaActivity.this);
        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_share, null);
        ImageButton btnCorreo = (ImageButton) v.findViewById(R.id.AlertDialog_correo);
        ImageButton btnWaassap = (ImageButton) v.findViewById(R.id.AlertDialog_wassap);
        builder.setView(v);
        alertDialog = builder.create();

        btnCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentmail = new Intent(Intent.ACTION_VIEW , Uri.parse(""));
                intentmail.setType("plain/text");
                intentmail.putExtra(Intent.EXTRA_SUBJECT , "Lista de la compa: " + b.getString("NOM"));
                intentmail.putExtra(Intent.EXTRA_TEXT ,  b.getString("DESC"));
                startActivity(intentmail);

            }
        });

        btnWaassap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setPackage("com.whatsapp");
                intent.putExtra(Intent.EXTRA_TEXT, b.getString("DESC"));

                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    ex.printStackTrace();

                    Toast.makeText(ListaActivity.this, "El dispositivo no tiene WHATSAPP instalado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return alertDialog;
    }
}
