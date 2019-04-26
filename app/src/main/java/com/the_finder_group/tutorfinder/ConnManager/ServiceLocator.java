package com.the_finder_group.tutorfinder.ConnManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author José Luis Puentes Jiménez <jlpuentes74@gmail.com>
 */
public class ServiceLocator {

    private static final int LOGIN = 0;
    private static final int USER_DATA = 1;
    private static final int REGISTER = 2;
    private static final int EDIT_USER = 3;
    private static final int LIST_USERS = 5;
    private static final int DELETE_USER = 4;
    private static final int PUBLISH_AD = 8;
    private static final int LIST_PRODUCTS_ROLE = 11;
    private static final int LIST_PRODUCTS_USER = 10;
    private static final int DELETE_PRODUCT = 14;
    private static final int EDIT_USER_PASSWORD = 6;
    private static final int EDIT_PRODUCT = 13;
    private static final int BOOKING_PRODUCT = 12;
    private static final int LIST_PRODUCTS_BOOKED_USER = 7;
    private static final int LIST_PRODUCTS_BOOKED_OTHER = 18;
    private static final int CANCEL_BOOKING_PRODUCT = 9;
    private static final int GET_AD_TYPE_BY_NAME = 17;

    private static final String LOGIN_CODE = "loginString";

    // Dades de configuració del servidor
    private static final String serverIp = "192.168.1.3";
    private static final int port = 7474;


    public static String login(String userName) {

        String storedPassword = null;

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el login al servidor
            dos.writeInt(LOGIN);
            dos.writeUTF(LOGIN_CODE);
            dos.writeUTF(userName);
            dos.flush();

            // Llegim la resposta
            storedPassword = dis.readUTF();

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return storedPassword;
    }

