package com.seaboxdata.auth.server.axis.utils;

import org.apache.axis2.AxisFault;
import org.apache.axis2.rpc.client.RPCServiceClient;

public class AxisUtis {

    private static RPCServiceClient rpcServiceClient;

    public static RPCServiceClient createRPCServiceClient() throws AxisFault {
        if(null == rpcServiceClient){
            rpcServiceClient = new RPCServiceClient();
        }
        return rpcServiceClient;
    }

    /**
     * 获取用户usercode
     */
    public static final String GETUSERCODE = "getUserCode";

    /**
     * 获取用户信息
     */
    public static final String GETUSEINFOBYXML = "getUserInfoByXml";

    /**
     * 获取机构信息
     */
    public static final String GETDEPTINFOBYXML = "getDeptInfoByXml";

    /**
     * 获取角色信息
     */
    public static final String GETROLEINFOBYXML = "getRoleInfoByXml";

}
