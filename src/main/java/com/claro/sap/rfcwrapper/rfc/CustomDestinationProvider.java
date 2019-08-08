package com.claro.sap.rfcwrapper.rfc;

import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

import java.util.Properties;

public class CustomDestinationProvider implements DestinationDataProvider {

    private String host;
    private String sysNr;
    private String user;
    private String client;
    private String password;
    private String lang;

    public CustomDestinationProvider(String host, String sysNr, String user, String client, String password, String lang) {
        this.host = host;
        this.sysNr = sysNr;
        this.user = user;
        this.client = client;
        this.password = password;
        this.lang = lang;
    }


    @Override
    public Properties getDestinationProperties(String s) throws DataProviderException {
        Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, this.host);
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, this.sysNr);
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, this.client);
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, this.user);
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, this.password);
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, this.lang);
        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "10");
        return connectProperties;
    }

    @Override
    public boolean supportsEvents() {
        return false;
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener destinationDataEventListener) {

    }
}
