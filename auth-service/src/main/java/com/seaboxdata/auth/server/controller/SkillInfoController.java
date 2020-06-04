package com.seaboxdata.auth.server.controller;

import com.seaboxdata.auth.api.controller.ISkillInfoController;
import com.seaboxdata.auth.api.dto.SkillInfoDTO;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.server.mapstruct.SkillInfoConverter;
import com.seaboxdata.auth.server.model.SkillInfo;
import com.seaboxdata.auth.server.service.SkillInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author ：long
 * @date ：Created in 2020/3/31 下午3:47
 * @description：
 */
@Component
public class SkillInfoController implements ISkillInfoController {

    private SkillInfoConverter skillInfoConverter;

    private SkillInfoService skillInfoService;

    @Override
    public Boolean updateById(SkillInfoDTO skillInfoDTO) {
        if (Objects.nonNull(skillInfoDTO)) {
            return skillInfoService.updateById(skillInfoConverter.toSkillInfo(skillInfoDTO));
        }
        return false;
    }

    @Override
    public Boolean deleteById(Long id) {
        if (Objects.nonNull(id)) {
            return skillInfoService.removeById(id);
        }
        return false;
    }

    @Override
    public Long save(SkillInfoDTO skillInfoDTO) {
        if (Objects.nonNull(skillInfoDTO)) {
            SkillInfo skillInfo = skillInfoConverter.toSkillInfo(skillInfoDTO).setCreator(UserUtils.getUserDetails().getUserId())
                    .setCreateTime(LocalDateTime.now());
            skillInfoService.save(skillInfo);
            return skillInfo.getId();
        }
        return null;
    }

    @Autowired
    public SkillInfoController(SkillInfoConverter skillInfoConverter
            , SkillInfoService skillInfoService) {
        this.skillInfoConverter = skillInfoConverter;
        this.skillInfoService = skillInfoService;
    }
}
