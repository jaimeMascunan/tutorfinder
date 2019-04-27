package com.the_finder_group.tutorfinder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.the_finder_group.tutorfinder.ConnManager.TFClientImple;
import com.the_finder_group.tutorfinder.Helper.Helper;
import com.the_finder_group.tutorfinder.Helper.SQLiteHandler;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

public class EditUser extends AppCompatActivity {

    private static final String TAG = EditUser.class.getSimpleName();

    private TextView txtName, txtEmail, txtOldPswd, txtOldPswdConfirm, txtNewPswd, edit_text_label;
    private AppCompatSpinner userType;
    private Button btnSave, btnEditPswd;
    private AlertDialog.Builder aDialog, canviPassword;
    private ProgressDialog pDialog;

    private int id, validateOptions, selectionSpinner;

    private String name, email, password, user_type, new_pswd_encrypted;
    private View mView;

    private SQLiteHandler db;
    private TFClientImple tfClientImple;
    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        txtName = (TextView) findViewById(R.id.edit_name);
        txtEmail = (TextView) findViewById(R.id.edit_email);
        edit_text_label = (TextView) findViewById(R.id.edit_spinner_text_label);
        userType = (AppCompatSpinner) findViewById(R.id.edit_spinner);
        btnSave = (Button) findViewById(R.id.save_changes_bttn);
        btnEditPswd = (Button) findViewById(R.id.canvi_password);

        //Vista del canvi de contrasenya
        LayoutInflater inflater = EditUser.this.getLayoutInflater();
        mView = inflater.inflate(R.layout.password_user_dialog, null);
        txtOldPswd = (TextView) mView.findViewById(R.id.edit_old_password);
        txtOldPswdConfirm = (TextView) mView.findViewById(R.id.edit_confirm_old_password);
        txtNewPswd = (TextView) mView.findViewById(R.id.edit_new_password);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //Alert dialog
        aDialog = new AlertDialog.Builder(this);
        aDialog.setTitle("Atenció!");
        aDialog.setIcon(android.R.drawable.ic_dialog_info);
        aDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //Alert dialog per al canvi de contrasenya
        canviPassword = new AlertDialog.Builder(this);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        //Helper amb les clases de validacio de les dades introduides
        helper = new Helper(getApplicationContext());

        //TFClient implementation
        tfClientImple = new TFClientImple();

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        id = Integer.parseInt(user.get("user_id"));

        //Per esborrar despres
        Log.d(TAG, String.valueOf(id));
        name = user.get(getResources().getString(R.string.name));
        email = user.get(getResources().getString(R.string.email));
        password = user.get(getResources().getString(R.string.password));
        user_type = user.get(getResources().getString(R.string.user_type));

