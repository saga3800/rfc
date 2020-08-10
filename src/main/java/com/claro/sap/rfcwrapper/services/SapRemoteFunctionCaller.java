package com.claro.sap.rfcwrapper.services;

import com.claro.sap.rfcwrapper.rfc.PoolConnectionManager;
import com.claro.sap.rfcwrapper.rfc.RemoteFunctionCaller;
import com.claro.sap.rfcwrapper.rfc.RemoteFunctionParamList;
import com.claro.sap.rfcwrapper.rfc.RemoteFunctionTemplate;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecordFieldIterator;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class SapRemoteFunctionCaller implements RemoteFunctionCaller {

  private PoolConnectionManager connectionManager;

  @Autowired
  private SapRemoteFunctionCaller(PoolConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
  }

  /**
   * Invoca el RFC en SAP y retorna el resultado.
   *
   * @param template objeto que contiene el nombre y parametros del RFC a invocar.
   * @param outputTables Nombres de las tablas de salida a retornar, "all" retorna todas.
   * @return retorna un RemoteFunctionTemplate que contiene los parametros de salida
   */
  @Override
  public RemoteFunctionTemplate invoke(RemoteFunctionTemplate template, List<String> outputTables) {
    try {
      JCoFunction function = connectionManager.getFunction(template.getFunctionName());

      // set simple input params
      template.getSimpleParams().entrySet().stream()
          .forEach(
              stringObjectEntry ->
                  function
                      .getImportParameterList()
                      .setValue(stringObjectEntry.getKey(), stringObjectEntry.getValue()));
      // set structures input params
      template
          .getStructures()
          .entrySet()
          .forEach(
              entry -> {
                JCoStructure structure =
                    function.getImportParameterList().getStructure(entry.getKey());
                RemoteFunctionParamList params = entry.getValue();
                params
                    .getParamList()
                    .entrySet()
                    .forEach(
                        dataEntry -> structure.setValue(dataEntry.getKey(), dataEntry.getValue()));
              });
      // set tables parameters

      template
          .getTables()
          .entrySet()
          .forEach(
              entry -> {
                JCoTable table = function.getTableParameterList().getTable(entry.getKey());
                ArrayList<Map> rows = entry.getValue();
                rows.forEach(
                    row -> {
                      table.appendRow();
                      row.entrySet()
                          .forEach(
                              dataEntry -> {
                                table.setValue(
                                    ((Map.Entry) dataEntry).getKey().toString(),
                                    ((Map.Entry) dataEntry).getValue().toString());
                              });
                    });
              });

      template
          .getImportParameterList()
          .entrySet()
          .forEach(
              stringObjectEntry -> {
                JCoTable table =
                    function.getImportParameterList().getTable(stringObjectEntry.getKey());
                table.appendRow();
                Map<String, Object> mapProperties =
                    (Map<String, Object>) stringObjectEntry.getValue();
                mapProperties
                    .entrySet()
                    .forEach(
                        stringObjectEntry1 -> {
                          if (stringObjectEntry1.getValue() instanceof ArrayList) {
                            JCoTable subTable =
                                table.getTable(stringObjectEntry1.getKey().toString());

                            List<Map<String, Object>> subTablePropertiesMap =
                                (List<Map<String, Object>>) stringObjectEntry1.getValue();
                            subTablePropertiesMap.forEach(
                                seriales -> {
                                  subTable.appendRow();
                                  seriales
                                      .entrySet()
                                      .forEach(
                                          stringObjectEntry2 -> {
                                            subTable.setValue(
                                                stringObjectEntry2.getKey(),
                                                stringObjectEntry2.getValue());
                                          });
                                });

                          } else {
                            table.setValue(
                                stringObjectEntry1.getKey(), stringObjectEntry1.getValue());
                          }
                        });
              });
      if(template.getCredentials() == null){
        connectionManager.executeFuction(function);
      } else {
          String user = template.getCredentials().get("user");
          String password = template.getCredentials().get("password");
          if(Strings.isNotEmpty(user) && Strings.isNotBlank(user) && Strings.isNotEmpty(password) && Strings.isNotBlank(password))
              // Inicio Temporal error eCommerce usuario sin permisos, por defecto dejar CO_WDIGITAL
              if (user.equalsIgnoreCase("CO_WTIENDAV")) {
                connectionManager.executeFuction(function);            
              } else {
                connectionManager.executeFuction(function, user, password);
              } 
              // Fin Temporal
              //connectionManager.executeFuction(function, user, password);
          else
              throw new IllegalArgumentException("Se requiere un usuario y contraseña");
      }
      JCoFieldIterator fieldIterator =
          function.getExportParameterList() == null
              ? null
              : function.getExportParameterList().getFieldIterator();

      // JCoTable tablaDatos = function.getTableParameterList().getTable("EX_DATOS_PEDIDO_SAP");
      // JCoTable tablaMsgs = function.getTableParameterList().getTable("EX_MENSAJES");
      JCoParameterList tables = function.getTableParameterList();

      // simple output parameters
      Map data = new HashMap();
      while (fieldIterator != null && fieldIterator.hasNextField()) {
        JCoField field = fieldIterator.nextField();

        // validar is es tabla
        if (field.isTable()) {
          JCoTable tableField = field.getTable();
          JCoRecordFieldIterator subIterator = tableField.getRecordFieldIterator();
          while (subIterator != null && subIterator.hasNextField()) {
              JCoField subField = subIterator.nextField();
              if (subField.getValue() != null && !subField.getValue().toString().isEmpty()) {
                  data.put(subField.getName(), subField.getValue());
              }
          }

        } else {
          if (field.getValue() != null && !field.getValue().toString().isEmpty()) {
            data.put(field.getName(), field.getValue());
          }
        }
      }
      if (!data.isEmpty()) {
        template.getOutputParamList().add(data);
      }

      if (tables != null) {
          if(!CollectionUtils.isEmpty(outputTables)){
              boolean allTables = outputTables.contains("all");
              Map<String, Object> tablesMap = new HashMap<>();
              tables.forEach(
                  jCoField -> {
                      String tableName = jCoField.getName();
                      if (allTables || outputTables.contains(tableName)) {
                          JCoTable table = jCoField.getTable();
                          List<Map<String, Object>> rows = new ArrayList<>();
                          for (int i = 0; i < table.getNumRows(); i++) {
                              JCoFieldIterator iterator = table.getRecordFieldIterator();
                              Map<String, Object> fieldMap = new HashMap<>();
                              while (iterator.hasNextField()) {
                                  JCoField field = iterator.nextField();
                                  fieldMap.put(field.getName(), field.getValue());
                              }
                              rows.add(fieldMap);
                              table.nextRow();
                          }
                          tablesMap.put(tableName, rows);
                      }
                  });
              template.getOutputParamList().add(tablesMap);
          }else {
              tables.forEach(
                  jCoField -> {
                      if ((jCoField.getName().startsWith("EX") || jCoField.getName().equalsIgnoreCase("RETURN")) && jCoField.getTable().getNumRows() > 0) {
                          for (int i = 0; i < jCoField.getTable().getNumRows(); i++) {
                              JCoFieldIterator tablaDatosIterator =
                                      jCoField.getTable().getRecordFieldIterator();
                              Map outputParams = new HashMap();
                              while (tablaDatosIterator.hasNextField()) {
                                  JCoField field = tablaDatosIterator.nextField();
                                  outputParams.put(field.getName(), field.getValue());
                              }
                              template.getOutputParamList().add(outputParams);
                              jCoField.getTable().nextRow();
                          }
                      }
                  });
          }
      }

      // messages output params
      /*
      if (!tablaMsgs.isEmpty()) {
        while (tablaMsgs.nextRow()) {
          JCoFieldIterator tablaMsgsIterator = tablaMsgs.getRecordFieldIterator();
          data = new HashMap();
          while (tablaMsgsIterator.hasNextField()) {
            JCoField field = tablaMsgsIterator.nextField();
            data.put(field.getName(), field.getValue());
          }
          template.getOutputParamList().add(data);
        }
      }

      // order output params
      if (!tablaDatos.isEmpty()) {
        JCoFieldIterator tablaDatosIterator = tablaDatos.getRecordFieldIterator();
        data = new HashMap();
        while (tablaDatosIterator.hasNextField()) {
          JCoField field = tablaDatosIterator.nextField();
          data.put(field.getName(), field.getValue());
        }
        template.getOutputParamList().add(data);
      }*/

    } catch (JCoException e) {
      e.printStackTrace();
      template.setError(manageError(e));
    }
    return template;
  }

  private List<String> manageError (JCoException e){
      List<String> errors = new ArrayList<>();
      Throwable exception = e;
      while (exception != null){
          errors.add(exception.getClass().getName()+ ": "+exception.getMessage());
          exception = exception.getCause();
      }
      return errors;
  }
}
