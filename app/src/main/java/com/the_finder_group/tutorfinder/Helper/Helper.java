package com.the_finder_group.tutorfinder.Helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;

import com.the_finder_group.tutorfinder.AdminActivity;
import com.the_finder_group.tutorfinder.LoginActivity;
import com.the_finder_group.tutorfinder.R;
import com.the_finder_group.tutorfinder.StudentActivity;
import com.the_finder_group.tutorfinder.TutorActivity;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Clase que ofereix diverses funciolanitats de suport a altres clases i metodes de l'applicacio
 */
public class Helper {
    //Definim un context per a poder implementar els diferents metodes
    Context _context;
    //Constructor d'acces a la base de dades locas
    private SQLiteHandler db;
    //Sessionmanager que ens permet establir si hi ha un usuari loggegat a l'aplicacio o no
    private SessionManager session;
    //Codis per a solicitar acces a l'emmagatzament intern i la galeria
    private int STORAGE_PERMISSION_CODE= 1111;
    private int GALLERY_REQUEST_CODE = 1112;

    /**
     * constructor de la clase en que pasem un context com a parametre que s'ha indicat al inicialitzar la clase
     * @param context
     */
    public Helper(Context context) {
        this._context = context;

        // SqLite database handler
        db = new SQLiteHandler(_context);

        // session manager
        session = new SessionManager(_context);
    }

