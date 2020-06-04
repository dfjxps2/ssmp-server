package com.seaboxdata.auth.server.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @since 2019-05-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ToString(onlyExplicitlyIncluded = true)
public class OauthClientDetails implements Serializable {

    private static final long serialVersionUID = 2800234750600888570L;

    @ToString.Include
    private String clientId;

    private String resourceIds;

    private String clientSecret;

    private String scope;

    @ToString.Include
    private String authorizedGrantTypes;

    private String webServerRedirectUri;

    private String authorities;

    private Integer accessTokenValidity;

    private Integer refreshTokenValidity;

    private String additionalInformation;

    private String autoapprove;

}
