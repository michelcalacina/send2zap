package com.gdg.manaus.sendtowhatsapp.business;

import com.gdg.manaus.sendtowhatsapp.model.Contact;

import java.io.IOException;
import java.util.List;

/**
 * Created by Michnnick on 05/04/2017.
 */

public interface ReadContactSource {
    public List<Contact> getContacts() throws IOException;
}
