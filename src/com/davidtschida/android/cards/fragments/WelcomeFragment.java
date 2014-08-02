package com.davidtschida.android.cards.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davidtschida.android.cards.R;
import com.davidtschida.android.cast.framework.OnCastConnectedListener;
import com.davidtschida.android.cast.framework.OnMessageReceivedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by david on 7/27/14.
 */
public class WelcomeFragment extends CastFragment implements OnMessageReceivedListener, OnCastConnectedListener {

    final String TAG = "Welfome Fragment";

    private String mUserName;
    public WelcomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUserName = getUsername(inflater.getContext());

        return inflater.inflate(R.layout.welcome, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onMessageRecieved(JSONObject json) {
        //Recieve ready with cards.

        String command = null;
        try {
            command = json.getString("command");

            if(command.equals("cards")) {
                JSONArray cards = json.getJSONArray("content");

                ArrayList<String> cardNames = new ArrayList<String>();

                for (int i = 0; i < cards.length(); i++) {
                    JSONObject card = cards.getJSONObject(i);
                    String name = card.getString("name");

                    cardNames.add(name);
                }

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, WaitingForPlayersFragment.newInstance(cardNames)).commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUsername(Context context){
        try {
            AccountManager manager = AccountManager.get(context);
            Account[] accounts = manager.getAccountsByType("com.google");
            List<String> possibleEmails = new LinkedList<String>();

            for (Account account : accounts) {
                // TODO: Check possibleEmail against an email regex or treat
                // account.name as an email address only for certain account.type values.
                possibleEmails.add(account.name);
            }

            if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
                String email = possibleEmails.get(0);
                String[] parts = email.split("@");
                if (parts.length > 0 && parts[0] != null)
                    return parts[0];
                else
                    return "Unknown";
            } else
                return "Unknown";

        }catch (Exception ex){
            Log.e(TAG,"Exception getting user name: ", ex);
            return "Unknown";
        }
    }

    @Override
    public void onCastConnected() {
        JSONObject json = new JSONObject();
        try {
            json.put("command", "join");
            json.put("name", mUserName);
            host.getCastmanager().setOnMessageRecievedListener(this);
            host.getCastmanager().sendMessage(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
