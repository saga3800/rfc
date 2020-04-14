package com.claro.sap.rfcwrapper.rfc;



import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class PoolConnectionManager {

  @Value("${sap.datasource.sysnr}")
  private String sysNr;

  @Value("${sap.datasource.host}")
  private String host;

  @Value("${sap.datasource.client}")
  private String client;

  @Value("${sap.datasource.user}")
  private String user;

  @Value("${sap.datasource.password}")
  private String password;

  @Value("${sap.datasource.lang}")
  private String lang;

  private String ABAP_AS = "mySAPSystem";

  private CustomDestinationProvider destinationProvider;

  private Map<String, String> users = new HashMap<>();

  @PostConstruct
  public void init() {
    this.destinationProvider = new CustomDestinationProvider(host,sysNr,user,client,password,lang);
    com.sap.conn.jco.ext.Environment.registerDestinationDataProvider( destinationProvider );
    /*Properties connectProperties = new Properties();
    connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, this.host);
    connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, this.sysNr);
    connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, this.client);
    connectProperties.setProperty(DestinationDataProvider.JCO_USER, this.user);
    connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, this.password);
    connectProperties.setProperty(DestinationDataProvider.JCO_LANG, this.lang);
    connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "10");*/
    //createDataFile(ABAP_AS, "jcoDestination", connectProperties);
  }

  /**
   * Obtiene el contexto para interactuar con SAP
   * @return retorna el JCoDestination
   * @throws JCoException
   */
  public JCoDestination getDestination() throws JCoException {
    JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS);
    return destination;
  }

  /**
   * Obtiene la metadata de la funcion SAP(rfc)
   * @param name
   * @return
   * @throws JCoException
   */
  public JCoFunction getFunction(String name) throws JCoException {
    JCoFunction function = this.getDestination().getRepository().getFunction(name);
    return function;
  }

  /**
   * Ejecuta un RFC
   * @param function
   * @throws JCoException
   */
  public void executeFuction(JCoFunction function) throws JCoException {
    function.execute(this.getDestination());
  }

  /**
   * Ejecuta un RFC utilizando el usuario y contraseña enviados en la petición
   * @param function función a ejecutar
   * @param user usuario para autenticación
   * @param password contraseña de autentciación
   * @throws JCoException en caso de error
   */
  public void executeFuction(JCoFunction function, String user, String password) throws JCoException {
    destinationProvider.setUser(user);
    destinationProvider.setPassword(password);
    destinationProvider.getDestinationDataEventListener().updated(user);
    JCoDestination destination = JCoDestinationManager.getDestination(user);
    function.execute(destination);
  }

  /**
   * Crea el archivo de conexion a SAP
   * @param name nombre del archivo
   * @param suffix sufijo del archivo
   * @param properties propiedades de conexion a SAP
   */
  private void createDataFile(String name, String suffix, Properties properties) {
    File cfg = new File(name + "." + suffix);
    if (!cfg.exists()) {
      try {
        FileOutputStream fos = new FileOutputStream(cfg, false);
        properties.store(fos, "for tests only !");
        fos.close();
      } catch (Exception e) {
        throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);
      }
    }
  }
}
