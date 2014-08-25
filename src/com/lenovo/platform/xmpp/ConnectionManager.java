package com.lenovo.platform.xmpp;

import org.jivesoftware.smack.AndroidConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.lenovo.market.common.MarketApp;

public class ConnectionManager {

    private static XMPPConnection connection;

    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ConnectionManager() {
    }

    ;

    public static XMPPConnection getConnection() {
        if (connection == null) {
            XMPPConnection.DEBUG_ENABLED = true;
            // ConnectionConfiguration config = new ConnectionConfiguration(MarketApp.OPENFIRE_SERVER, MarketApp.PORT);
            AndroidConnectionConfiguration config = new AndroidConnectionConfiguration(MarketApp.OPENFIRE_SERVER, MarketApp.PORT, MarketApp.OPENFIRE_SERVER_NAME);
            config.setReconnectionAllowed(true);
            config.setSendPresence(true);
            connection = new XMPPConnection(config);
        }

        if (!connection.isConnected()) {
            try {
                connection.connect();
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void disconnect() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
    }
}
