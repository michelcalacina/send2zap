package com.gdg.manaus.sendtowhatsapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.gdg.manaus.sendtowhatsapp.R;
import com.gdg.manaus.sendtowhatsapp.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michnnick on 05/04/2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.Holder> {

    private LayoutInflater mInflater;
    private List<Contact> contacts;

    public ContactAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        contacts = new ArrayList<>();
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<Contact> getContacts() {
        return contacts;
    }
    public void alterCheckStatus(boolean status) {
        for (Contact c : contacts) {
            c.setChecked(status);
        }
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.contact_list_item, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        Contact c = contacts.get(position);
        if (c != null) {
            holder.contactName.setText(c.getName());
            holder.contactPhone.setText(c.getNumber());
            holder.checkStatus.setChecked(contacts.get(position).isChecked());
            holder.checkStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    contacts.get(holder.getAdapterPosition()).setChecked(isChecked);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView contactName;
        TextView contactPhone;
        CheckBox checkStatus;

        public Holder(final View itemView) {
            super(itemView);

            // Fix to not loose status on checked/unchecked elements.
            this.setIsRecyclable(false);

            contactName = (TextView) itemView.findViewById(R.id.contact_name);
            contactPhone = (TextView) itemView.findViewById(R.id.contact_phone);
            checkStatus = (CheckBox) itemView.findViewById(R.id.contact_list_item_checkbox);
        }
    }
}
