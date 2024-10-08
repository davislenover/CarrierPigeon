package com.group12.carrierpigeon.controller;

import com.group12.carrierpigeon.Info;
import com.group12.carrierpigeon.components.accounts.Account;
import com.group12.carrierpigeon.networking.DataObject;
import com.group12.carrierpigeon.threading.Publisher;
import com.group12.carrierpigeon.threading.Subscriber;

/**
 * Authentication Controller class responsible for session tickets and their validity
 */
public class Authentication extends Publisher<Boolean> implements Subscriber<DataObject> {
    private Account account;

    public Authentication(String serverIp, Integer port) {
        this.account = new Account();
        this.account.setConnectionDetails(serverIp,port);
        this.account.subscribe(this);
    }


    public void authenticate(String username, String password) {
        this.account.setCredentials(username, password);
        Info.password = password; Info.username = username;
        this.account.handleDataResponseCommand(this.account.init,"AUTH");
    }

    public Account getAccount() {
        return this.account;
    }


    @Override
    public void update(DataObject context, String whoIs) {
        if (whoIs != null && whoIs.contains("AUTH") && context.getStatus().equals(DataObject.Status.VALID)) {
            // When Authentication is notified, it is done so via the UI thread
            // Any logic to be executed by subscribers of the controller is done via the UI thread too
            this.notifySubscribersInSameThread(true,whoIs);
            // Automatically unsubscribe everyone when used authenticate
            this.unsubscribeAll();
        } else if (whoIs != null && whoIs.contains("AUTH")) {
            this.notifySubscribersInSameThread(false,whoIs);
            // Automatically unsubscribe everyone when used authenticate
            this.unsubscribeAll();
        }
    }
}
