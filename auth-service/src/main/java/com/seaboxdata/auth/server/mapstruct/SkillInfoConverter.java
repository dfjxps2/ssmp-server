package com.seaboxdata.auth.server.mapstruct;


import com.seaboxdata.auth.api.dto.SkillInfoDTO;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.SkillInfoVo;
import com.seaboxdata.auth.server.model.SkillInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring",
        imports = {LocalDateTime.class, UserUtils.class})
public interface SkillInfoConverter {

    @Mapping(target = "modifier", expression = "java(UserUtils.getUserDetails().getUserId())")
    @Mapping(target = "modifyTime", expression = "java(LocalDateTime.now())")
    SkillInfo toSkillInfo(SkillInfoDTO skillInfoDTO);

    List<SkillInfo> toSkillInfoList(List<SkillInfoDTO> skillInfoDTOS);

    SkillInfoVo toSkillInfoVo(SkillInfo skillInfo);

    List<SkillInfoVo> toSkillInfoVos(List<SkillInfo> skillInfos);
}
