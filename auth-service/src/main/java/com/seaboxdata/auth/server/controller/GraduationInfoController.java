package com.seaboxdata.auth.server.controller;


import com.seaboxdata.auth.api.controller.IGraduationInfoController;
import com.seaboxdata.auth.api.dto.GraduationInfoDTO;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.server.mapstruct.GraduationConverter;
import com.seaboxdata.auth.server.model.GraduationInfo;
import com.seaboxdata.auth.server.service.GraduationInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class GraduationInfoController implements IGraduationInfoController {


    private GraduationInfoService graduationInfoService;

    private GraduationConverter graduationConverter;

    @Override
    public Boolean updateById(GraduationInfoDTO graduationInfoDTO) {
        if (Objects.nonNull(graduationInfoDTO)) {
            return graduationInfoService.updateById(graduationConverter.toGraduationInfo(graduationInfoDTO));
        }
        return false;
    }

    @Override
    public Boolean deleteById(Long id) {
        if (Objects.nonNull(id)) {
            return graduationInfoService.removeById(id);
        }
        return false;
    }

    @Override
    public Long save(GraduationInfoDTO graduationInfoDTO) {
        if (Objects.nonNull(graduationInfoDTO)) {
            GraduationInfo graduationInfo = graduationConverter.toGraduationInfo(graduationInfoDTO).setCreator(UserUtils.getUserDetails().getUserId())
                    .setCreateTime(LocalDateTime.now());
            graduationInfoService.save(graduationInfo);
            return graduationInfo.getId();
        }
        return null;
    }

    @Autowired
    public GraduationInfoController(GraduationInfoService graduationInfoService,
                                    GraduationConverter graduationConverter) {
        this.graduationInfoService = graduationInfoService;
        this.graduationConverter = graduationConverter;
    }
}
