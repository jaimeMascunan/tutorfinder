package com.the_finder_group.tutorfinder.Helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;

import com.the_finder_group.tutorfinder.AdminActivity;
import com.the_finder_group.tutorfinder.ConnManager.UserMessageDTO;
import com.the_finder_group.tutorfinder.LoginActivity;
import com.the_finder_group.tutorfinder.R;
import com.the_finder_group.tutorfinder.StudentActivity;
import com.the_finder_group.tutorfinder.TutorActivity;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Comparator;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Helper {

    Context _context;
    private SQLiteHandler db;
    private SessionManager session;
    private int STORAGE_PERMISSION_CODE= 1111;
    private int GALLERY_REQUEST_CODE = 1112;

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

    //Metode per validar les dades introduides per l'usuari en funcio dels parametres establerts
    public boolean validate_ad(EditText ad_titol, EditText ad_descripcio,
                               EditText ad_preu, AppCompatSpinner ad_type){
        boolean valid = true;

        String titol = ad_titol.getText().toString().trim();
        String descripcio = ad_descripcio.getText().toString().trim();
        String preu = ad_preu.getText().toString().trim();
        String categoria = ad_type.getSelectedItem().toString();


        if (titol.isEmpty() || titol.length()<3){
            ad_titol.setError("Has de itroduir 3 caracters com a minim al titol");
            valid = false;
        }else{
            ad_titol.setError(null);
        }

        if (descripcio.isEmpty() || descripcio.length()<10){
            ad_descripcio.setError("La descripcio no es valida");
            valid = false;
        }else{
            ad_descripcio.setError(null);
        }

        if(preu.isEmpty()){
            ad_preu.setError("EL format ha de ser numeric");
            valid = false;
        }else{
            ad_preu.setError(null);
        }

        if(categoria.isEmpty()){
            TextView errorText = (TextView)ad_type.getSelectedView();
            errorText.setError(_context.getResources().getString(R.string.error_field_required));
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(_context.getResources().getString(R.string.error_field_required));
            valid = false;
        }

        return  valid;
    }

    public int validate_edit_password(String pswrd_old, String pswrd_old_confirmatio, String pswrd_new, String db_password) {
        int valid = 1000;
        try {
            if (((pswrd_old != null) && !(pswrd_old.isEmpty()) && (validatePassword(pswrd_old, db_password)))
                    && ((pswrd_old_confirmatio != null) && !(pswrd_old_confirmatio.isEmpty()) &&
                    (validatePassword(pswrd_old_confirmatio, db_password)))) {

                if ((pswrd_new != null) && !(pswrd_new.isEmpty()) && (pswrd_new.length()>=4) && (pswrd_new.length()<=12)) {
                    valid = -1;
                } else {
                    valid = 1;
                }
            } else {
                valid = 0;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return valid;
    }

    //Metode per validar les dades introduides per l'usuari en funcio dels parametres establerts
    public boolean validate_edit_user(TextView name_edit, TextView email_edit){
        boolean valid = true;

        String name  = name_edit.getText().toString();
        String email = email_edit.getText().toString();

        if(name.isEmpty() || name.length()<3){
            name_edit.setError(_context.getResources().getString(R.string.error_invalid_name));
            valid = false;
        }else{
            name_edit.setError(null);
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_edit.setError(_context.getResources().getString(R.string.error_invalid_email));
            valid = false;
        }else{
            email_edit.setError(null);
        }

        return  valid;
    }

    //Metode per validar les dades introduides per l'usuari en funcio dels parametres establerts
    public boolean validate_login(EditText name_login, EditText password_login){
        boolean valid = true;

        String name = name_login.getText().toString();
        String password = password_login.getText().toString();

        if(name.isEmpty() || name.length()<3){
            name_login.setError(_context.getResources().getString(R.string.error_invalid_name));
            valid = false;
        }else{
            name_login.setError(null);
        }

        if(password.isEmpty() || password.length()<4 || password.length()>12){
            password_login.setError(_context.getResources().getString(R.string.error_invalid_password));
            valid = false;
        }else{
            password_login.setError(null);
        }

        return  valid;
    }

    //Metode per validar les dades introduides per l'usuari en funcio dels parametres establerts
    public boolean validate_signup(EditText name_signup, EditText email_signup,
                                   EditText password_signup, AppCompatSpinner user_type_login){
        boolean valid = true;

        String name = name_signup.getText().toString();
        String email = email_signup.getText().toString();
        String password = password_signup.getText().toString();
        String text = user_type_login.getSelectedItem().toString();


        if (name.isEmpty() || name.length()<3){
            name_signup.setError(_context.getResources().getString(R.string.error_invalid_name));
            valid = false;
        }else{
            name_signup.setError(null);
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_signup.setError(_context.getResources().getString(R.string.error_invalid_email));
            valid = false;
        }else{
            email_signup.setError(null);
        }

        if(password.isEmpty() || password.length()<4 || password.length()>10){
            password_signup.setError(_context.getResources().getString(R.string.error_invalid_password));
            valid = false;
        }else{
            email_signup.setError(null);
        }

        if(text.isEmpty()){
            TextView errorText = (TextView)user_type_login.getSelectedView();
            errorText.setError(_context.getResources().getString(R.string.error_field_required));
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(_context.getResources().getString(R.string.error_field_required));
            valid = false;
        }

        return  valid;
    }

    public void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale((Activity)_context,
                Manifest.permission.READ_EXTERNAL_STORAGE) ){
        }else{
            ActivityCompat.requestPermissions((Activity)_context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

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

    public static Bitmap setScaledImageBitmap(String imageAbsoluthePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageAbsoluthePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 512, 512);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imageAbsoluthePath, options);

    }

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


    public String generateStorngPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException{

        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

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

    public boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
}
