package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.server.model.OauthTranCode;

public interface OauthTranCodeService {

    Long queryLocalIdByTranCode(String caCode, String type);

    String queryTranCodeByLocalId(Long localId, String type);

    void insertOauthTranCode(OauthTranCode oauthTranCode);

    void deleteOauthTranCodeByLocalId(Long localId, String type);

    void deleteOauthTranCodeByCaCode(String caCode, String type);

}
