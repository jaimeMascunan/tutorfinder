package com.the_finder_group.tutorfinder.ConnManager;

import java.util.ArrayList;
import java.util.List;

/**
 *  Clase que implemetarà el client per demanar els serveis del
 *  servidor
 *
 * @author José Luis Puentes Jiménez <jlpuentes74@gmail.com>
 */
public class TFClientImple implements TFClient{

    @Override
    public String login(String userName) {

        return ServiceLocator.login(userName);

    }

    @Override
    public UserDTO userData(String userName) {

        return ServiceLocator.userData(userName);

    }

    @Override
    public boolean newUser(String userName, String email, String pswd, String userType){

        return ServiceLocator.newUser(userName, email, pswd, userType);
    }

    @Override
    public boolean editUser( Integer user_id, String userName, String email, String userRole){

        return ServiceLocator.editUser(user_id, userName, email, userRole);
    }

    @Override
    public boolean editUserPswd (String userName, String password){

        return ServiceLocator.editUserPswd(userName, password);
    }

    @Override
    public ArrayList<UserDTO> listUsers(){

        return ServiceLocator.listUsers();
    }

    @Override
    public boolean delUser (String userName){

        return ServiceLocator.deleteUser(userName);
    }

    @Override
    public boolean createAd(Integer ad_user_id, String ad_title, String ad_description, Integer ad_type, Integer ad_price){

        return ServiceLocator.createAd(ad_user_id, ad_title, ad_description, ad_type, ad_price);
    }

    @Override
    public List<AdDTO> listProductsRole(Integer user_role_id){

        return ServiceLocator.listProductsRole(user_role_id);
    }

    @Override
    public List<AdDTO> listProductsUser(Integer user_id){

        return ServiceLocator.listProductsUser(user_id);
    }

    @Override
    public boolean delAd (Integer productId){

        return ServiceLocator.delAd(productId);
    }

    @Override
    public boolean editAd (Integer ad_id, String ad_title, String ad_description, Integer ad_type_id, Integer ad_price){

        return ServiceLocator.editAd(ad_id, ad_title, ad_description, ad_type_id, ad_price);
    }

    @Override
    public boolean bookAd (Integer ad_id, Integer ad_user_booking_id){

        return ServiceLocator.adsBookByUser(ad_id, ad_user_booking_id);
    }

    @Override
    public List<AdDTO> listBookAdsUser (Integer user_id){

        return ServiceLocator.listProductsBookedByUser(user_id);
    }

    @Override
    public List<AdDTO> listBookdAdsOther (Integer user_id){

        return ServiceLocator.listProductsBookedByOther(user_id);
    }

    @Override
    public boolean cancelBookAd (Integer ad_id){

        return ServiceLocator.cancelBookingProductByUser(ad_id);
    }

    @Override
    public int getAdTypeByName(String adTypeName) {

        return ServiceLocator.getAdTypeByName(adTypeName);
    }


}



