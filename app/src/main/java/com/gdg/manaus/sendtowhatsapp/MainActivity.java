package com.gdg.manaus.sendtowhatsapp;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.gdg.manaus.sendtowhatsapp.adapter.ContactAdapter;
import com.gdg.manaus.sendtowhatsapp.business.ContactHandler;
import com.gdg.manaus.sendtowhatsapp.model.Contact;
import com.gdg.manaus.sendtowhatsapp.service.GDGService;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements ContactHandler.ContactHandlerCallBack
        , View.OnClickListener
        , TextWatcher {

    private final String TAG = "GDGMainActivity";
    private final int READ_REQUEST_CODE = 42;
    private final int COLOR_GRAY = 0xff888888;
    private final int COLOR_HOLO_GREEN_DARK = 0xff669900;

    private ProgressDialog progressDialog;
    private RecyclerView mContactsRecyclerView;
    private ContactAdapter mContactAdapter;
    private EditText mMessageField;
    private RelativeLayout mainLayout;
    private Button openFile;
    private CheckBox checkBoxHeader;
    FloatingActionButton mFab;

    private boolean isAccessibilityServiceEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = (RelativeLayout) findViewById(R.id.main_content_layout);

        openFile = (Button) findViewById(R.id.action_open_file);
        openFile.setOnClickListener(this);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        // Default is to be enable, only when all requirement have been settled!
        mFab.setEnabled(false);

        mMessageField = (EditText) findViewById(R.id.message_field);
        mMessageField.addTextChangedListener(this);

        mContactAdapter = new ContactAdapter(this);
        mContactsRecyclerView = (RecyclerView) findViewById(R.id.contact_list);
        mContactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mContactsRecyclerView.setAdapter(mContactAdapter);

        checkBoxHeader = (CheckBox) findViewById(R.id.contact_list_header_checkbox);
        checkBoxHeader.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mContactAdapter.alterCheckStatus(isChecked);
            }
        });
        checkBoxHeader.setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAccessibilityServiceEnabled = isAccessibilityEnabled();
        if (!isAccessibilityServiceEnabled) {
            Snackbar snackbar = Snackbar.make(mainLayout
                    , getString(R.string.warning_enable_accessibility)
                    , Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.snack_enable_service), new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            startActivity(i);
                        }
                    });
            snackbar.show();
        } else {
            controlFab(true);
        }
    }

    private void sendMessage() {
        if (mContactAdapter.getContacts() == null || mContactAdapter.getContacts().size() == 0) {
            Snackbar.make(mainLayout
                    , getString(R.string.contact_empty_list)
                    , Snackbar.LENGTH_LONG).show();
            return;
        }

        String messageToSend = mMessageField.getText().toString();
        if (messageToSend.trim().isEmpty()) {
            return;
        }

        GDGService.setTextToSend(messageToSend);

        for (Contact c : mContactAdapter.getContacts()) {
            // Send only for selected contacts.
            if (c.isChecked()) {
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setAction(Intent.ACTION_SENDTO);
                sendIntent.setComponent(new  ComponentName("com.whatsapp","com.whatsapp.Conversation"));
                sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(c.getNumber())+"@s.whatsapp.net");

                startActivity(sendIntent);
            }
        }
    }

    private void fileChooser() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("text/*");
        // Auto enable internal storage on Picker SAF. It is not a official solution
        i.putExtra("android.content.extra.SHOW_ADVANCED", true);
        startActivityForResult(Intent.createChooser(i, "Abrir CSV"), READ_REQUEST_CODE);
    }

    @SuppressWarnings("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // FileChooser return.
        if (requestCode == READ_REQUEST_CODE && resultCode==RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                progressDialog = ProgressDialog.show(
                        this
                        , getString(R.string.dialog_title_loading_file)
                        , getString(R.string.dialog_body_loading_file)
                        , true);

                uri = data.getData();
                grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(uri, takeFlags);

                Thread t = null;
                try {
                    t = new Thread(new ContactHandler(getApplicationContext(), uri, this));
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }

                t.start();
            }
        }
    }

    @Override
    public void onContactsLoad(final List<Contact> contacts) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContactAdapter.setContacts(contacts);
                mContactAdapter.notifyDataSetChanged();
                controlFab(true);
            }
        });
        progressDialog.dismiss();
    }

    @Override
    public void onExtractContactError() {
        progressDialog.dismiss();
        Snackbar.make(mainLayout
                , getString(R.string.snack_file_not_found_exception)
                , Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == openFile.getId()) {
            fileChooser();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().isEmpty() && mFab.isEnabled()) {
            controlFab(false);
        } else if (!mFab.isEnabled()) {
            controlFab(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void controlFab(boolean enable) {
        if (!enable) {
            mFab.setEnabled(false);
            mFab.setBackgroundTintList(ColorStateList.valueOf(COLOR_GRAY));
        } else if ( !mFab.isEnabled()
                && isAccessibilityServiceEnabled
                && mMessageField.getText().length() > 0
                && mContactAdapter.getContacts() != null
                && mContactAdapter.getContacts().size() > 0) {

            mFab.setEnabled(true);
            mFab.setBackgroundTintList(ColorStateList.valueOf(COLOR_HOLO_GREEN_DARK));
        }
    }

    private boolean isAccessibilityEnabled() {
        int enabled = 0;
        final String service = getPackageName() +"/"+GDGService.class.getCanonicalName();

        try {
            enabled = Settings.Secure.getInt(getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        if (enabled == 1) {
            String settingValue = Settings.Secure.getString(
                    getApplicationContext().getContentResolver()
                    , Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            if (settingValue != null) {
                String[] values = settingValue.split(":");
                for (String s : values) {
                    if (s.equalsIgnoreCase(service))
                        return true;
                }
            }
        }

        return false;
    }
}
