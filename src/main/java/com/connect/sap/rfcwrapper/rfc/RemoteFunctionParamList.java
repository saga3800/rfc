package com.connect.sap.rfcwrapper.rfc;

import java.util.HashMap;
import java.util.Map;

public class RemoteFunctionParamList {
  private Map<String, Object> paramList = new HashMap();

  public void setParam(String name, Object value) {
    getParamList().put(name, value);
  }



  public Map<String, Object> getParamList() {
    return paramList;
  }

  public void setParamList(Map<String, Object> paramList) {
    this.paramList = paramList;
  }

  public static class Builder {
    private Map<String, Object> paramList = new HashMap();
    private RemoteFunctionTemplate.Builder parentBuilder;

    public Builder(RemoteFunctionTemplate.Builder parentBuilder){
      this.parentBuilder = parentBuilder;
    }

    public Builder param(String name, Object value) {
      this.paramList.put(name, value);
      return this;
    }

    public RemoteFunctionTemplate.Builder end(){
      return this.parentBuilder;
    }

    public RemoteFunctionParamList build() {
      RemoteFunctionParamList paramList = new RemoteFunctionParamList();
      paramList.setParamList(this.paramList);
      return paramList;
    }
  }
}
