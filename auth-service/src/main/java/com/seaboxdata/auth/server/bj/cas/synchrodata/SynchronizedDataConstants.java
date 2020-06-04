package com.seaboxdata.auth.server.bj.cas.synchrodata;


import org.apache.axis2.addressing.EndpointReference;

/**
* @author makaiyu
* @description costs
* @date 19:17 2019/10/17
**/
public class SynchronizedDataConstants {
	 
	 //成功
	 public static final String SUCCESS_STATUS = "1";
	 //失败
	 public static final String FAIL_STATUS = "0";
	 
	 public static final String ZERO_TOTAL = "0";

	public final static String SOAP_WSDL_ADDRESS = "http://10.1.3.2:18001/portal/intlDataSynchronizedService?wsdl";
//	public final static String SOAP_WSDL_ADDRESS = "http://10.1.1.137:8443/portal/intlDataSynchronizedService?wsdl";

	public final static String SOAP_TARGET_NAMESPACE = "http://internal.synchrodata.security.portal.quick.com/";
	
	public final static String GET_CASLOGIN_URL = "https://10.1.3.193:8443/cas/login?locale=zh_CN&service=http%3A%2F%2F127.0.0.1%3A8080%2Fportal%2Fcallback%3Fclient_name%3DCasClient";

	public final static String WSDL_SERVICE_NAME = "intlDataSynchronizedService";

	public final static String GET_ONEUSER_WSDL_OPERATION_NAME = "getUsersDataByUserID";

	public final static String GET_BATCHUSER_WSDL_OPERATION_NAME = "getUserBatchDataByAppName";

	public final static String GET_ORG_BATCH_DATA = "getOrgBatchData";

	public final static String GET_ORG_DATA_BY_USER_ID = "getOrgDataByUserID";

	public final static String APP_NAME = "用户权限中心";

	//30秒
	public final static long TIMEOUT_SECONDS =30000;

	public static EndpointReference targetAirline = new EndpointReference(
			SOAP_WSDL_ADDRESS);

}
