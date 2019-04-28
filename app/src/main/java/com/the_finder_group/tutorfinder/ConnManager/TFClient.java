package com.the_finder_group.tutorfinder.ConnManager;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * @author José Luis Puentes Jiménez <jlpuentes74@gmail.com>
 */
public interface TFClient {
    /**
     * Demana per fer login al servidor
     * @param userName Nom d'usuari al servidor
     * @return True si l'usuari és al server
     */
    public String login(String userName, Context context);

    /**
     * Demana les dades de l'usuari
     * @param userName Nom de l'usuari
     * @return Retorna el objecte amb les dades d'usuari
     */
    public UserDTO userData(String userName, Context context);

    /**
     * Introdueix les dades de registre a la base de dades
     * @param userName nom de l'usuari
     * @param email    email de l'usuari
     * @param pswd     password de l'usuari
     * @param userType tipus d'usuari
     * @return True si em pogut emmagatzemar l'usuari a la base de dades
     */
    public boolean newUser(String userName, String email, String pswd, String userType, Context context);

    /**
     *
     * @param user_id
     * @param userName
     * @param email
     * @param userRole
     * @return
     */
    public boolean editUser(Integer user_id, String userName, String email, String userRole, Context context);

    /**
     * @param userName
     * @param password
     * @return
     */
    public boolean editUserPswd (String userName, String password, Context context);
    /**
     * Retorna un array amb tots els usuaris
     * @return
     */
    public ArrayList<UserDTO> listUsers(Context context);

    /**
     *
     * @param userName
     * @return
     */
    public boolean delUser(String userName, Context context);

    /**
     * @param ad_user_id
     * @param ad_title
     * @param ad_description
     * @param ad_type
     * @param ad_price
     * @return true o false depenent si s'ha pugt guardar o no a la base de dades
     */
    public boolean createAd(Integer ad_user_id, String ad_title, String ad_description, Integer ad_type, Integer ad_price, Context context);

    /**
     * @param user_role_id el tipus de usuaris que volem llistar
     * @return
     */
    public List<AdDTO> listProductsRole(Integer user_role_id, Context context);

    /**
     * @param user_role_id el tipus de usuaris que volem llistar
     * @return
     */
    public List<AdDTO> listProductsAdmin(Integer user_role_id, Context context);

    /**
     * @param user_id el tipus de usuaris que volem llistar
     * @return
     */
    public List<AdDTO> listProductsUser(Integer user_id, Context context);

    /**
     * @param productId l'id del producte que volem esborrar
     * @return
     */
    public boolean delAd(Integer productId, Context context);

    /**
     *
     * @param ad_id
     * @param ad_title
     * @param ad_description
     * @param ad_price
     * @param ad_type_id
     * @return
     */
    public boolean editAd (Integer ad_id, String ad_title, String ad_description, Integer ad_type_id, Integer ad_price, Context context);

    /**
     *
     * @param ad_id
     * @param ad_user_booking_id
     * @return
     */
    public boolean bookAd (Integer ad_id, Integer ad_user_booking_id, Context context);

    /**
     *
     * @param user_id
     * @return
     */
    public List<AdDTO> listBookAdsUser (Integer user_id, Context context);

    /**
     *
     * @param user_id
     * @return
     */
    public List<AdDTO> listBookdAdsOther (Integer user_id, Context context);

    /**
     *
     * @param ad_id
     * @return
     */
    public boolean cancelBookAd (Integer ad_id, Context context);

    //TODO: documentar getAdTypeByName
    public int getAdTypeByName(String adTypeName, Context context);

    /**
     *
     * @param sender_id
     * @param sender_name
     * @param message
     * @param date
     * @return
     */
    public boolean createMessage (Integer sender_id, String sender_name, String message, String date, Context context);

    /**
     *
     * @param user_id
     * @return
     */
    public List<UserMessageDTO> listMessagesByUser (Integer user_id, Context context);

}