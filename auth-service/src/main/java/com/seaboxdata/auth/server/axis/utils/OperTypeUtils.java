package com.seaboxdata.auth.server.axis.utils;

import lombok.experimental.UtilityClass;

//@UtilityClass
public class OperTypeUtils {

    public static final String UP_CODE = "0";
    public static final String OrganizationAddress = "CA经信局数据同步";

    /**
     * 中间表类型
     */

    public static final String TRAN_USER = "user";
    public static final String TRAN_ROLE = "role";
    public static final String TRAN_DEPT = "dept";


    /**
     * 集成方触发同步数据类型
     */

    public static final int CREATEUSER = 11;//创建用户
    public static final int UPDATEUSER = 12;//修改用户
    public static final int DELETEUSER = 13;//删除用户

    public static final int CREATEROLE = 21;//创建角色
    public static final int UPDATEROLE = 22;//修改角色
    public static final int DELETEROLE = 23;//删除角色

    public static final int CREATEDEPT = 41;//创建机构
    public static final int UPDATEDEPT = 42;//修改机构
    public static final int DELETEDEPT = 43;//删除机构

    public static final String CA_RQUEST_SUCCESS = "000";

    //    1．增加组织
    //101：增加组织唯一标识重复
    //102：增加组织的上级节点不存在
    //103：增加组织时同级组织名称重复
    public static final String DEPT_ADD_CODE_REP = "101";
    public static final String DEPT_ADD_NODE_NON = "102";
    public static final String DEPT_ADD_NAME_REP = "103";


    //2．删除组织
    //201：组织唯一标识对应的组织不存在
    //202：删除的组织下存在子组织
    //203：删除的组织下存在用户
    //204：删除的组织下存在角色
    public static final String DEPT_DEL_CODE_NON = "201";
    public static final String DEPT_DEL_EXIST_NODE = "202";
    public static final String DEPT_DEL_EXIST_USER = "203";
    public static final String DEPT_DEL_EXIST_ROLE = "204";


    //3．修改组织
    //301：组织唯一标识对应的组织不存在
    //302：修改组织的上级节点不存在
    //303：修改组织时同级组织名称重复
    public static final String DEPT_UPDATE_CODE_NON = "301";
    public static final String DEPT_UPDATE_UP_NON = "302";
    public static final String DEPT_UPDATE_NAME_REP = "303";

    //4．增加用户
    //401：增加用户唯一标识重复
    //402：增加用户的角色标识编号不存在
    //403：增加用户的组织标识编号不存在
    public static final String USER_ADD_CODE_REP = "401";
    public static final String USER_ADD_ROLE_NON = "402";
    public static final String USER_ADD_DEPT_NON = "403";


    //5．修改用户
    //501：用户唯一标识对应的用户不存在
    //502：修改用户的角色标识编号不存在
    //503：修改用户的组织标识编号不存在
    public static final String USER_UPDATE_CODE_NON = "501";
    public static final String USER_UPDATE_ROLE_NON = "502";
    public static final String USER_UPDATE_DEPT_NON = "503";


    //6．删除用户
    //601：用户唯一标识对应的用户不存在
    public static final String USER_DEL_CODE_NON = "601";

    //7．增加角色
    //701：增加角色唯一标识重复
    //702：增加角色名字重复
    //703：增加角色的组织标识编号不存在
    public static final String ROLE_ADD_CODE_REP = "701";
    public static final String ROLE_ADD_NAME_REP = "702";
    public static final String ROLE_ADD_CODE_NON = "703";


    //8．修改角色
    //801：唯一标识对应的角色不存在
    //802：修改角色名字重复
    //803：修改角色的组织标识编号不存在
    public static final String ROLE_UPDATE_CODE_NON = "801";
    public static final String ROLE_UPDATE_NAME_REP = "802";
    public static final String ROLE_UPDATE_DEPT_NON = "803";


    //9．删除角色
    //901：唯一标识对应的角色不存在
    public static final String ROLE_DEL_CODE_NON = "901";


    //10其他
    //1001：格式错误
    //-1002：其他错误
    public static final String FORMAT_ERROR_CODE = "1001";
    public static final String OTHER_ERROR_CODE = "-1002";
}