        //Adaptador de l'spinner amb els diferents tipus d'usuaris
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.user_type_register, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userType.setAdapter(adapter);

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);
        if(user_type.equals("student")){
            selectionSpinner = 1;
        }else if (user_type.equals("tutor")){
            selectionSpinner = 2;
        }else if (user_type.equals("admin")){
            userType.setVisibility(View.INVISIBLE);
            edit_text_label.setVisibility(View.INVISIBLE);
        }

        userType.setSelection(selectionSpinner);



        // Logout button click event
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_user();
            }
        });

        //Boto per canviar password
        btnEditPswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mView.getParent() != null) {
                    ((ViewGroup)mView.getParent()).removeView(mView);
                }
                canviPassword.setView(mView);
                canviPassword.setPositiveButton("Guardar canvis", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        //Encripteem les dades introduides per l'usuari per fer la comprobacio
                        String pswrd_old = txtOldPswd.getText().toString();
                        String pswrd_old_confirmacio = txtOldPswdConfirm.getText().toString();
                        String pswrd_new = txtNewPswd.getText().toString();
                        switch ((validateOptions = helper.validate_edit_password(pswrd_old, pswrd_old_confirmacio,
                                pswrd_new, password))) {
                            case 0:
                                aDialog.setMessage("Las contraseñas a cambiar no coinciden o es incorrecta");
                                aDialog.show();
                                break;

                            case 1:
                                aDialog.setMessage("La nueva contraseña no es correcta o no tiene un formato valido");
                                aDialog.show();
                                break;

                            case -1:
                                // Si no s'ha entrat a modificar cap dada, en realitat si que actualitzem el valor
                                //Pero amb el mateix que tenia abans
                                if((txtNewPswd.getText().toString().trim().isEmpty())){ txtNewPswd.setText(password); };
                                try {
                                    new_pswd_encrypted = helper.generateStorngPasswordHash(txtNewPswd.getText().toString());
                                    new edit_user_password().execute(name, new_pswd_encrypted);
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                } catch (InvalidKeySpecException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                });

                canviPassword.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //Pot ser que s'haguin introduit alguns valors, fem clear i reiniciem
                        txtOldPswd.setText("");
                        txtOldPswdConfirm.setText("");
                        txtNewPswd.setText("");
                        btnEditPswd.setEnabled(true);
                    }
                });

                canviPassword.show();
            }
        });
    }

    /**
     * Metode per realitzar la conexio al servidor i obtenir un boolea en funcio de si s'ha trobat
     * una coincidencia o no amb els registres guardats a la base de dades
     */
    public void edit_user() {

        if (!helper.validate_edit_user(txtName, txtEmail)) {
            aDialog.setMessage("No s'ha pogut realitzar el canvi de dades. Aquestes no soon correctes");
            aDialog.show();
            return;
        }

        if (userType.getSelectedItem().toString().equals("")){
            new edit_user().execute(String.valueOf(id), txtName.getText().toString().trim(),
                    txtEmail.getText().toString().trim(), "admin");

        }else{
            new edit_user().execute(String.valueOf(id), txtName.getText().toString().trim(),
                    txtEmail.getText().toString().trim(), userType.getSelectedItem().toString());
        }


        Log.d(TAG, "Edit usuari");
    }

    /**
     * Clase per realitzar la conexio amb la base de dades. Aquesta taska rebra parametres de tipus string
     * retornara un boolea i no definim cap tipus d'unitat de progressio
     */
    private class edit_user extends AsyncTask<String, Void, Boolean> {
        String userId, userName, email, user_type;

        //Abans de res llancem el progres dialog per informar a l'usuari/a de l'accio que s'esta realitzant
        @Override
        protected void onPreExecute() {
            pDialog.setMessage("Editing user...");
            showDialog();
        }

        @Override
        //Instaniem un objecte de la clase tfClientImpl per realitzar la conexio
        protected Boolean doInBackground(String... strings) {
            userId = strings[0];
            userName = strings[1];
            email = strings[2];
            user_type = strings[3];
            boolean edit_success = tfClientImple.editUser(Integer.parseInt(userId), userName, email, user_type, getApplicationContext());

            return edit_success;
        }

        @Override
        /**
         * Amaguem el progres dialog. En cas que s'hagi trobat un usuari que concideixi amb les credencials pasades,
         * l'afegim a la base de dades local, modifiquem el valor de la sessio i redirigim a la activitat corresponent.
         * En aquest cas encara falta implementar la conexio a la base de dades i per aixo definim "admin"
         */
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            hideDialog();
            if (result != null) {
                db.editUser(Integer.parseInt(userId), userName, email, user_type);

                helper.redirectUserTypeAct(user_type);
                finish();

            } else {
                aDialog.setMessage("Fallo al editar el usuari/a");
                aDialog.show();
            }
        }
    }

    /**
     * Clase per realitzar la conexio amb la base de dades. Aquesta taska rebra parametres de tipus string
     * retornara un boolea i no definim cap tipus d'unitat de progressio
     */
    private class edit_user_password extends AsyncTask<String, Void, Boolean> {
        String userName, password;

        //Abans de res llancem el progres dialog per informar a l'usuari/a de l'accio que s'esta realitzant
        @Override
        protected void onPreExecute() {
            pDialog.setMessage("Editing user password...");
            showDialog();
        }

        @Override
        //Instaniem un objecte de la clase tfClientImpl per realitzar la conexio
        protected Boolean doInBackground(String... strings) {
            userName = strings[0];
            password = strings[1];
            boolean edit_success = tfClientImple.editUserPswd(userName, password, getApplicationContext());

            return edit_success;
        }

        @Override
        /**
         * Amaguem el progres dialog. En cas que s'hagi trobat un usuari que concideixi amb les credencials pasades,
         * l'afegim a la base de dades local, modifiquem el valor de la sessio i redirigim a la activitat corresponent.
         * En aquest cas encara falta implementar la conexio a la base de dades i per aixo definim "admin"
         */
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            hideDialog();
            if (result != null) {
                db.editUserPassword(userName, password);

            } else {
                aDialog.setMessage("Fallo al editar el usuari/a");
                aDialog.show();
            }
        }
    }

    //Mostrem el progres dialog
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    //Amaguem el progres dialog
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        //desabilitamos volver atras de la MainActivity
        moveTaskToBack(true);
    }
}

