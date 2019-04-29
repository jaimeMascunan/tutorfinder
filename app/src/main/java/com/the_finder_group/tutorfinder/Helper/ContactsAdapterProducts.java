package com.the_finder_group.tutorfinder.Helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.the_finder_group.tutorfinder.ConnManager.AdDTO;
import com.the_finder_group.tutorfinder.ConnManager.TFClientImple;
import com.the_finder_group.tutorfinder.MessageListActivity;
import com.the_finder_group.tutorfinder.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ContactsAdapterProducts extends RecyclerView.Adapter<ContactsAdapterProducts.MyViewHolder> implements Filterable {

    private static final int POPUP_MENU_OWN_COURSES = 1;
    private static final int POPUP_MENU_OTHER_COURSES = 2;
    private static final int POPUP_MENU_BOOKED_COURSES = 3;

    private Context context;
    private List<AdDTO> productList;
    private List<AdDTO> productListFiltered;
    private SQLiteHandler db;
    private TFClientImple tfClientImple;
    private AlertDialog.Builder deleteDialog, registerDialog, confirmDialog, edit_ad_dialog, cancelRegDialog;
    private AdDTO productToRemove, productToUpdate;
    private TextView ad_titol_edit, ad_preu_edit, ad_descripcio_edit;
    private AppCompatSpinner ad_type_ad_edit;
    private Integer product_id, ad_user_id, ad_user_booking_id, db_popup_option, db_user_id;
    private String ad_user_name;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView ad_title, ad_description, ad_price;
        public ImageView thumbnail, listItemOptions;

        public MyViewHolder(View view) {
            super(view);
            ad_title = (TextView)view.findViewById(R.id.product_title);
            ad_description = (TextView)view.findViewById(R.id.product_description);
            ad_price = (TextView)view.findViewById(R.id.product_price);
            thumbnail = (ImageView)view.findViewById(R.id.thumbnail);
            listItemOptions = (ImageView)view.findViewById(R.id.lisItemOptions);
        }
    }

    public ContactsAdapterProducts(Context context, List<AdDTO> productList) {
        this.context = context;
        this.productList = productList;
        this.productListFiltered = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //Menu amb les opciosn
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_row_product, viewGroup, false);

        //Instanciem el server
        tfClientImple = new TFClientImple();

        // SqLite database handler
        db = new SQLiteHandler(context);
        // Fetching user details from sqlite
        db_popup_option = db.getOptionPopUP();
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        //Id de l'usuari que esta realitzant lel registre
        db_user_id = Integer.parseInt(user.get("user_id"));


        //Alert dialog per la edicio del producte
        edit_ad_dialog = new AlertDialog.Builder(context);

        //Alert dialog
        deleteDialog = new AlertDialog.Builder(context);
        deleteDialog.setTitle("Atenció!");
        deleteDialog.setIcon(android.R.drawable.ic_input_delete);
        deleteDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new deleteProduct().execute(String.valueOf(product_id));
            }
        });
        deleteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        registerDialog = new AlertDialog.Builder(context);
        registerDialog.setTitle("Inscripcio a la clase");
        registerDialog.setIcon(android.R.drawable.ic_dialog_info);
        registerDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new registerProduct().execute(product_id, db_user_id);
            }
        });
        registerDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        cancelRegDialog = new AlertDialog.Builder(context);
        cancelRegDialog.setTitle("Cancelar inscripcio a la clase");
        cancelRegDialog.setIcon(android.R.drawable.ic_dialog_info);
        cancelRegDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new cancelRegisterProduct().execute(product_id);
            }
        });
        cancelRegDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        //Confirmar delete dialog
        confirmDialog = new AlertDialog.Builder(context);
        confirmDialog.setTitle("Confirmacio");
        confirmDialog.setIcon(android.R.drawable.ic_dialog_info);
        confirmDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final AdDTO adDTO = productListFiltered.get(position);
        holder.ad_title.setText(adDTO.getAdTittle());
        holder.ad_description.setText(adDTO.getAdDescription());
        holder.ad_price.setText(Integer.toString(adDTO.getAdPrice()));
        holder.thumbnail.setImageResource(R.drawable.ic_school_black_24dp);

        holder.listItemOptions.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // send selected contact in callback
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.listItemOptions);
                //inflating menu from xml resource
                if(db_popup_option == POPUP_MENU_OWN_COURSES){
                    userPopUpMenu(popup, adDTO, position);

                }else if (db_popup_option == POPUP_MENU_OTHER_COURSES){
                    rolePopupMenu(popup, adDTO, position);

                }else if (db_popup_option == POPUP_MENU_BOOKED_COURSES){
                    bookedPopupMenu(popup, adDTO, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() { return productListFiltered.size(); }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    productListFiltered = productList;
                } else {
                    List<AdDTO> filteredList = new ArrayList<>();
                    for (AdDTO row : productList) {

                        // name match condition. this might differ depending on your requirement
                        if (row.getAdTittle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    productListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productListFiltered;
                return filterResults;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productListFiltered = (ArrayList<AdDTO>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void userPopUpMenu(PopupMenu popup, final AdDTO adDTO, final Integer position){
        popup.inflate(R.menu.menu_search_owned_product);
        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_edit_product:
                    //Vista del canvi de contrasenya
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                    View productItemView = inflater.inflate(R.layout.edit_ad_dialog, null);
                    ad_titol_edit = (TextView)productItemView.findViewById(R.id.ad_title_edit);
                    ad_descripcio_edit = (TextView)productItemView.findViewById(R.id.ad_descripcio_edit);
                    ad_preu_edit = (TextView)productItemView.findViewById(R.id.ad_price_edit);
                    ad_type_ad_edit = (AppCompatSpinner)productItemView.findViewById(R.id.ad_type_spinner_edit);

                    //Adaptador de l'spinner amb els diferents tipus de tipus de cursos a publicar
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                            R.array.ad_typer_publish, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ad_type_ad_edit.setAdapter(adapter);


                    ad_titol_edit.setText(adDTO.getAdTittle());
                    ad_descripcio_edit.setText(adDTO.getAdDescription());
                    ad_preu_edit.setText(String.valueOf(adDTO.getAdPrice()));
                    ad_type_ad_edit.setSelection(adapter.getPosition(adDTO.getTypesName()));

                    if(productItemView.getParent() != null) {
                        ((ViewGroup)productItemView.getParent()).removeView(productItemView);
                    }
                    edit_ad_dialog.setView(productItemView);
                    edit_ad_dialog.setPositiveButton("Guardar canvis", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            product_id = adDTO.getAdId();
                            String titol = ad_titol_edit.getText().toString().trim();
                            String descripcio = ad_descripcio_edit.getText().toString().trim();
                            String adType = ad_type_ad_edit.getSelectedItem().toString();
                            String preu = ad_preu_edit.getText().toString().trim();
                            new getAdTypeByName().execute(adType, String.valueOf(product_id), titol, descripcio,
                                    preu, String.valueOf(position));
                        }
                    });

                    edit_ad_dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });

                    edit_ad_dialog.show();

                    break;
                case R.id.menu_delete_product:
                    product_id = adDTO.getAdId();
                    Log.d("product_id", String.valueOf(product_id));
                    deleteDialog.setMessage("Estas segur que vols esborrar la classe seleccionada?");
                    deleteDialog.show();
                    break;
            }
            return false;
            }
        });
        //displaying the popup
        popup.show();

    }

    public void rolePopupMenu(PopupMenu popup, final AdDTO adDTO, Integer position){

        popup.inflate(R.menu.menu_search_other_product);
        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_inscriure_product:
                        product_id = adDTO.getAdId();
                        Log.d("product_id", String.valueOf(product_id));
                        registerDialog.setMessage("Estas segur que vols inscriuret a la classe seleccionada?");
                        registerDialog.show();
                        break;

                    case R.id.menu_info_owner_product:
                        ad_user_id = adDTO.getAdUserId();
                        ad_user_name = adDTO.getUserName();

                        Intent intent = new Intent(context, MessageListActivity.class);
                        intent.putExtra("ad_owner_id", String.valueOf(ad_user_id));
                        intent.putExtra("ad_owner_name", ad_user_name);

                        context.startActivity(intent);
                        ((Activity)context).finish();
                        break;
                }
                return false;
            }
        });
        //displaying the popup
        popup.show();

    }

    public void bookedPopupMenu(PopupMenu popup, final AdDTO adDTO, Integer position){

        popup.inflate(R.menu.menu_search_booked_product);
        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_gestionar_inscripcio_product:
                        product_id = adDTO.getAdId();
                        Log.d("product_id", String.valueOf(product_id));
                        cancelRegDialog.setMessage("Estas segur que vols cancelar la inscripcio a la classe seleccionada?");
                        cancelRegDialog.show();
                        break;

                    case R.id.menu_contact_owner_product:
                        ad_user_id = adDTO.getAdUserId();
                        ad_user_name = adDTO.getUserName();

                        Intent intent = new Intent(context, MessageListActivity.class);
                        intent.putExtra("ad_owner_id", String.valueOf(ad_user_id));
                        intent.putExtra("ad_owner_name", ad_user_name);

                        context.startActivity(intent);
                        ((Activity)context).finish();
                        break;
                }
                return false;
            }
        });
        //displaying the popup
        popup.show();
    }

    /**
     * Clase per realitzar la conexio amb la base de dades i esborrar un objete de tipus userDTO
     * en funcio de l'usarID d'aquest.
     */
    private class deleteProduct extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            product_id = Integer.parseInt(strings[0]);

            boolean delete = tfClientImple.delAd(product_id, context);
            return delete;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            confirmDialog.setMessage("Producte amb id " + product_id + " esborrat");
            confirmDialog.show();
            Iterator<AdDTO> product_iterator = productList.iterator();
            while (product_iterator.hasNext()) {
                AdDTO adDTO = product_iterator.next();
                if ((adDTO.getAdId()) == product_id) {
                    productToRemove = adDTO;
                }
            }
            //Eliminem de la llista el usuari que hem esborrat i notifiquem els canvis al recycleview
            productList.remove(productToRemove);
            notifyDataSetChanged();
        }
    }
    /**
     * Clase per realitzar la conexio amb la base de dades i esborrar un objete de tipus userDTO
     * en funcio de l'usarID d'aquest.
     */
    private class editAd extends AsyncTask<String, Void, Boolean> {
        String titol, descripcio, ad_type;
        Integer preu, ad_type_id, position;
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            product_id = Integer.parseInt(strings[0]);
            titol = strings[1];
            descripcio = strings[2];
            ad_type_id = Integer.parseInt(strings[3]);
            preu = Integer.parseInt(strings[4]);
            position = Integer.parseInt(strings[5]);
            ad_type = strings[6];


            boolean edit = tfClientImple.editAd(product_id, titol, descripcio, ad_type_id, preu, context);
            return edit;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            confirmDialog.setMessage("Producte amb id " + product_id + " modificat");
            confirmDialog.show();
            Iterator<AdDTO> product_iterator = productList.iterator();
            while (product_iterator.hasNext()) {
                AdDTO adDTO = product_iterator.next();
                if ((adDTO.getAdId()) == product_id) {
                    adDTO.setAdTittle(titol);
                    adDTO.setAdDescription(descripcio);
                    adDTO.setAdTypeId(ad_type_id);
                    adDTO.setTypesName(ad_type);
                    adDTO.setAdPrice(preu);
                    productToUpdate = adDTO;
                }
            }
            //Eliminem de la llista el usuari que hem esborrat i notifiquem els canvis al recycleview
            productList.set(position, productToUpdate);
            notifyDataSetChanged();


        }
    }
    /**
     * Clase per realitzar la conexio amb la base de dades i esborrar un objete de tipus userDTO
     * en funcio de l'usarID d'aquest.
     */
    private class registerProduct extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Integer...integers) {
            product_id = integers[0];
            ad_user_booking_id = integers[1];


            boolean registrat = tfClientImple.bookAd(product_id, ad_user_booking_id, context);
            return registrat;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            confirmDialog.setMessage("Usuari amb id " + ad_user_booking_id + " registrat a producte amb id " + product_id);
            confirmDialog.show();
            Iterator<AdDTO> product_iterator = productList.iterator();
            while (product_iterator.hasNext()) {
                AdDTO adDTO = product_iterator.next();
                if ((adDTO.getAdId()) == product_id) {
                    productToRemove = adDTO;
                }
            }
            //Eliminem de la llista el usuari que hem esborrat i notifiquem els canvis al recycleview
            productList.remove(productToRemove);
            notifyDataSetChanged();
        }
    }

    /**
     * Clase per realitzar la conexio amb la base de dades i esborrar un objete de tipus userDTO
     * en funcio de l'usarID d'aquest.
     */
    private class cancelRegisterProduct extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Integer...integers) {
            product_id = integers[0];

            boolean registrat = tfClientImple.cancelBookAd(product_id, context);
            return registrat;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            confirmDialog.setMessage("Usuari amb id " + ad_user_booking_id + " cancela inscripcio a producte amb id " + product_id);
            confirmDialog.show();
            Iterator<AdDTO> product_iterator = productList.iterator();
            while (product_iterator.hasNext()) {
                AdDTO adDTO = product_iterator.next();
                if ((adDTO.getAdId()) == product_id) {
                    productToRemove = adDTO;
                }
            }
            //Eliminem de la llista el usuari que hem esborrat i notifiquem els canvis al recycleview
            productList.remove(productToRemove);
            notifyDataSetChanged();
        }
    }

    /**
     * Clase per realitzar la conexio amb la base de dades i guardar un objecte de tipus usuari
     * En aquests moments encara no tenim implementada aquesta funcio i per tant aquest apartat no es funcional
     */
    private class getAdTypeByName extends AsyncTask<String, Void, Integer> {
        String categoria, productId, titol, descripcio, preu, position;
        Integer adTypeId;

        @Override
        protected void onPreExecute(){}

        @Override
        protected Integer doInBackground(String... strings) {
            categoria = strings[0];
            productId = strings[1];
            titol = strings[2];
            descripcio = strings[3];
            preu = strings[4];
            position = strings[5];

            adTypeId = tfClientImple.getAdTypeByName(categoria, context);
            return adTypeId;
        }

        @Override
        protected void onPostExecute(Integer result){
            super.onPostExecute(result);

            //Llancem l'asynctask per realitzar la conexio en segon pla
            new editAd().execute(productId, titol, descripcio, String.valueOf(adTypeId), preu, position, categoria);

        }
    }
}


