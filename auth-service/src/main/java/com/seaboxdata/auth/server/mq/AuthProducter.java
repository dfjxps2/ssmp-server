package com.seaboxdata.auth.server.mq;

import com.seaboxdata.commons.Msg;
import com.seaboxdata.commons.enums.AppKeyEnum;
import com.seaboxdata.commons.enums.ImplTypeEnum;
import com.seaboxdata.commons.mq.auth.AuthTag;
import com.seaboxdata.commons.mq.auth.AuthTopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
public class AuthProducter {

    @Value("${rocketmq.producer.group}")
    private String consumerGroup;

    @Value("${spring.profiles.active}")
    private String env;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * @param userId, name, uid, tid
     * @return void
     * @author makaiyu
     * @description 删除用户发送消息
     * @date 16:31 2019/9/21
     **/
    public void delUserTag(Long userId, String name, Long uid, Long tid) {
        Msg msg = new Msg(userId, ImplTypeEnum.DELETE, name, "Auth_User " + name + "删除了");
        sendAuthUserMsg(msg, uid, tid);
    }

    /**
     * @param userId, name, uid, tid
     * @return void
     * @author makaiyu
     * @description 新增用户发送消息
     * @date 16:31 2019/9/21
     **/
    public void saveUserTag(Long userId, String name, Long uid, Long tid) {
        Msg msg = new Msg(userId, ImplTypeEnum.INSERT, name, "Auth_User " + name + "增加了");
        sendAuthUserMsg(msg, uid, tid);
    }

    /**
     * @param msg, uid, tid
     * @return void
     * @author makaiyu
     * @description 发送用户消息
     * @date 16:30 2019/9/21
     **/
    private void sendAuthUserMsg(Msg msg, Long uid, Long tid) {
        msg.setObjectType(AuthTag.USER_TAG_NAME);
        msg.setAppKey(AppKeyEnum.AUTHORIZATION);
        msg.setCreator(uid);
        msg.setTenantId(tid);
        msg.setArgs(Collections.emptyMap());
        sendMsg(msg, AuthTag.USER_TAG_NAME);
    }

    /**
     * @param tenantId, name, uid, tid
     * @return void
     * @author makaiyu
     * @description 增加租户消息
     * @date 16:31 2019/9/21
     **/
    public void saveTenantMsg(Long tenantId, String name, Long uid, Long tid) {
        Msg msg = new Msg(tenantId, ImplTypeEnum.INSERT, name, "Auth_Tenant " + name + "新增了");
        sendAuthTenantMsg(msg, uid, tid);
    }

    /**
     * @param msg, uid, tid
     * @return void
     * @author makaiyu
     * @description 增加租户消息
     * @date 16:30 2019/9/21
     **/
    private void sendAuthTenantMsg(Msg msg, Long uid, Long tid) {
        msg.setObjectType(AuthTag.TENANT_TAG_NAME);
        msg.setAppKey(AppKeyEnum.AUTHORIZATION);
        msg.setCreator(uid);
        msg.setTenantId(tid);
        msg.setArgs(Collections.emptyMap());
        sendMsg(msg, AuthTag.TENANT_TAG_NAME);
    }

    /**
     * @param organizationId, name, uid, tid
     * @return void
     * @author makaiyu
     * @description 新增机构发送消息
     * @date 16:31 2019/9/21
     **/
    public void saveOrganizationTag(Long organizationId, String name, Long uid,
                                    Long tid, Map<String, Object> map) {
        Msg msg = new Msg(organizationId, ImplTypeEnum.INSERT, name,
                "Auth_Organization " + name + "增加");
        sendOrganizationMsg(msg, uid, tid, map);
    }

    /**
     * @param organizationId, name, uid, tid
     * @return void
     * @author makaiyu
     * @description 修改机构发送消息
     * @date 16:31 2019/9/21
     **/
    public void updateOrganizationTag(Long organizationId, String name, Long uid,
                                      Long tid, Map<String, Object> map) {
        Msg msg = new Msg(organizationId, ImplTypeEnum.UPDATE, name,
                "Auth_Organization " + name + "修改");
        sendOrganizationMsg(msg, uid, tid, map);
    }

    /**
     * @param msg, uid, tid
     * @param map
     * @return void
     * @author makaiyu
     * @description 发送机构消息
     * @date 16:30 2019/9/21
     **/
    private void sendOrganizationMsg(Msg msg, Long uid, Long tid, Map<String, Object> map) {
        msg.setObjectType(AuthTag.ORGANIZATION_TAG_NAME);
        msg.setAppKey(AppKeyEnum.AUTHORIZATION);
        msg.setCreator(uid);
        msg.setTenantId(tid);
        msg.setArgs(map);
        sendMsg(msg, AuthTag.ORGANIZATION_TAG_NAME);
    }


    public void sendMsg(Msg msg, String tag) {
        String topic = String.format("%s:%s", AuthTopic.NAME + "_" + env, tag);
        SendResult sendResult = rocketMQTemplate.syncSendOrderly(topic, msg, msg.getId() + "");
        log.info("send msg, {}, {}, {}, {}", topic, tag, msg, sendResult.getMsgId());
    }

}
