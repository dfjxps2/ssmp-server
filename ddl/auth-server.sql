create schema auth_server_${TARGET_ENV} collate utf8_general_ci;
use auth_server_${TARGET_ENV};

CREATE TABLE `app_data_type`
(
    `id`             bigint       NOT NULL COMMENT '主键id',
    `data_type_name` varchar(60)  NOT NULL COMMENT '应用数据类型分类',
    `url`            varchar(255) NOT NULL DEFAULT '' COMMENT '应用数据类型url',
    `jump_mode`      varchar(60)  NOT NULL COMMENT '跳转类型',
    `parent_id`      bigint       NOT NULL COMMENT '级别父Id',
    `level`          int(11)      NOT NULL COMMENT 'level级别',
    `order_number`   int(11)      NOT NULL COMMENT '序号',
    `create_time`    datetime(3)  NOT NULL COMMENT '创建时间',
    `update_time`    datetime(3)  NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) COMMENT ='数据应用表';

CREATE TABLE `oauth_tenant`
(
    `id`             bigint       NOT NULL COMMENT '主键Id',
    `tenant_name`    varchar(60)  NOT NULL COMMENT '租户名称',
    `tenant_code`    varchar(255) NOT NULL DEFAULT '' COMMENT '租户编码',
    `tenant_desc`    varchar(255) NOT NULL DEFAULT '' COMMENT '租户详情',
    `status`         tinyint(3)   NOT NULL DEFAULT '0' COMMENT '状态 0:启用 1:未启用',
    `user_id`        bigint       NOT NULL COMMENT '用户Id',
    `tenant_code_id` bigint       NOT NULL DEFAULT '0' COMMENT '租户级别Id',
    `create_time`    datetime(3)  NOT NULL COMMENT '创建时间',
    `update_time`    datetime(3)  NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) COMMENT ='租户信息表';

CREATE TABLE `oauth_permission`
(
    `id`              bigint       NOT NULL COMMENT '主键Id',
    `permission_name` varchar(100) NOT NULL COMMENT '权限名称',
    `permission_code` varchar(50)  NOT NULL COMMENT '权限码',
    `parent_id`       bigint       NOT NULL COMMENT '父Id',
    `app_name`        varchar(60)  NOT NULL COMMENT '应用名称',
    `description`     varchar(500) NOT NULL COMMENT '资源详情',
    `create_time`     datetime(3)  NOT NULL COMMENT '创建时间',
    `update_time`     datetime(3)  NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `permission_name` (`permission_name`) USING BTREE COMMENT '权限名称'
) COMMENT ='资源许可表';

CREATE TABLE `oauth_role`
(
    `id`          bigint        NOT NULL,
    `role_name`   varchar(50)   NOT NULL COMMENT '角色名称',
    `role_code`   varchar(50)   NOT NULL COMMENT '角色码',
    `parent_id`   bigint        NOT NULL DEFAULT '0' COMMENT '父Id',
    `status`      tinyint(3)    NOT NULL COMMENT '可用状态 0：不可用  1：可用',
    `tenant_id`   bigint        NOT NULL COMMENT '租户Id',
    `description` varchar(1000) NOT NULL DEFAULT '' COMMENT '角色描述',
    `create_time` datetime(3)   NOT NULL COMMENT '创建时间',
    `update_time` datetime(3)   NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `role_tenant` (`role_name`, `tenant_id`) USING BTREE,
    KEY `role_name` (`role_name`) USING BTREE COMMENT '角色名称'
) COMMENT ='角色表';

CREATE TABLE `tenant_code`
(
    `id`              bigint       NOT NULL COMMENT '主键Id',
    `tenant_id`       bigint       NOT NULL COMMENT '租户Id',
    `tenant_level_id` bigint       NOT NULL COMMENT '租户级别Id',
    `status`          tinyint(1)   NOT NULL DEFAULT '1' COMMENT '开启状态',
    `activity_code`   varchar(255) NOT NULL DEFAULT '' COMMENT '租户激活码',
    `create_time`     datetime(3)  NOT NULL COMMENT '创建时间',
    `update_time`     datetime(3)  NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) COMMENT ='租户-级别-激活码';

