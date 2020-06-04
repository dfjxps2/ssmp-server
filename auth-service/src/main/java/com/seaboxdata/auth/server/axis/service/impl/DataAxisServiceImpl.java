package com.seaboxdata.auth.server.axis.service.impl;

import com.seaboxdata.auth.server.axis.service.IDataAxisService;
import com.seaboxdata.auth.server.axis.service.OperLocalService;
import com.seaboxdata.auth.server.axis.utils.OperTypeUtils;
import com.seaboxdata.auth.server.cas.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataAxisServiceImpl implements IDataAxisService {

    @Override
    public String SynchronizedInfo(int operateId, String usercode) {

        String result = "";

        try {

            OperLocalService operLocalService = (OperLocalService) SpringContextUtils.getBean("operLocalServiceImpl");

            if(operateId == OperTypeUtils.CREATEUSER){
                //创建用户
                result = operLocalService.addUser(usercode);

            }else if(operateId == OperTypeUtils.UPDATEUSER){
                //修改用户
                result = operLocalService.updateUser(usercode);

            }else if(operateId == OperTypeUtils.DELETEUSER){
                //删除用户
                result = operLocalService.delUser(usercode);

            }else if(operateId == OperTypeUtils.CREATEROLE){
                //创建角色
                //result = operLocalService.addRole(usercode);
                result = OperTypeUtils.CA_RQUEST_SUCCESS;

            }else if(operateId == OperTypeUtils.UPDATEROLE){
                //修改角色
                //result = operLocalService.updateRole(usercode);
                result = OperTypeUtils.CA_RQUEST_SUCCESS;

            }else if(operateId == OperTypeUtils.DELETEROLE){
                //删除角色
                //result = operLocalService.delRole(usercode);
                result = OperTypeUtils.CA_RQUEST_SUCCESS;

            }else if(operateId == OperTypeUtils.CREATEDEPT){
                //创建机构
                result = operLocalService.addOrg(usercode);

            }else if(operateId == OperTypeUtils.UPDATEDEPT){
                //修改机构
                result = operLocalService.updateOrg(usercode);

            }else if(operateId == OperTypeUtils.DELETEDEPT){
                //删除机构
                result = operLocalService.delOrg(usercode);
            }

        } catch (Exception e) {
            log.error("CA请求参数：operateId：{}, usercode: {}, 异常信息：{}", operateId, usercode, e);
            result = OperTypeUtils.OTHER_ERROR_CODE;
        }
        log.info("CA请求参数：operateId：{}, usercode: {}, 返回结果result：{}", operateId, usercode, result);

        return result;
    }

}
