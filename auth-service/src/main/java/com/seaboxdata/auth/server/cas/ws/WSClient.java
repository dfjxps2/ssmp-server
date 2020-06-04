package com.seaboxdata.auth.server.cas.ws;

import com.seaboxdata.auth.server.cas.model.CasOrganize;
import com.seaboxdata.auth.server.cas.model.CasUser;
import com.seaboxdata.auth.server.cas.model.SynchronizedDataConstants;
import com.seaboxdata.auth.server.cas.utils.Dom4jUtil;
import com.seaboxdata.commons.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class WSClient {

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
        OMNamespace omNs = fac.createOMNamespace(SynchronizedDataConstants.SOAP_TARGET_NAMESPACE, ""); //http://tempuri.org/是命名空间
        String val = "";
        OMElement data = fac.createOMElement(operationName, omNs);
        //获得要调用的方法名
        for (Map.Entry<String, Object> entry : mp.entrySet()) {
            OMElement inner = fac.createOMElement(new QName(entry.getKey()));      //获得该方法名要调用的参数名
            val = entry.getValue() == null || "".equals(entry.getValue()) ? "" : entry.getValue().toString();

            inner.setText(val);             //输入参数
            data.addChild(inner);             //将该参数加入要调用的方法节点
        }
        return data;
    }

    private static Options buildWsdlOptions() {
        Options options = new Options();
        options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
//        options.setTo((EndpointReference) SpringContextUtils.getBean("endpointReference"));
        options.setTo(new EndpointReference("http://106.52.132.19:18001/pcmss/intlDataSynchronizedService?wsdl"));
        options.setTransportInProtocol(Constants.TRANSPORT_HTTP);     //设置传输协议
        options.setTimeOutInMilliSeconds(SynchronizedDataConstants.TIMEOUT_SECONDS);
        return options;
    }

    private static List<Map<String, Object>> requestCasByMap(Map<String, Object> mp, String operationName) {
        try {
            ServiceClient sender = new ServiceClient();
            sender.setOptions(buildWsdlOptions());
            OMElement result = sender.sendReceive(buildWsdlParam(mp, operationName));
            String retXml = result.getFirstElement().getText();
            log.info("解析之前的数据：{}", retXml);
            List<Map<String, Object>> retList = Dom4jUtil.readDom4jXml(retXml);
            return retList;
        } catch (Exception e) {
            log.error("调用CAS接口{}异常：{}", operationName, e);
            throw new ServiceException("400", "调用CAS接口" + operationName + "出错");
        }
    }

    /**
     * 获取单个用户信息
     */
    public static CasUser casUser(String username) {
        Map<String, Object> usermp = new HashMap<>();
        String operationName = SynchronizedDataConstants.GET_ONEUSER_WSDL_OPERATION_NAME;
        usermp.put("arg0", SynchronizedDataConstants.APP_NAME);
        usermp.put("arg1", username);
        List<Map<String, Object>> retList = requestCasByMap(usermp, operationName); //传入参数名，参数值，方法名

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
        List<Map<String, Object>> retList = requestCasByMap(mp, operationName); //传入参数名，参数值，方法名
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
        mp.put("arg0", SynchronizedDataConstants.APP_NAME);
        mp.put("arg1", dateStart);
        mp.put("arg2", dataEnd);
        List<Map<String, Object>> retList = requestCasByMap(mp, operationName); //传入参数名，参数值，方法名
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
        List<Map<String, Object>> retList = requestCasByMap(mp, operationName); //传入参数名，参数值，方法名
        return retList.stream().map(CasOrganize::toModel).collect(Collectors.toList());
    }

}
