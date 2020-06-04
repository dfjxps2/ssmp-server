package com.seaboxdata.auth.server.axis.service.impl;

import com.seaboxdata.auth.server.axis.model.OrgInfo;
import com.seaboxdata.auth.server.axis.model.RoleInfo;
import com.seaboxdata.auth.server.axis.model.UserInfo;
import com.seaboxdata.auth.server.axis.service.IAxisSynService;
import com.seaboxdata.auth.server.axis.utils.AxisUtis;
import com.seaboxdata.auth.server.axis.utils.CaEncryptUtils;
import com.seaboxdata.commons.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.xml.namespace.QName;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class AxisSynServiceImpl implements IAxisSynService {

    @Value("${axis.ca.service.url}")
    private String webServiceURL;

    @Value("${axis.ca.service.qname}")
    private String qName;


    /**
     * 根据账号密码获取y用户的usercode
     * @param loginName
     * @param password
     * @return
     */
    @Override
    public String getUserCode(String loginName, String password) {

        try {
            Options options = AxisUtis.createRPCServiceClient().getOptions();
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CONNECTION_TIMEOUT, new Integer(48000000));
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, "false");
            EndpointReference targetEPR = new EndpointReference(webServiceURL);//url地址由太极公司提供
            options.setTo(targetEPR);
            QName opGetAllLegalInfor = new QName(qName, AxisUtis.GETUSERCODE);
            Object[] opGetAllLegalInforArgs = new Object[] {loginName,password};
            Class[] returnTypes = new Class[] { String.class };
            Object[] response = AxisUtis.createRPCServiceClient().invokeBlocking(opGetAllLegalInfor,opGetAllLegalInforArgs,returnTypes);
            String xml = (String) response[0];//返回值为usercode
            log.info("getUserCode接收xml: " + xml);

            /**
             * 解密
             */
            xml = CaEncryptUtils.ssoEncrypt(xml, false);

            return xml;
        }catch (Exception e){
            log.error("loginName: {}, password: {} 获取usercode异常：{}", loginName, password, e);
            throw new ServiceException("500", "获取usercode异常");
        }
    }

    /**
     * 获取用户信息
     * @param userId
     * @param appId
     * @return
     *
     * 文档中getUserInfoByXml没有说明需要参数appId，但代码样例中提到，需要再确实中确认
     */
    @Override
    public String getUserInfo(String userId, String appId) {
        try {
            Options options = AxisUtis.createRPCServiceClient().getOptions();
            options.setProperty(
                    org.apache.axis2.transport.http.HTTPConstants.CONNECTION_TIMEOUT,
                    new Integer(48000000));
            EndpointReference targetEPR = new EndpointReference(webServiceURL);
            options.setTo(targetEPR);
            QName opGetAllLegalInfor = new QName(qName, AxisUtis.GETUSEINFOBYXML);
            Object[] opGetAllLegalInforArgs = new Object[] { userId, appId };
            Class[] returnTypes = new Class[] { String.class };
            Object[] response = AxisUtis.createRPCServiceClient().invokeBlocking(
                    opGetAllLegalInfor, opGetAllLegalInforArgs, returnTypes);
            String userInfo = (String) response[0];
            return userInfo;
        } catch (Exception e) {
            log.error("userId: {}, appId: {} 获取用户信息异常：{}", userId, appId, e);
            throw new ServiceException("500", "获取用户信息异常");
        }
    }

    @Override
    public String getData(String id, String method) {
        try {
            Options options = AxisUtis.createRPCServiceClient().getOptions();
            options.setProperty(
                    org.apache.axis2.transport.http.HTTPConstants.CONNECTION_TIMEOUT,
                    new Integer(48000000));
            EndpointReference targetEPR = new EndpointReference(webServiceURL);// webServiceURL的值是如何获得的ss
            System.out.println(webServiceURL);
            options.setTo(targetEPR);

            QName opGetAllLegalInfor = new QName(qName, method);
            Object[] opGetAllLegalInforArgs = new Object[] { id };
            Class[] returnTypes = new Class[] { String.class };
            Object[] response = AxisUtis.createRPCServiceClient().invokeBlocking(
                    opGetAllLegalInfor, opGetAllLegalInforArgs, returnTypes);
            String deptInfo = (String) response[0];
            return deptInfo;
        } catch (Exception e) {
            log.error("id: {}, method: {} 获取数据异常：{}", id, method, e);
            throw new ServiceException("500", "获取数据异常");
        }
    }

    /**
     * 获取机构
     * @param deptId
     * @return
     */
    @Override
    public String getDeptInfo(String deptId) {
        return this.getData(deptId, AxisUtis.GETDEPTINFOBYXML);
    }

    /**
     * 获取角色
     * @param roleId
     * @return
     */
    @Override
    public String getRoleInfo(String roleId) {
        return this.getData(roleId, AxisUtis.GETDEPTINFOBYXML);
    }

    /**
     * xml转UserInfo
     */
    @Override
    public UserInfo xmlToUserInfo(String xml) {
        try {
            log.info("xmlToUserInfo接收xml: " + xml);
            UserInfo userInfo = new UserInfo();

            // 将字符串转为XML
            Document doc = DocumentHelper.parseText(xml);
            // 获取根节点
            Element rootElt = doc.getRootElement();
            // 拿到根节点的名称
            System.out.println("根节点：" + rootElt.getName());

            // 获取根节点下的子节点xml
            Iterator iter = rootElt.elementIterator("xml");
            // 遍历xml节点
            while (iter.hasNext()) {

                Element recordEle = (Element) iter.next();
                // 拿到xml节点下的子节点userCode值
                userInfo.setUserCode(recordEle.elementTextTrim("userCode"));
                Iterator<Element> elementIt = recordEle.elementIterator("userCode");
                while(elementIt.hasNext()){
                    Element userCodeElement = elementIt.next();
                    String resultText = userCodeElement.getText();
                }

                userInfo.setCaCode(recordEle.elementTextTrim("caCode"));
                userInfo.setName(recordEle.elementTextTrim("name"));
                userInfo.setUserLoginName(recordEle.elementTextTrim("userLoginName"));
                userInfo.setSynpassword(recordEle.elementTextTrim("synpassword"));
                userInfo.setSex(recordEle.elementTextTrim("sex"));
                userInfo.setAddress(recordEle.elementTextTrim("address"));
                userInfo.setIdentityCard(recordEle.elementTextTrim("identityCard"));
                userInfo.setPostCode(recordEle.elementTextTrim("postCode"));
                userInfo.setOfficePhone(recordEle.elementTextTrim("officePhone"));
                userInfo.setMobilePhone(recordEle.elementTextTrim("mobilePhone"));
                userInfo.setFax(recordEle.elementTextTrim("fax"));
                userInfo.setEmail(recordEle.elementTextTrim("email"));
                userInfo.setUserType(recordEle.elementTextTrim("userType"));
                userInfo.setUserPost(recordEle.elementTextTrim("userPost"));
                userInfo.setUserDuty(recordEle.elementTextTrim("userDuty"));
                userInfo.setOrgCode(recordEle.elementTextTrim("orgCode"));

                try {
                    String order = recordEle.elementTextTrim("userOrder");
                    if(StringUtils.isNotBlank(order)){
                        userInfo.setUserOrder(order);
                    }else{
                        userInfo.setUserOrder("1");
                    }
                } catch (Exception e){
                    userInfo.setUserOrder("1");
                }

                /**
                 * 文档中命令是roleList，代码样例中是roles，请测试确认
                 */
                Element rolesElement = recordEle.element("roles");
                List roleList = rolesElement.elements("role");
                List<String> roles= new ArrayList<>();
                for (int i = 0; i < roleList.size(); i++) {
                    Element roleElement = (Element)roleList.get(i);
                    String role = roleElement.getText();
                    roles.add(role);
                }
                userInfo.setRoleList(roles);
            }
            return userInfo;
        } catch (Exception e) {
            log.error("xml报文："+ xml +", 转化异常：{}", e);
            throw new ServiceException("500", "用户信息xml转化异常");
        }
    }

    /**
     * xml转OrgInfo
     */
    @Override
    public OrgInfo xmlToOrgInfo(String xml) {
        try {
            log.info("xmlToOrgInfo接收xml: " + xml);
            OrgInfo orgInfo = new OrgInfo();

            // 将字符串转为XML
            Document doc = DocumentHelper.parseText(xml);
            // 获取根节点
            Element rootElt = doc.getRootElement();
            // 拿到根节点的名称
            System.out.println("根节点：" + rootElt.getName());

            // 获取根节点下的子节点xml
            Iterator iter = rootElt.elementIterator("xml");
            // 遍历xml节点
            while (iter.hasNext()) {

                Element recordEle = (Element) iter.next();
                // 拿到xml节点下的子节点userCode值
                orgInfo.setOrgCode(recordEle.elementTextTrim("orgCode"));
                orgInfo.setCaCode(recordEle.elementTextTrim("caCode"));
                orgInfo.setParentCode(recordEle.elementTextTrim("parentCode"));
                orgInfo.setName(recordEle.elementTextTrim("name"));

                try {
                    String order = recordEle.elementTextTrim("orgOrder");
                    if(StringUtils.isNotBlank(order)){
                        orgInfo.setOrgOrder(Integer.valueOf(order));
                    }else{
                        orgInfo.setOrgOrder(1);
                    }
                }catch (Exception e){
                    orgInfo.setOrgOrder(1);
                }

            }
            return orgInfo;
        } catch (Exception e) {
            log.error("xml报文："+ xml +", 转化异常：{}", e);
            throw new ServiceException("500", "机构信息xml转化异常");
        }
    }

    /**
     * xml转RoleInfo
     */
    @Override
    public RoleInfo xmlToRoleInfo(String xml) {
        try {
            log.info("xmlToRoleInfo接收xml: " + xml);
            RoleInfo roleInfo = new RoleInfo();

            // 将字符串转为XML
            Document doc = DocumentHelper.parseText(xml);
            // 获取根节点
            Element rootElt = doc.getRootElement();
            // 拿到根节点的名称
            System.out.println("根节点：" + rootElt.getName());

            // 获取根节点下的子节点xml
            Iterator iter = rootElt.elementIterator("xml");
            // 遍历xml节点
            while (iter.hasNext()) {
                Element recordEle = (Element) iter.next();
                // 拿到xml节点下的子节点userCode值
                roleInfo.setRoleCode(recordEle.elementTextTrim("roleCode"));
                roleInfo.setName(recordEle.elementTextTrim("name"));
                roleInfo.setOrgCode(recordEle.elementTextTrim("orgCode"));
            }
            return roleInfo;
        } catch (Exception e) {
            log.error("xml报文："+ xml +", 转化异常：{}", e);
            throw new ServiceException("500", "角色信息xml转化异常");
        }
    }
}
