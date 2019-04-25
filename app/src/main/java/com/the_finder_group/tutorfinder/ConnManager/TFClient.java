package com.the_finder_group.tutorfinder.ConnManager;

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
    public String login(String userName);

    /**
     * Demana les dades de l'usuari
     * @param userName Nom de l'usuari
     * @return Retorna el objecte amb les dades d'usuari
     */
    public UserDTO userData(String userName);

    /**
     * Introdueix les dades de registre a la base de dades
     * @param userName nom de l'usuari
     * @param email    email de l'usuari
     * @param pswd     password de l'usuari
     * @param userType tipus d'usuari
     * @return True si em pogut emmagatzemar l'usuari a la base de dades
     */
    public boolean newUser(String userName, String email, String pswd, String userType);

    /**
     *
     * @param user_id
     * @param userName
     * @param email
     * @param userRole
     * @return
     */
    public boolean editUser(Integer user_id, String userName, String email, String userRole);

    /**
     * @param userName
     * @param password
     * @return
     */
    public boolean editUserPswd (String userName, String password);
    /**
     * Retorna un array amb tots els usuaris
     * @return
     */
    public ArrayList<UserDTO> listUsers();

    /**
     *
     * @param userName
     * @return
     */
    public boolean delUser(String userName);

    /**
     * @param ad_user_id
     * @param ad_title
     * @param ad_description
     * @param ad_type
     * @param ad_price
     * @return true o false depenent si s'ha pugt guardar o no a la base de dades
     */
    public boolean createAd(Integer ad_user_id, String ad_title, String ad_description, Integer ad_type, Integer ad_price);

    /**
     * @param user_role_id el tipus de usuaris que volem llistar
     * @return
     */
    public List<AdDTO> listProductsRole(Integer user_role_id);

    /**
     * @param user_id el tipus de usuaris que volem llistar
     * @return
     */
    public List<AdDTO> listProductsUser(Integer user_id);

    /**
     * @param productId l'id del producte que volem esborrar
     * @return
     */
    public boolean delAd(Integer productId);

    /**
     *
     * @param ad_id
     * @param ad_title
     * @param ad_description
     * @param ad_price
     * @param ad_type_id
     * @return
     */
    public boolean editAd (Integer ad_id, String ad_title, String ad_description, Integer ad_type_id, Integer ad_price);

    /**
     *
     * @param ad_id
     * @param ad_user_booking_id
     * @return
     */
    public boolean bookAd (Integer ad_id, Integer ad_user_booking_id);

    /**
     *
     * @param user_id
     * @return
     */
    public List<AdDTO> listBookAdsUser (Integer user_id);

    /**
     *
     * @param user_id
     * @return
     */
    public List<AdDTO> listBookdAdsOther (Integer user_id);

    /**
     *
     * @param ad_id
     * @return
     */
    public boolean cancelBookAd (Integer ad_id);

    //TODO: documentar getAdTypeByName
    public int getAdTypeByName(String adTypeName);

}