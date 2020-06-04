package com.seaboxdata.auth.server.cas.service;

import com.seaboxdata.auth.server.model.OauthUser;

public interface ISynchDataService {

    Boolean checkLoginByCasUsername(String username);

    void synchOrgnize(OauthUser adminUser);

    void synchUsers(OauthUser adminUser);

    void synchData();
}