    public static UserDTO userData(String userName) {

        UserDTO user = new UserDTO();

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos =null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem les dades de l'usuari al servidor
            dos.writeInt(USER_DATA);
            dos.writeUTF(userName);
            dos.flush();

            // Llegim la resposta i creem la resposta
            user.setUserId(dis.readInt());
            user.setUserName(dis.readUTF());
            user.setUserMail(dis.readUTF());
            user.setUserPswd(dis.readUTF());
            user.setUserRol(dis.readUTF());


        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
            }
        }

        return user;
    }

    public static boolean newUser(String userName, String email, String password, String userType ) {

        boolean ret = false;

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el login al servidor
            dos.writeInt(REGISTER);
            dos.writeUTF(userName);
            dos.writeUTF(email);
            dos.writeUTF(password);
            dos.writeUTF(userType);
            dos.flush();

            // Llegim la resposta
            ret = dis.readBoolean();

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    public static boolean editUserPswd (String userName, String password){
        boolean ret = false;

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el login al servidor
            dos.writeInt(EDIT_USER_PASSWORD);
            dos.writeUTF(userName);
            dos.writeUTF(password);
            dos.flush();

            // Llegim la resposta
            ret = dis.readBoolean();

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    public static boolean editUser(Integer user_id, String userName, String email, String userRole ) {

        boolean ret = false;

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el login al servidor
            dos.writeInt(EDIT_USER);
            dos.writeInt(user_id);
            dos.writeUTF(userName);
            dos.writeUTF(email);
            dos.writeUTF(userRole);
            dos.flush();

            // Llegim la resposta
            ret = dis.readBoolean();

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    public static ArrayList<UserDTO> listUsers(){

        ArrayList<UserDTO> listUsers= new ArrayList<UserDTO>();

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el login al servidor
            dos.writeInt(LIST_USERS);
            dos.writeUTF("listUsers");
            dos.flush();

            // Llegim la resposta
            //Revem el nombre d'ususaris que hi haurà de resposta
            int nUsers = dis.readInt();

            while(dis.available()>0) {

                for (int i = 0; i < nUsers; i++) {
                    //Rebem los dades del servidor i construï un UserDTO
                    //i el fiquem l'ArrayList
                    UserDTO user = new UserDTO();
                    user.setUserId(dis.readInt());
                    user.setUserName(dis.readUTF());
                    user.setUserMail(dis.readUTF());
                    user.setUserPswd(dis.readUTF());
                    user.setUserRol(dis.readUTF());

                    listUsers.add(user);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return listUsers;

    }

    public static boolean deleteUser(String userName){

        boolean ret = false;

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el enviar el nou usuari al servidor
            dos.writeInt(DELETE_USER);
            //Enviem les dades del nou usari
            dos.writeUTF(userName);
            dos.flush();

            // Llegim la resposta
            ret = dis.readBoolean();


        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return ret;
    }

    public static boolean createAd(Integer ad_user_id, String ad_title, String ad_description, Integer ad_type, Integer ad_price) {

        boolean ret = false;

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el login al servidor
            dos.writeInt(PUBLISH_AD);
            dos.writeInt(ad_user_id);
            dos.writeUTF(ad_title);
            dos.writeUTF(ad_description);
            dos.writeInt(ad_type);
            dos.writeInt(ad_price);
            dos.flush();

            // Llegim la resposta
            ret = dis.readBoolean();

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    public static List<AdDTO> listProductsRole(Integer user_role_id){

        List<AdDTO> listProducts = new ArrayList<AdDTO>();

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el login al servidor
            dos.writeInt(LIST_PRODUCTS_ROLE);
            //Enviem el tipus d'usuari que volem llistar
            dos.writeInt(user_role_id);
            dos.flush();

            // Llegim la resposta
            //Revem el nombre d'ususaris que hi haurà de resposta
            int nProducts = dis.readInt();

            while(dis.available()>0) {

                for (int i = 0; i < nProducts; i++) {
                    //Rebem los dades del servidor i construï un UserDTO
                    //i el fiquem l'ArrayList
                    AdDTO ad = new AdDTO();
                    ad.setAdId(dis.readInt());
                    ad.setAdUserId(dis.readInt());
                    ad.setUserName(dis.readUTF());
                    ad.setAdTittle(dis.readUTF());
                    ad.setAdDescription(dis.readUTF());
                    ad.setAdTypeId(dis.readInt());
                    ad.setTypesName(dis.readUTF());
                    ad.setAdPrice(dis.readInt());

                    listProducts.add(ad);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return listProducts;

    }

    public static List<AdDTO> listProductsUser(Integer user_id){

        List<AdDTO> listProducts = new ArrayList<AdDTO>();

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el login al servidor
            dos.writeInt(LIST_PRODUCTS_USER);
            //Enviem el tipus d'usuari que volem llistar
            dos.writeInt(user_id);
            dos.flush();

            // Llegim la resposta
            //Revem el nombre d'ususaris que hi haurà de resposta
            int nProducts = dis.readInt();

            while(dis.available()>0) {

                for (int i = 0; i < nProducts; i++) {
                    //Rebem los dades del servidor i construï un UserDTO
                    //i el fiquem l'ArrayList
                    AdDTO ad = new AdDTO();
                    ad.setAdId(dis.readInt());
                    ad.setAdUserId(dis.readInt());
                    ad.setUserName(dis.readUTF());
                    ad.setAdTittle(dis.readUTF());
                    ad.setAdDescription(dis.readUTF());
                    ad.setAdTypeId(dis.readInt());
                    ad.setTypesName(dis.readUTF());
                    ad.setAdPrice(dis.readInt());

                    listProducts.add(ad);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return listProducts;

    }

    public static List<AdDTO> listProductsBookedByUser(Integer user_id){

        List<AdDTO> listProducts = new ArrayList<AdDTO>();

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el login al servidor
            dos.writeInt(LIST_PRODUCTS_BOOKED_USER);
            //Enviem el tipus d'usuari que volem llistar
            dos.writeInt(user_id);
            dos.flush();

            // Llegim la resposta
            //Revem el nombre d'ususaris que hi haurà de resposta
            int nProducts = dis.readInt();

            while(dis.available()>0) {

                for (int i = 0; i < nProducts; i++) {
                    //Rebem los dades del servidor i construï un UserDTO
                    //i el fiquem l'ArrayList
                    AdDTO product = new AdDTO();

                    product.setAdId(dis.readInt());
                    product.setAdUserId(dis.readInt());
                    product.setAdTittle(dis.readUTF());
                    product.setAdDescription(dis.readUTF());
                    product.setAdTypeId(dis.readInt());
                    product.setTypesName(dis.readUTF());
                    product.setAdPrice(dis.readInt());

                    listProducts.add(product);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return listProducts;

    }

    public static List<AdDTO> listProductsBookedByOther(Integer user_id){

        List<AdDTO> listProducts = new ArrayList<AdDTO>();

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el login al servidor
            dos.writeInt(LIST_PRODUCTS_BOOKED_OTHER);
            //Enviem el tipus d'usuari que volem llistar
            dos.writeInt(user_id);
            dos.flush();

            // Llegim la resposta
            //Revem el nombre d'ususaris que hi haurà de resposta
            int nProducts = dis.readInt();

            while(dis.available()>0) {

                for (int i = 0; i < nProducts; i++) {
                    //Rebem los dades del servidor i construï un UserDTO
                    //i el fiquem l'ArrayList
                    AdDTO product = new AdDTO();

                    product.setAdId(dis.readInt());
                    product.setAdUserId(dis.readInt());
                    product.setAdTittle(dis.readUTF());
                    product.setAdDescription(dis.readUTF());
                    product.setAdTypeId(dis.readInt());
                    product.setTypesName(dis.readUTF());
                    product.setAdPrice(dis.readInt());

                    listProducts.add(product);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return listProducts;

    }

    public static boolean delAd(Integer productId){

        boolean ret = false;

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el enviar el nou usuari al servidor
            dos.writeInt(DELETE_PRODUCT);
            //Enviem les dades del nou usari
            dos.writeInt(productId);
            dos.flush();

            // Llegim la resposta
            ret = dis.readBoolean();


        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return ret;
    }

    public static boolean editAd(Integer ad_id, String ad_title, String ad_description, Integer ad_type_id, Integer ad_price) {

        boolean ret = false;

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el login al servidor
            dos.writeInt(EDIT_PRODUCT);
            dos.writeInt(ad_id);
            dos.writeUTF(ad_title);
            dos.writeUTF(ad_description);
            dos.writeInt(ad_type_id);
            dos.writeInt(ad_price);
            dos.flush();

            // Llegim la resposta
            ret = dis.readBoolean();

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    public static boolean adsBookByUser(Integer ad_id, Integer ad_user_booking_id) {

        boolean ret = false;

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el login al servidor
            dos.writeInt(BOOKING_PRODUCT);
            dos.writeInt(ad_id);
            dos.writeInt(ad_user_booking_id);

            dos.flush();

            // Llegim la resposta
            ret = dis.readBoolean();

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    public static boolean cancelBookingProductByUser(Integer ad_id) {

        boolean ret = false;

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el login al servidor
            dos.writeInt(CANCEL_BOOKING_PRODUCT);
            dos.writeInt(ad_id);

            dos.flush();

            // Llegim la resposta
            ret = dis.readBoolean();

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    //TODO: documentar getAdTypeById ServiceLocator
    public static Integer getAdTypeByName(String adTypeName){

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        Integer ret = -1;

        try {

            // Instanciem el Socket i els Input i Output
            // per comunicar amb el server
            s = new Socket(serverIp, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // Solicitem el nom del tipus pel seu id
            dos.writeInt(GET_AD_TYPE_BY_NAME);

            dos.writeUTF(adTypeName);


            //Revem la resposta
            ret = dis.readInt();

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);

        } finally {

            try {
                // Tanquem connexions
                if (dis != null) { dis.close();}
                if (dos != null) { dos.close();}
                if (s != null) { s.close();}

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return ret;
    }

}