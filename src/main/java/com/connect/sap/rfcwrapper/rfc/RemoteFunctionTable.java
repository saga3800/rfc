package com.connect.sap.rfcwrapper.rfc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RemoteFunctionTable {
  private ArrayList<Map> rows = new ArrayList<>();
  private String name;

  /*public void setParam(String name, Object value) {
      getRows().put(name, value);
    }
  */

  public ArrayList<Map> getRows() {
    return rows;
  }

  public void setParamList(ArrayList<Map> rows) {
    this.rows = rows;
  }  

  public static class Builder {
    ArrayList<Map> rows = new ArrayList<>();
    private RemoteFunctionTemplate.Builder parentBuilder;
    private String name;
    private Map currentRow;

    public Builder(RemoteFunctionTemplate.Builder parentBuilder) {
      this.parentBuilder = parentBuilder;
    }

    public Builder name() {
      this.name = name;
      return this;
    }

    public Builder row() {
      currentRow = new HashMap();
      rows.add(currentRow);
      return this;
    }

    public Builder param(String name, Object value) {
      this.currentRow.put(name, value);
      return this;
    }

    public RemoteFunctionTemplate.Builder end() {
      return this.parentBuilder;
    }

    public RemoteFunctionTable build() {
      RemoteFunctionTable paramList = new RemoteFunctionTable();
      paramList.setParamList(this.rows);
      return paramList;
    }
  }
}
