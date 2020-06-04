package com.seaboxdata.auth.server.mapstruct;


import com.seaboxdata.auth.api.dto.GraduationInfoDTO;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.GraduationInfoVo;
import com.seaboxdata.auth.api.vo.OauthJxpmUserVO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.model.GraduationInfo;
import com.seaboxdata.auth.server.model.OauthUser;
import com.seaboxdata.auth.server.utils.LocalDateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring",
        imports = {UserUtils.class, LocalDateTime.class, LocalDateTimeUtil.class})
public interface GraduationConverter {

    @Mapping(target = "modifier", expression = "java(UserUtils.getUserDetails().getUserId())")
    @Mapping(target = "modifyTime", expression = "java(LocalDateTime.now())")
    @Mapping(target = "educationStartTime", expression = "java(LocalDateTimeUtil.parseString(graduationInfoDTO.getEducationStartTime()))")
    @Mapping(target = "graduateTime", expression = "java(LocalDateTimeUtil.parseString(graduationInfoDTO.getGraduateTime()))")
    GraduationInfo toGraduationInfo(GraduationInfoDTO graduationInfoDTO);

    List<GraduationInfo> toGraduationInfoList(List<GraduationInfoDTO> graduationInfoDTOS);

    GraduationInfoVo toGraduationInfoVo(GraduationInfo graduationInfo);

    List<GraduationInfoVo> toGraduationInfoVos(List<GraduationInfo> graduationInfos);

    List<OauthJxpmUserVO> toJxpmUserVos(List<OauthUserVO> userVOS);

}
