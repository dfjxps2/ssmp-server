package com.seaboxdata.auth.server.bj.cas.synchrodata;

import com.seaboxdata.auth.server.bj.cas.config.SoapWsdlAddressConfig;
import com.seaboxdata.auth.server.bj.cas.model.CasOrganize;
import com.seaboxdata.auth.server.bj.cas.model.CasUser;
import com.seaboxdata.commons.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.seaboxdata.auth.server.bj.cas.synchrodata.Dom4jUtil.readDom4jXml;

@Slf4j
@Component
public class WebClient {

    private static SoapWsdlAddressConfig soapWsdlAddressConfig;

    @Autowired
    public void init(SoapWsdlAddressConfig soapWsdlAddressConfig) {
        WebClient.soapWsdlAddressConfig = soapWsdlAddressConfig;
    }

    /**
     * 设置发送请求的URL
     *
     * @param mp
     * @param operationName
     * @return
     * @param: param:参数类型   paramValue:参数值  method:方法名
     * @return： 请求的URL
     */
    private static OMElement buildWsdlParam(Map<String, Object> mp, String operationName) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(SynchronizedDataConstants.SOAP_TARGET_NAMESPACE, "");
        String val = "";
        OMElement data = fac.createOMElement(operationName, omNs);
        //获得要调用的方法名
        for (Map.Entry<String, Object> entry : mp.entrySet()) {
            //获得该方法名要调用的参数名
            OMElement inner = fac.createOMElement(new QName(entry.getKey()));
            val = entry.getValue() == null || "".equals(entry.getValue()) ? "" : entry.getValue().toString();

            //输入参数
            inner.setText(val);
            //将该参数加入要调用的方法节点
            data.addChild(inner);
        }
        return data;
    }

    private static OMElement buildNoParam(String param, String paramValue,
                                          String method) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(SynchronizedDataConstants.SOAP_TARGET_NAMESPACE, "");
        OMElement data = fac.createOMElement(method, omNs);
        QName qname = new QName(param);
        OMElement inner = fac.createOMElement(qname);
        inner.setText(paramValue);
        data.addChild(inner);
        return data;
    }


    public static Options buildWsdlOptions() {
        Options options = new Options();
        options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        options.setTo(new EndpointReference(soapWsdlAddressConfig.getUrl()));
        log.info("同步 url : {} ", soapWsdlAddressConfig.getUrl());
        //设置传输协议
        options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
        options.setTimeOutInMilliSeconds(SynchronizedDataConstants.TIMEOUT_SECONDS);
        return options;
    }


    public static String getWsdlResultByCode(Map<String, Object> mp, String operationName) {
        ServiceClient sender = null;
        try {
            sender = new ServiceClient();
            sender.setOptions(buildWsdlOptions());
            OMElement result = sender.sendReceive(buildWsdlParam(mp, operationName));
            return result.getFirstElement().getText();
        } catch (Exception e) {
            log.warn("getWsdlResultByCode", e);
            return "调用出错！";
        } finally {
            try {
                if (Objects.nonNull(sender)) {
                    sender.cleanupTransport();
                }
            } catch (AxisFault axisFault) {
                axisFault.printStackTrace();
            }
        }
    }

    /**
     * dom4j解析WebService返回的数据
     *
     * @param mp
     * @param operationName
     * @return
     * @param: param:参数类型   paramValue:参数值  method:方法名
     * @return: string类型的数据
     */

    public static List<Map<String, Object>> getResultByDom4j(Map<String, Object> mp, String operationName) {
        List<Map<String, Object>> retList = null;
        try {
            String retXml = getWsdlResultByCode(mp, operationName);
            retList = readDom4jXml(retXml);
        } catch (Exception e) {
           log.warn("getResultByDom4j", e);
        }
        return retList;

    }

    private static List<Map<String, Object>> requestCasByMap(Map<String, Object> mp, String operationName) {
        ServiceClient sender = null;
        try {
            sender = new ServiceClient();
            sender.setOptions(buildWsdlOptions());
            OMElement result = sender.sendReceive(buildWsdlParam(mp, operationName));
            String retXml = result.getFirstElement().getText();
            log.info("解析之前的数据：{}", retXml);
            List<Map<String, Object>> retList = readDom4jXml(retXml);
            return retList;
        } catch (Exception e) {
            log.error("调用CAS接口{}异常：{}", operationName, e);
            throw new ServiceException("400", "调用CAS接口" + operationName + "出错");
        } finally {
            try {
                if (Objects.nonNull(sender)) {
                    sender.cleanupTransport();
                }
            } catch (AxisFault axisFault) {
                axisFault.printStackTrace();
            }
        }
    }

    /**
     * 获取单个用户信息
     */
    public static CasUser casUser(String username) {
        Map<String, Object> usermp = new HashMap<>();
        String operationName = SynchronizedDataConstants.GET_ONEUSER_WSDL_OPERATION_NAME;
        usermp.put("arg0", soapWsdlAddressConfig.getAppname());
        usermp.put("arg1", username);
        //传入参数名，参数值，方法名
        List<Map<String, Object>> retList = requestCasByMap(usermp, operationName);

        if (null == retList || retList.isEmpty()) {
            throw new ServiceException("500", username + "调用接口查无数据");
        }
        CasUser casUser = CasUser.toModel(retList.get(0));
        casUser.setCasOrganize(casOrganize(username));

        return casUser;
    }

    /**
     * 获取单个机构数据接口
     */
    public static CasOrganize casOrganize(String username) {
        Map<String, Object> mp = new HashMap<>();
        String operationName = SynchronizedDataConstants.GET_ORG_DATA_BY_USER_ID;
        mp.put("arg0", username);
        //传入参数名，参数值，方法名
        List<Map<String, Object>> retList = requestCasByMap(mp, operationName);
        if (null != retList && !retList.isEmpty()) {
            return CasOrganize.toModel(retList.get(0));
        }
        return null;
    }

    /**
     * 批量同步用户数据接口
     */
    public static List<CasUser> casUsers(String dateStart, String dataEnd) {
        Map<String, Object> mp = new HashMap<>();
        String operationName = SynchronizedDataConstants.GET_BATCHUSER_WSDL_OPERATION_NAME;
        mp.put("arg0", soapWsdlAddressConfig.getAppname());
        mp.put("arg1", dateStart);
        mp.put("arg2", dataEnd);
        //传入参数名，参数值，方法名
        List<Map<String, Object>> retList = requestCasByMap(mp, operationName);
        return retList.stream().map(v -> {
            CasUser casUser = CasUser.toModel(v);
            casUser.setCasOrganize(casOrganize(casUser.getUsername()));
            return casUser;
        }).collect(Collectors.toList());
    }

    /**
     * 获取批量机构数据接口
     */
    public static List<CasOrganize> casOrganizes() {
        Map<String, Object> mp = new HashMap<>();
        String operationName = SynchronizedDataConstants.GET_ORG_BATCH_DATA;
        //传入参数名，参数值，方法名
        List<Map<String, Object>> retList = requestCasByMap(mp, operationName);
        return retList.stream().map(CasOrganize::toModel).collect(Collectors.toList());
    }


}