    /**
     * Regirigim a l'usuari a l'activitat corresponent al seu tipus d'usuari
     * Aqui tindra acces a funcionalitats en funcio d'aquest
     * @param user_type el tipus d'usuari
     */
    public void redirectUserTypeAct(String user_type) {
        if ((!user_type.isEmpty()) && (user_type != null)) {

            if (user_type.equals("admin")) {
                Intent intent = new Intent(_context, AdminActivity.class);
                _context.startActivity(intent);

            } else if (user_type.equals("student")) {
                Intent intent = new Intent(_context, StudentActivity.class);
                _context.startActivity(intent);

            } else if (user_type.equals("tutor")) {
                Intent intent = new Intent(_context, TutorActivity.class);
                _context.startActivity(intent);

            }
        }
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    public void logoutUser() {
        session.setLogin(false);

        db.deleteDB();

        // Launching the login activity
        Intent intent = new Intent(_context, LoginActivity.class);
        _context.startActivity(intent);
        ((Activity)_context).finish();

    }

    /**
     * Metode per validar les dades introduides al moment de crear un anunci.
     * @param ad_titol el titol de la publicacio a validar
     * @param ad_descripcio la descripcio de la publicacio a validar
     * @param ad_preu el preu de la publicacio a validar
     * @param ad_type el tipus de publicacio a validar
     * @return un boolea amb valor true en cas que els parametres passats compleixin els requisits indicats al metode
     */
    public boolean validate_ad(EditText ad_titol, EditText ad_descripcio,
                               EditText ad_preu, AppCompatSpinner ad_type){
        boolean valid = true;
        // Obtenim els valors en format string dels edittexts/appcompat spinner passats com a parametre
        String titol = ad_titol.getText().toString().trim();
        String descripcio = ad_descripcio.getText().toString().trim();
        String preu = ad_preu.getText().toString().trim();
        String categoria = ad_type.getSelectedItem().toString();

        //Comprobem que el titol de la publicacio no estigui vuit i que tingui mes de tres caracters
        //En cas contrari definim el missatge d'error per al edittext corresponent al valor a evaluar
        if (titol.isEmpty() || titol.length()<3){
            ad_titol.setError("Has de itroduir 3 caracters com a minim al titol");
            valid = false;
        }else{
            ad_titol.setError(null);
        }

        //Comprobem que la descripcio de la publicacio no estigui vuida i que tingui mes de 10 caracters
        //En cas contrari definim el missatge d'error per al edittext corresponent al valor a evaluar
        if (descripcio.isEmpty() || descripcio.length()<10){
            ad_descripcio.setError("La descripcio no es valida");
            valid = false;
        }else{
            ad_descripcio.setError(null);
        }
        //Comprobem que el preu de la publicacio no estigui vuit
        //En cas contrari definim el missatge d'error per al edittext corresponent al valor a evaluar
        if(preu.isEmpty()){
            ad_preu.setError("EL format ha de ser numeric");
            valid = false;
        }else{
            ad_preu.setError(null);
        }
        //Comprobem que s'hagui seleccionat un valor per al tipus d'anunci al appcompatspinner
        //En cas contrari definim el missatge d'error per al edittext corresponent al valor a evaluar
        if(categoria.isEmpty()){
            TextView errorText = (TextView)ad_type.getSelectedView();
            errorText.setError(_context.getResources().getString(R.string.error_field_required));
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(_context.getResources().getString(R.string.error_field_required));
            valid = false;
        }
        //True en cas de que tots els parametres s'haguin evaluat com a correctes d'acord als valors establerts
        return  valid;
    }

    /**
     * Metode per validar els valors introduits a l'hora de realitzar el canvi de password per a un usuari
     * @param pswrd_old el password anterior a la modificacio
     * @param pswrd_old_confirmatio repetim el valor del password previ a la modificacio
     * @param pswrd_new el nou password que volem introduir a la base de dades
     * @param db_password el password guardat a la base de dades
     * @return un valor numeric que ens indicara si l'operacio ha tingut exit, i en cas de fallar en quin punt ho ha fet
     */
    public int validate_edit_password(String pswrd_old, String pswrd_old_confirmatio, String pswrd_new, String db_password) {
        int valid = 1000;
        try {
            //Comprobem que les contrasenyes antigues coincideixin i que no estiguin buicdes. Tambe cridemal metode validatePassowrd
            if (((pswrd_old != null) && !(pswrd_old.isEmpty()) && (validatePassword(pswrd_old, db_password)))
                    && ((pswrd_old_confirmatio != null) && !(pswrd_old_confirmatio.isEmpty()) &&
                    //Aquest metode comprova que el valor introduit per l'usuari per la contrasenya sense encriptar
                    // coincideix amb el password encriptat a la base de dades
                    (validatePassword(pswrd_old_confirmatio, db_password)))) {
                //En cas que es compleixin els parametres establerts oomprobem que el camp per al password nou no s'hagi deixat buit
                //Tambe que tingui la llargada establesta
                if ((pswrd_new != null) && !(pswrd_new.isEmpty()) && (pswrd_new.length()>=4) && (pswrd_new.length()<=12)) {
                    //Valor per a una modificacio satisfactoria
                    valid = -1;
                } else {
                    //Valor per a una modificacio no satisfactoria
                    valid = 1;
                }
            } else {
                //Valor per a una modificacio no satisfactoria
                valid = 0;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return valid;
    }

    /**
     * Metode per a validar les dades que s'introdueixen a l'hora de modificar un objecte user
     * @param name_edit el nou nom per a l'usuari que volem introduir
     * @param email_edit el nou e-mail per a l'usuari que volem introduir
     * @return true en cas de que poguem realitzar la modificacio de forma satisfactoria
     */
    public boolean validate_edit_user(TextView name_edit, TextView email_edit){
        boolean valid = true;
        //Obtenim els valors dels textviews passats per paramentre
        String name  = name_edit.getText().toString();
        String email = email_edit.getText().toString();
        //Comprobem que el camp name no esta buit i que la extensio es superior a 3 caracters
        //En cas contrari definim l'error per al camp i tornem false com a resultat
        if(name.isEmpty() || name.length()<3){
            name_edit.setError(_context.getResources().getString(R.string.error_invalid_name));
            valid = false;
        }else{
            name_edit.setError(null);
        }
        //Comprobem que el camp email no esta buit i que el format es correspon amb el d'un correu-e
        //En cas contrari definim l'error per al camp i tornem false com a resultat
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_edit.setError(_context.getResources().getString(R.string.error_invalid_email));
            valid = false;
        }else{
            email_edit.setError(null);
        }
        return  valid;
    }

    /**
     * Metode per a validar les dades introduides al realitzar el login a l'aplicacio
     * @param name_login el nom de l'usuari introduit
     * @param password_login el password per a l'usuari amb que es vol fer login
     * @return true en cas que els parametres compleixin els requisits establerts
     */
    public boolean validate_login(EditText name_login, EditText password_login){
        boolean valid = true;
        //Obtenim els valors per als edittext passats com a parametre
        String name = name_login.getText().toString();
        String password = password_login.getText().toString();
        //Comprobem que el camp name no esta buit i que la extensio es superior a 3 caracters
        //En cas contrari definim l'error per al camp i tornem false com a resultat
        if(name.isEmpty() || name.length()<3){
            name_login.setError(_context.getResources().getString(R.string.error_invalid_name));
            valid = false;
        }else{
            name_login.setError(null);
        }
        //Comprobem que el camp password no esta buit i que la extensio es superior a 4 caracters i inferior a 12
        //En cas contrari definim l'error per al camp i tornem false com a resultat
        if(password.isEmpty() || password.length()<4 || password.length()>12){
            password_login.setError(_context.getResources().getString(R.string.error_invalid_password));
            valid = false;
        }else{
            password_login.setError(null);
        }
        return  valid;
    }

    /**
     * Metode per validar les dades introduides per l'usuari a l'hore de realitzar el registre d'un nou usuari
     * @param name_signup el nom de l'usuari introduit
     * @param email_signup el correu-e de l'usuari introduit
     * @param password_signup el password de l'usuari introduit
     * @param user_type_login el tipus d'usuari de l'usuari introduit
     * @return true en cas que els parametres compleixin els requisits establerts
     */
    public boolean validate_signup(EditText name_signup, EditText email_signup,
                                   EditText password_signup, AppCompatSpinner user_type_login){
        boolean valid = true;
        //Obtenim els valors dels exittext/appcompatspinner passats per parametre
        String name = name_signup.getText().toString();
        String email = email_signup.getText().toString();
        String password = password_signup.getText().toString();
        String text = user_type_login.getSelectedItem().toString();
        //Comprobem que el camp name no esta buit i que la extensio es superior a 3 caracters
        //En cas contrari definim l'error per al camp i tornem false com a resultat
        if (name.isEmpty() || name.length()<3){
            name_signup.setError(_context.getResources().getString(R.string.error_invalid_name));
            valid = false;
        }else{
            name_signup.setError(null);
        }
        //Comprobem que el camp email no esta buit i que el format es correspon amb el de un correu.e
        //En cas contrari definim l'error per al camp i tornem false com a resultat
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_signup.setError(_context.getResources().getString(R.string.error_invalid_email));
            valid = false;
        }else{
            email_signup.setError(null);
        }
        //Comprobem que el camp password no esta buit i que la extensio es superior a 4 caracters i inferior a 12
        //En cas contrari definim l'error per al camp i tornem false com a resultat
        if(password.isEmpty() || password.length()<4 || password.length()>10){
            password_signup.setError(_context.getResources().getString(R.string.error_invalid_password));
            valid = false;
        }else{
            email_signup.setError(null);
        }
        //Comprobem que s'hagui seleccionat un valor per al tipus de usuari al appcompatspinner
        //En cas contrari definim el missatge d'error per al edittext corresponent al valor a evaluar
        if(text.isEmpty()){
            TextView errorText = (TextView)user_type_login.getSelectedView();
            errorText.setError(_context.getResources().getString(R.string.error_field_required));
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(_context.getResources().getString(R.string.error_field_required));
            valid = false;
        }
        return  valid;
    }

    /**
     * Metode per solicitar permis per accedir a l'emmagatzenament intern del telefon en temps d'execuccio
     */
    public void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale((Activity)_context,
                Manifest.permission.READ_EXTERNAL_STORAGE) ){
        }else{
            ActivityCompat.requestPermissions((Activity)_context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

    /**
     * Metode que defineix l'acces a la galeria de fotos del telefon i filtra el tipus de documents que volem veure
     */
    public void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpg", "image/png", "image/jpeg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        ((Activity)_context).startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    /**
     * Metode per escalar la imatge seleccionada, de forma que sigui visible de forma correcta al espai designat per aquesta
     * @param imageAbsoluthePath la ruta a on esta el fitxer seleccionat pe l'usuari
     * @return la imatge seleccionada en format bitmap per a poder emmagatzemarla a la base de dades el un camp blob
     */
    public static Bitmap setScaledImageBitmap(String imageAbsoluthePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageAbsoluthePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 512, 512);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imageAbsoluthePath, options);
    }

