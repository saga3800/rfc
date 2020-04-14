package com.claro.sap.rfcwrapper.rfc;

import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import lombok.Getter;
import lombok.Setter;

import java.util.Properties;

public class CustomDestinationProvider implements DestinationDataProvider {

    private String host;
    private String sysNr;
    @Getter @Setter
    private String user;
    private String client;
    @Setter
    private String password;
    private String lang;

    @Getter
    private DestinationDataEventListener destinationDataEventListener;

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
        return true;
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener destinationDataEventListener) {
        this.destinationDataEventListener = destinationDataEventListener;
    }
}
