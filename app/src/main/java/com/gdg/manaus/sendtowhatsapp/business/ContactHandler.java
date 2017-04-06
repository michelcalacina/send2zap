package com.gdg.manaus.sendtowhatsapp.business;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.gdg.manaus.sendtowhatsapp.model.Contact;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by Michnnick on 05/04/2017.
 */

public class ContactHandler implements Runnable {

    /**
     * This callback interface but be implemented
     * for it that need to manage ContactHandler class.
     */
    public interface ContactHandlerCallBack {
        void onContactsLoad(List<Contact> contacts);
        void onExtractContactError();
    }

    private final String TAG = this.getClass().getSimpleName();

    // The file URI that contains all contacts.
    private Uri uri;

    /**
     * If application grows up, could be need to read contacts from differente
     * ways, because of it, the polymorphism exist.
     */
    private ReadContactSource reader;

    private ContactHandlerCallBack callBack;

    public ContactHandler(Context context, Uri uri, ContactHandlerCallBack callBack)
            throws IOException {
        if (uri == null) {
            throw new NullPointerException();
        }

        this.uri = uri;
        reader = new ReadCSV(context, uri);
        this.callBack = callBack;
    }

    @Override
    public void run() {
        try {
            callBack.onContactsLoad(reader.getContacts());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            callBack.onExtractContactError();
        }
    }

}
