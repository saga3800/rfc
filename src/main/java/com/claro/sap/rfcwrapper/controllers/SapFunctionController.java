package com.claro.sap.rfcwrapper.controllers;

import com.claro.sap.rfcwrapper.rfc.RemoteFunctionTemplate;
import com.claro.sap.rfcwrapper.services.SapRemoteFunctionCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@RestController
@RequestMapping("/sap")
public class SapFunctionController {

  @Autowired SapRemoteFunctionCaller remoteFunctionCaller;

  @PostMapping("/invoke")
  public List<Map<String, Object>> create(@RequestBody RemoteFunctionTemplate template,
                                          @RequestParam(name = "tablas", required = false) List<String> tablas) {

    RemoteFunctionTemplate result = remoteFunctionCaller.invoke(template, tablas);
    if (result.getError() != null) {
      int index = 1;
      List<Map<String, Object>> error = new ArrayList<>();
      Map<String, Object> detail = new TreeMap<>();
      for(String message: result.getError()) {
        detail.put("Error"+index, message);
        index++;
      }
      error.add(detail);
      return  error;
    } else {
      return result.getOutputParamList();
    }
  }

  @GetMapping("/orders")
  public RemoteFunctionTemplate get() {
    RemoteFunctionTemplate template =
        new RemoteFunctionTemplate.Builder()
            .function("Z02OTCMF_0495_CREACION_PEDIDO")
            .param("IM_LEGADO", "OMS")
            .param("IM_REGLA", "010")
            .structure("IM_DATOS_CABECERA")
            .param("NRO_PEDIDO", "114578987")
            .param("FECHA_PRECIO", "20190801")
            .param("NOMBRE1", "VICTOR")
            .param("CALLE_NUMERO", "cll 26#28-15")
            .param("CIUDAD", "BOG")
            .param("PAIS", "CO")
            .param("TELEFONO", "3002851")
            .param("MOVIL", "3043801243")
            .param("EMAIL", "victor.h.julio.hoyos@accenture.com")
            .param("MATERIAL", "7005927")
            .param("CANT_PEDIDO", "1")
            .end()
            .table("IM_POSICIONES")
            .param("POSICION", "0")
            .param("MATERIAL", "0")
            .param("CANT_PEDIDO", "0")
            .param("UNIDAD", "0")
            .param("CENTRO", "0")
            .param("ALMACEN", "0")
            .param("LOTE", "0")
            .param("PTO_EXP", "0")
            .param("ENTREGA_PARCIAL", "0")
            .param("IMPORTE_DESCUENTO", "0")
            .param("IMPORTE_AJUSTE", "0")
            .param("ANTIGUEDAD", "0")
            .param("PROCESO_VENTAS", "0")
            .param("CLAUSULA_PERMAN", "0")
            .param("TIPO_VENTA", "0")
            .param("FORMA_DE_PAGO", "0")
            .param("PLAN_PAQUETE", "0")
            .param("SECTOR", "0")
            .param("Z001", "0")
            .param("CANT_MAX", "0")
            .param("CEBE", "0")
            .param("CANT_BASE_COND", "0")
            .end()
            .table("IM_CONDICIONES")
            .param("POSICION_PEDIDO", 0)
            .param("CLASE_CONDICION", "0")
            .param("IMPORTE_CONDICION", 0.0)
            .param("CANTIDAD_CONDICION", 0.0)
            .end()
            .build();
    return template;
  }
}
