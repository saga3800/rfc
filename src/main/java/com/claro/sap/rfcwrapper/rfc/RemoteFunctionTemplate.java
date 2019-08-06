package com.claro.sap.rfcwrapper.rfc;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class RemoteFunctionTemplate {

  private Map<String, Object> simpleParams = new HashMap<>();
  private Map<String, RemoteFunctionParamList> structures = new HashMap<>();
  private Map<String, RemoteFunctionParamList> tables = new HashMap<>();
  private List<Map<String, Object>> outputParamList = new ArrayList<>();
  private String functionName;
  private String error;

  public static class Builder {

    private String functionName;
    private Map<String, Object> simpleParams = new HashMap<>();
    private Map<String, RemoteFunctionParamList.Builder> structures = new HashMap<>();
    private Map<String, RemoteFunctionParamList.Builder> tables = new HashMap<>();

    public Builder function(String name) {
      this.functionName = name;
      return this;
    }

    public Builder param(String name, Object value) {
      this.simpleParams.put(name, value);
      return this;
    }

    public RemoteFunctionParamList.Builder structure(String name) {
      RemoteFunctionParamList.Builder builder = new RemoteFunctionParamList.Builder(this);
      this.structures.put(name, builder);
      return builder;
    }

    public RemoteFunctionParamList.Builder table(String name) {
      RemoteFunctionParamList.Builder builder = new RemoteFunctionParamList.Builder(this);
      this.tables.put(name, builder);
      return builder;
    }

    public RemoteFunctionTemplate build() {
      RemoteFunctionTemplate builder = new RemoteFunctionTemplate();
      builder.functionName = this.functionName;
      builder.simpleParams = this.simpleParams;
      Map<String, RemoteFunctionParamList> strs =
          this.structures.entrySet().stream()
              .collect(Collectors.toMap(o -> o.getKey(), o -> o.getValue().build()));
      builder.structures = strs;
      Map<String, RemoteFunctionParamList> tbls =
          this.tables.entrySet().stream()
              .collect(Collectors.toMap(o -> o.getKey(), o -> o.getValue().build()));
      builder.tables = tbls;
      return builder;
    }
  }
}