    /**
     * Metode per calcular les noves dimensions de la imatge
     * @param options
     * @param reqWidth l'amplaria desitjada
     * @param reqHeight l'alçada desitgada
     * @return les noves dimensions per a la imatge en funcio dels parametres establerts
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Metode per a genera una contrasenya segura amb el algoritme PBKF2
     * @param password el password que volem encriptar
     * @return la contrasenya encriptada en format string
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public String generateStorngPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException{

        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    /**
     * Metode per obtenir el salt que els servira per a encriptar la contrasenya amb major grau de seguretat
     * @return el salt
     * @throws NoSuchAlgorithmException
     */
    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    /**
     * Metode per transformar un array de bits en string
     * @param array l''array de bits que volem transformar
     * @return un string que es correspon a l'array de byts passats per parametre
     * @throws NoSuchAlgorithmException
     */
    private static String toHex(byte[] array) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }

    /**
     * Amb aquest metode validem que el password sense encriptar introduit per l'usuari, es correspon amb el password
     * encriptat que tenim a la base de dades
     * @param originalPassword password sense encriptar introduit per l'usuari
     * @param storedPassword password encriptat que esta emmagatzemat a la base de dades
     * @return un boolea amb valor true en cas de que coindiceixin
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);
        //Obtenim el arrau de bites per a la contrasenya d'string sense encriptar
        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        //Comrpboem si coincideix amb la contrasenya encriptada
        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    /**
     * Metode per transformar un string en un array de byts
     * @param hex l'string que volem transformar
     * @return el array de byts obtingut despres de la transformacio
     * @throws NoSuchAlgorithmException
     */
    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
}