CREATE TABLE `oauth_user`
(
    `id`                  bigint       NOT NULL COMMENT '主键ID',
    `name`                varchar(60)  NOT NULL COMMENT '用户姓名',
    `username`            varchar(60)  NOT NULL COMMENT '登录账号',
    `password`            varchar(60)  NOT NULL COMMENT '密码',
    `phone_number`        varchar(60)  NOT NULL DEFAULT '' COMMENT '主移动电话号码',
    `fixed_phone_number`  varchar(60)  NOT NULL DEFAULT '' COMMENT '主固定电话',
    `fax_number`          varchar(60)  NOT NULL DEFAULT '' COMMENT '主传真号码',
    `tenant_id`           bigint       NOT NULL DEFAULT '0' COMMENT '租户Id',
    `job_number`          varchar(60)  NOT NULL DEFAULT '' COMMENT '工号',
    `email`               varchar(255) NOT NULL DEFAULT '' COMMENT '主电子邮箱',
    `enabled`             tinyint(3)   NOT NULL DEFAULT '0' COMMENT '状态: 0-可用，1-禁用',
    `user_sex`            tinyint(3)   NOT NULL DEFAULT '0' COMMENT '性别: 0-男 ,1-女',
    `user_birthday`       varchar(60)  NOT NULL DEFAULT '' COMMENT '生日',
    `outsourcing_company` varchar(60)  NOT NULL DEFAULT '' COMMENT '外包公司名称',
    `outsourcing_phone`   varchar(60)  NOT NULL DEFAULT '' COMMENT '外包公司电话',
    `position`            varchar(60)  NOT NULL DEFAULT '' COMMENT '职位',
    `title`               varchar(60)  NOT NULL DEFAULT '' COMMENT '职称',
    `user_avatar`         varchar(255) NOT NULL DEFAULT '' COMMENT '头像',
    `user_address`        varchar(255) NOT NULL DEFAULT '' COMMENT '地址',
    `personal_signature`  varchar(255) NOT NULL DEFAULT '' COMMENT '个人介绍',
    `last_login_time`     datetime              DEFAULT NULL COMMENT '最后登录时间',
    `system_user`         tinyint(1)   NOT NULL DEFAULT '0' COMMENT '是否为系统用户',
    `create_time`         datetime(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `update_time`         datetime(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `name` (`name`) USING BTREE
) COMMENT ='用户信息表';

CREATE TABLE `oauth_role_permission`
(
    `id`            bigint      NOT NULL,
    `role_id`       bigint      NOT NULL COMMENT '角色Id',
    `permission_id` bigint      NOT NULL COMMENT '资源许可Id',
    `app_name`      varchar(60) NOT NULL COMMENT '应用名称',
    `create_time`   datetime(3) NOT NULL COMMENT '创建时间',
    `update_time`   datetime(3) NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) COMMENT ='角色-资源许可表';

CREATE TABLE `oauth_user_permission`
(
    `id`              bigint      NOT NULL COMMENT '主键Id',
    `user_id`         bigint      NOT NULL COMMENT '用户Id',
    `permission_id`   bigint      NOT NULL COMMENT '资源许可Id',
    `permission_code` varchar(50) NOT NULL COMMENT '权限码',
    `tenant_id`       bigint      NOT NULL COMMENT '租户Id',
    `create_time`     datetime(3) NOT NULL COMMENT '创建时间',
    `update_time`     datetime(3) NOT NULL COMMENT '修改时间',
    `status`          int(11)     NOT NULL COMMENT '0:增 1:减',
    `operator_id`     bigint      NOT NULL COMMENT '操作人Id',
    PRIMARY KEY (`id`) USING BTREE
) COMMENT ='用户-资源许可表';

CREATE TABLE `oauth_user_role`
(
    `id`          bigint      NOT NULL COMMENT '主键id',
    `user_id`     bigint      NOT NULL COMMENT '用户id',
    `role_id`     bigint      NOT NULL COMMENT '角色id',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    `update_time` datetime(3) NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) COMMENT ='用户-角色表';

CREATE TABLE `platform_code`
(
    `id`               bigint       NOT NULL COMMENT '主键Id',
    `user_id`          bigint       NOT NULL COMMENT '平台用户Id',
    `tenant_use_count` int(11)      NOT NULL COMMENT '平台可建租户数',
    `status`           tinyint(1)   NOT NULL COMMENT '状态',
    `activity_code`    varchar(255) NOT NULL DEFAULT '' COMMENT '平台激活码',
    `times_tamp`       bigint       NOT NULL COMMENT '时间戳',
    `create_time`      datetime(3)  NOT NULL COMMENT '创建时间',
    `update_time`      datetime(3)  NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) COMMENT ='平台-激活码';

CREATE TABLE `oauth_group`
(
    `id`          bigint       NOT NULL COMMENT '主键ID',
    `group_name`  varchar(60)  NOT NULL COMMENT '分组名称',
    `group_desc`  varchar(255) NOT NULL COMMENT '分组描述',
    `user_id`     bigint       NOT NULL COMMENT '负责人ID',
    `tenant_id`   bigint       NOT NULL COMMENT '租户Id',
    `create_time` datetime(3)  NOT NULL COMMENT '创建时间',
    `update_time` datetime(3)  NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) COMMENT ='分组管理';


CREATE TABLE `oauth_organization`
(
    `id`                   bigint      NOT NULL COMMENT '主键ID',
    `parent_id`            bigint      NOT NULL COMMENT '父Id',
    `tenant_id`            bigint      NOT NULL COMMENT '租户Id',
    `organization_name`    varchar(60) NOT NULL COMMENT '机构名称',
    `organization_code`    varchar(60) NOT NULL DEFAULT '' COMMENT '机构编码',
    `organization_address` varchar(60) NOT NULL COMMENT '机构地址',
    `organization_number`  bigint      NOT NULL DEFAULT '0' COMMENT '机构编号',
    `manager_user_id`      bigint      NOT NULL COMMENT '负责人用户Id',
    `level`                int(11)     NOT NULL DEFAULT '0' COMMENT '机构层级等级',
    `create_time`          datetime(3) NOT NULL COMMENT '创建时间',
    `update_time`          datetime(3) NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) COMMENT ='机构管理';

CREATE TABLE `oauth_user_info`
(
    `id`          bigint       NOT NULL COMMENT '主键ID',
    `contact`     varchar(20)  NOT NULL COMMENT '联系类型',
    `information` varchar(60)  NOT NULL COMMENT '联系信息',
    `user_id`     bigint       NOT NULL COMMENT '用户Id',
    `info_desc`   varchar(255) NOT NULL DEFAULT '' COMMENT '联系方式描述',
    `is_primary`  tinyint(1)   NOT NULL COMMENT '0:主联系方式 1:次级联系方式',
    `create_time` datetime(3)  NOT NULL COMMENT '创建时间',
    `update_time` datetime(3)  NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) COMMENT ='用户信息-扩展表';

CREATE TABLE `oauth_system`
(
    `app_name`    varchar(60)  NOT NULL COMMENT '系统名称',
    `system_name` varchar(20)  NOT NULL COMMENT '系统名称',
    `system_desc` varchar(255) NOT NULL COMMENT '系统描述',
    `create_time` datetime(3)  NOT NULL COMMENT '创建时间',
    `update_time` datetime(3)  NOT NULL COMMENT '修改时间'
) COMMENT ='系统表';


CREATE TABLE `oauth_user_group`
(
    `id`          bigint      NOT NULL COMMENT '主键Id',
    `user_id`     bigint      NOT NULL COMMENT '用户Id',
    `group_id`    bigint      NOT NULL COMMENT '分组Id',
    `tenant_id`   bigint      NOT NULL COMMENT '租户Id',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    `update_time` datetime(3) NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) COMMENT ='用户-分组中间表';

CREATE TABLE `oauth_user_organization`
(
    `id`              bigint      NOT NULL COMMENT '主键Id',
    `user_id`         bigint      NOT NULL COMMENT '用户Id',
    `organization_id` bigint      NOT NULL COMMENT '机构Id',
    `tenant_id`       bigint      NOT NULL COMMENT '租户Id',
    `create_time`     datetime(3) NOT NULL COMMENT '创建时间',
    `update_time`     datetime(3) NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) COMMENT ='用户-机构表';

### 这个表的字段作用待仔细研究
create table `oauth_client_details`
(
    `client_id`               varchar(128) not null,
    `resource_ids`            text,
    `client_secret`           text,
    `scope`                   text,
    `authorized_grant_types`  text,
    `web_server_redirect_uri` text,
    `authorities`             text,
    `access_token_validity`   int default null,
    `refresh_token_validity`  int default null,
    `additional_information`  text,
    `autoapprove`             text,
    primary key (`client_id`)
) comment '第三方应用表';

CREATE TABLE `tenant_level`
(
    `id`           bigint       NOT NULL COMMENT '主键Id',
    `user_count`   int(11)      NOT NULL COMMENT '容纳人数',
    `tenant_level` int(11)      NOT NULL COMMENT '租户级别',
    `description`  varchar(255) NOT NULL DEFAULT '' COMMENT '描述',
    `status`       tinyint(1)   NOT NULL COMMENT '开启状态',
    `create_time`  datetime(3)  NOT NULL COMMENT '创建时间',
    `update_time`  datetime(3)  NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) COMMENT ='租户级别表';

CREATE TABLE `oauth_tenant_info`
(
    `id`               bigint      NOT NULL COMMENT '主键Id',
    `tenant_id`        bigint      NOT NULL COMMENT '租户Id',
    `virtual_currency` bigint      NOT NULL DEFAULT '0' COMMENT '虚拟货币：海贝值',
    `creator`          bigint      NOT NULL COMMENT '记录创建者',
    `drd_manager`      tinyint(1)  NOT NULL DEFAULT '0' COMMENT '是否为资源目录负责租户',
    `create_time`      datetime(3) NOT NULL COMMENT '创建时间',
    `update_time`      datetime(3) NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `tenant` (`tenant_id`)
) COMMENT ='租户额外信息表';