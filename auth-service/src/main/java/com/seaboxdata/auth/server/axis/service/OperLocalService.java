package com.seaboxdata.auth.server.axis.service;

public interface OperLocalService {

    String addOrg(String orgCode);

    String addRole(String roleCode);

    String addUser(String userCode);

    String delOrg(String orgCode);

    String delRole(String roleCode);

    String delUser(String userCode);

    String updateOrg(String orgCode);

    String updateRole(String roleCode);

    String updateUser(String userCode);
}
