package com.the_finder_group.tutorfinder.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Clase que ens servira per guardad dades d'usuaris en local
 * Aixi estalviem tenir que estar constantment realitzant crides al servidor
 */
public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "tutor_finder";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PSWRD = "password";
    private static final String KEY_USER_TYPE = "user_type";

    // Image table name
    private static final String TABLE_IMAGE = "images";

    //Image Table Columns names
    private static final String KEY_ID_IMAGE = "id";
    private static final String KEY_IMAGE = "name";
    private static final String USER_ID_FK = "user_id";

    // Popup options menu
    private static final String TABLE_POPUP_OPTIONS = "popup_options";

    //Pop up options Colum names
    private static final String KEY_POPUP_OPTION = "popup_menu_option";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creacio de les taules
     * @param db la base de dades
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT,"  + KEY_PSWRD + " TEXT,"
                + KEY_USER_TYPE + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_IMAGE_TABLE = "CREATE TABLE " + TABLE_IMAGE + "("
                + KEY_ID_IMAGE + " INTEGER PRIMARY KEY," + KEY_IMAGE + " BLOB NOT NULL,"
                + USER_ID_FK + " INTEGER," + "FOREIGN KEY " + "(" +
                USER_ID_FK + ")" + "REFERENCES " + TABLE_USER + "(" + KEY_ID + ")" + ")";
        db.execSQL(CREATE_IMAGE_TABLE);

        String CREATE_POPUP_OPTIONS_TABLE = "CREATE TABLE " + TABLE_POPUP_OPTIONS + "("
                + KEY_POPUP_OPTION + " INTEGER PRIMARY KEY" + ")";
        db.execSQL(CREATE_POPUP_OPTIONS_TABLE);

        Log.d(TAG, "Database tables created");


    }

    /**
     * Modificacio de la base de dades
     * @param db base de dades
     * @param oldVersion la versio a esborrar
     * @param newVersion la nova versio
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POPUP_OPTIONS);

        // Create tables again
        onCreate(db);
    }

    // Adding new image
    public void addImageBitmap(Integer user_id, Bitmap imageBmp) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        byte[] buffer = out.toByteArray();

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IMAGE, buffer); // Image path
            values.put(USER_ID_FK, user_id);//User id
            // Inserting Row
            db.insert(TABLE_IMAGE, null, values);
            db.setTransactionSuccessful();

        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
    }

    // Adding new image
    public void updateImageBitmap(Integer user_id, Bitmap imageBmp) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        byte[] buffer = out.toByteArray();

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IMAGE, buffer); // Image path
            values.put(USER_ID_FK, user_id);//User id


            String where = USER_ID_FK + " = ?";
            final String whereArgs[] = new String[] {String.valueOf(user_id)};

            // Inserting Row
            long rows_affected = db.update(TABLE_IMAGE,  values, where, whereArgs);
            db.setTransactionSuccessful();

            Log.d(TAG, "User pic edited on sqlite database. Rows affected: " + rows_affected);
        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
    }

    // Getting single image
    public Bitmap getImagePath(int id) {
        Bitmap bitmap = null;

        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        try {
            String selectQuery = "SELECT * FROM " + TABLE_IMAGE + " WHERE " + USER_ID_FK + "=" + id;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    byte[] blob = cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE));
                    bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                }

            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
        return bitmap;
    }

    /**
     * Guardem les dades d'usuari/a a la base de dades
     * @param name el nom introduit
     * @param email email introduit
     * @param password pswrod introudi
     * @param user_type el tipus d'usuari
     */
    public void addUser(Integer user_id, String name, String email, String password, String user_type) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, user_id); //ID
            values.put(KEY_NAME, name); // Name
            values.put(KEY_EMAIL, email); // Email
            values.put(KEY_PSWRD, password); //Password
            values.put(KEY_USER_TYPE, user_type); //User_type

            // Inserting Row
            long id = db.insert(TABLE_USER, null, values);
            db.setTransactionSuccessful();
            Log.d(TAG, "New user inserted into sqlite: " + id + " " + user_id);
        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
    }

    /**
     * Guardem les dades d'usuari/a a la base de dades
     * @param name el nom introduit
     * @param email email introduit
     */
    public void editUser(Integer user_id, String name, String email, String user_type) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, user_id); //ID
            values.put(KEY_NAME, name); // Name
            values.put(KEY_EMAIL, email); // Email
            values.put(KEY_USER_TYPE, user_type); //UserType

            String where = KEY_ID + " = ?";
            final String whereArgs[] = new String[] {String.valueOf(user_id)};

            // Inserting Row
            long rows_affected = db.update(TABLE_USER,  values, where, whereArgs);
            db.setTransactionSuccessful();
            Log.d(TAG, "User edited on sqlite database. Rows affected: " + rows_affected);
        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
    }

    /**
     *
     * @param userName
     * @param password
     */
    public void editUserPassword(String userName, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_PSWRD, password); // Password

            String where = KEY_NAME + " = ?";
            final String whereArgs[] = new String[] {userName};

            // Inserting Row
            long rows_affected = db.update(TABLE_USER,  values, where, whereArgs);
            db.setTransactionSuccessful();
            Log.d(TAG, "User edited on sqlite database. Rows affected: " + rows_affected);
        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
    }

    /**
     * Obtenim un usuari/a de la base de dades que es guarda a un HashMap de clau i valor
     * @return un objecte user
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            // Move to first row
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                user.put("user_id", String.valueOf(cursor.getInt(0)));
                user.put("name", cursor.getString(1));
                user.put("email",cursor.getString(2));
                user.put("password", cursor.getString(3));
                user.put("user_type",cursor.getString(4));
            }
            cursor.close();
            // return user
            db.setTransactionSuccessful();
            Log.d(TAG, "Fetching user from Sqlite: " + user.toString());
        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
        return user;
    }

    // Adding new image
    public void addPopUpOption(Integer option) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_POPUP_OPTION, option); //Option popup

            // Inserting Row
            db.insert(TABLE_POPUP_OPTIONS, null, values);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
    }

    // Getting single image
    public Integer getOptionPopUP() {
        Integer popupOption = 0;
        String selectQuery = "SELECT * FROM " + TABLE_POPUP_OPTIONS;

        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            // Move to first row
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                popupOption = cursor.getInt(0);
            }
            cursor.close();
            // return user
            db.setTransactionSuccessful();
            Log.d(TAG, "Fetching image from Sqlite: " + popupOption.toString());
        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
        return popupOption;
    }

    // Adding new image
    public void updateMenuPoputOption(Integer previousOption, Integer newOption) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_POPUP_OPTION, newOption);

            String where = KEY_POPUP_OPTION + " = ?";
            final String whereArgs[] = new String[] {String.valueOf(previousOption)};

            // Inserting Row
            long rows_affected = db.update(TABLE_POPUP_OPTIONS,  values, where, whereArgs);
            db.setTransactionSuccessful();
            Log.d(TAG, "User pic edited on sqlite database. Rows affected: " + rows_affected);
        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
    }

    /**
     * Esborrem tots els usuaris de la base de dades
     * */
    public void deleteDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            // Delete All Rows
            db.delete(TABLE_USER, null, null);
            db.delete(TABLE_POPUP_OPTIONS, null, null);
            db.setTransactionSuccessful();
            Log.d(TAG, "Deleted all user info from sqlite");
        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
    }

}
