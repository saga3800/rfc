package com.claro.sap.rfcwrapper.rfc;

import java.util.List;

public interface RemoteFunctionCaller {
  RemoteFunctionTemplate invoke(RemoteFunctionTemplate template, List<String> outputTables);
}
