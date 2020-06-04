package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seaboxdata.auth.server.dao.OauthTranCodeMapper;
import com.seaboxdata.auth.server.model.OauthTranCode;
import com.seaboxdata.auth.server.service.OauthTranCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OauthTranCodeServiceImpl implements OauthTranCodeService {

    @Autowired
    private OauthTranCodeMapper oauthTranCodeMapper;

    @Override
    public Long queryLocalIdByTranCode(String caCode, String type) {
        LambdaQueryWrapper<OauthTranCode> lambdaQueryWrapper = new QueryWrapper<OauthTranCode>().lambda();
        lambdaQueryWrapper.eq(OauthTranCode::getCaCode, caCode);
        lambdaQueryWrapper.eq(OauthTranCode::getType, type);
        List<OauthTranCode> list = oauthTranCodeMapper.selectList(lambdaQueryWrapper);
        if(!list.isEmpty() && list.size() > 0){
            return list.get(0).getLocalId();
        }
        return null;
    }

    @Override
    public String queryTranCodeByLocalId(Long localId, String type) {
        LambdaQueryWrapper<OauthTranCode> lambdaQueryWrapper = new QueryWrapper<OauthTranCode>().lambda();
        lambdaQueryWrapper.eq(OauthTranCode::getLocalId, localId);
        lambdaQueryWrapper.eq(OauthTranCode::getType, type);
        List<OauthTranCode> list = oauthTranCodeMapper.selectList(lambdaQueryWrapper);
        if(!list.isEmpty() && list.size() > 0){
            return list.get(0).getCaCode();
        }
        return null;
    }

    @Override
    public void insertOauthTranCode(OauthTranCode oauthTranCode) {
        oauthTranCodeMapper.insert(oauthTranCode);
    }

    @Override
    public void deleteOauthTranCodeByLocalId(Long localId, String type) {
        LambdaQueryWrapper<OauthTranCode> lambdaQueryWrapper = new QueryWrapper<OauthTranCode>().lambda();
        lambdaQueryWrapper.eq(OauthTranCode::getLocalId, localId);
        lambdaQueryWrapper.eq(OauthTranCode::getType, type);
        oauthTranCodeMapper.delete(lambdaQueryWrapper);
    }

    @Override
    public void deleteOauthTranCodeByCaCode(String caCode, String type) {
        LambdaQueryWrapper<OauthTranCode> lambdaQueryWrapper = new QueryWrapper<OauthTranCode>().lambda();
        lambdaQueryWrapper.eq(OauthTranCode::getCaCode, caCode);
        lambdaQueryWrapper.eq(OauthTranCode::getType, type);
        oauthTranCodeMapper.delete(lambdaQueryWrapper);
    }
}