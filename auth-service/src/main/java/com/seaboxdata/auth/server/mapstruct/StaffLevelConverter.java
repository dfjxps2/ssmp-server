package com.seaboxdata.auth.server.mapstruct;


import com.seaboxdata.auth.api.dto.StaffLevelDTO;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.StaffLevelVo;
import com.seaboxdata.auth.server.model.StaffLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        imports = {LocalDateTime.class, UserUtils.class})
public interface StaffLevelConverter {

    @Mapping(target = "modifier", ignore = true)
    StaffLevelVo toStaffLevelVo(StaffLevel staffLevel);

    @Mapping(target = "modifier", expression = "java(UserUtils.getUserDetails().getUserId())")
    @Mapping(target = "modifyTime", expression = "java(LocalDateTime.now())")
    StaffLevel toStaffLevel(StaffLevelDTO staffLevelDTO);

}
