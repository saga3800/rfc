package com.claro.sap.rfcwrapper.services;

import com.claro.sap.rfcwrapper.rfc.PoolConnectionManager;
import com.claro.sap.rfcwrapper.rfc.RemoteFunctionCaller;
import com.claro.sap.rfcwrapper.rfc.RemoteFunctionParamList;
import com.claro.sap.rfcwrapper.rfc.RemoteFunctionTemplate;
import com.sap.conn.jco.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
   * @return retorna un RemoteFunctionTemplate que contiene los parametros de salida
   */
  @Override
  public RemoteFunctionTemplate invoke(RemoteFunctionTemplate template) {
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
                RemoteFunctionParamList params = entry.getValue();
                table.appendRow();
                params
                    .getParamList()
                    .entrySet()
                    .forEach(
                        dataEntry -> {
                          table.setValue(dataEntry.getKey(), dataEntry.getValue());
                        });
              });

      connectionManager.executeFuction(function);
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
        if (field.getValue() != null && !field.getValue().toString().isEmpty()) {
          data.put(field.getName(), field.getValue());
        }
      }
      if (!data.isEmpty()) {
        template.getOutputParamList().add(data);
      }

      tables.forEach(
          jCoField -> {
              if(jCoField.getName().startsWith("EX") && jCoField.getTable().getNumRows() > 0){
                  JCoFieldIterator tablaDatosIterator = jCoField.getTable().getRecordFieldIterator();
                  Map outputParams = new HashMap();
                  while (tablaDatosIterator.hasNextField()) {
                      JCoField field = tablaDatosIterator.nextField();
                      outputParams.put(field.getName(), field.getValue());
                  }
                  template.getOutputParamList().add(outputParams);
              }

          });
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

      return template;

    } catch (JCoException e) {
      e.printStackTrace();
      template.setError(e.getMessageText());
    }
    return null;
  }
}