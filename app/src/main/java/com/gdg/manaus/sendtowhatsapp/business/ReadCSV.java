package com.gdg.manaus.sendtowhatsapp.business;

import android.content.Context;
import android.net.Uri;

import com.gdg.manaus.sendtowhatsapp.model.Contact;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michnnick on 04/04/2017.
 */

public class ReadCSV implements ReadContactSource {

    private final String TAG = this.getClass().getSimpleName();

    private Uri uriFile;
    private Context context;

    public ReadCSV(Context context, Uri uriFile) {
        this.context = context;
        this.uriFile = uriFile;
    }

    @Override
    public List<Contact> getContacts() throws IOException {
        List<Contact> contacts = null;
        contacts = readFileFromUri();
        // The CSV File has a Header line, remove this.
        contacts.remove(0);
        return contacts;
    }

    private List<Contact> readFileFromUri() throws IOException {
        List<Contact> contacts = new ArrayList<>();

        InputStream inputStream = context.getContentResolver().openInputStream(uriFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ( (line = reader.readLine()) != null) {
            contacts.add(readContactFromText(line));
        }
        reader.close();
        return contacts;
    }

    private Contact readContactFromText(String line) {
        Contact contact = new Contact();
        String[] columns = line.split(",");

        contact.setFirstName(columns[1]);
        contact.setNumber(columns[3]);

        return contact;
    }
}
