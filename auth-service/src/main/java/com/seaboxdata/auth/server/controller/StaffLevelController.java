package com.seaboxdata.auth.server.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seaboxdata.auth.api.controller.IStaffLevelController;
import com.seaboxdata.auth.api.dto.StaffLevelDTO;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.api.vo.StaffLevelVo;
import com.seaboxdata.auth.server.mapstruct.StaffLevelConverter;
import com.seaboxdata.auth.server.model.StaffLevel;
import com.seaboxdata.auth.server.service.OauthUserService;
import com.seaboxdata.auth.server.service.StaffLevelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ：long
 * @date ：Created in 2020/3/31 下午3:47
 * @description：
 */
@Component
public class StaffLevelController implements IStaffLevelController {

    private StaffLevelService staffLevelService;

    private OauthUserService oauthUserService;

    private StaffLevelConverter staffLevelConverter;

    @Override
    public Boolean updateById(StaffLevelDTO staffLevelDTO) {
        if (Objects.nonNull(staffLevelDTO)) {
            return staffLevelService.updateById(staffLevelConverter.toStaffLevel(staffLevelDTO));
        }
        return false;
    }

    @Override
    public Boolean deleteById(Long id) {
        if (Objects.nonNull(id)) {
            return staffLevelService.deleteStaffLevelById(id);
        }
        return false;
    }

    @Override
    @GetMapping({"/staffLevel/list/{searchKey}", "/staffLevel/list"})
    public List<StaffLevelVo> getStaffLevelList(@PathVariable(required = false) String searchKey) {
        if (StringUtils.isNotBlank(searchKey)) {
            return staffLevelService.list(Wrappers.lambdaQuery(new StaffLevel()).like(StaffLevel::getLevelName, searchKey))
                    .stream()
                    .filter(Objects::nonNull)
                    .map(staffLevel -> staffLevelConverter.toStaffLevelVo(staffLevel).setModifier(getUserVoById(staffLevel.getModifier())))
                    .collect(Collectors.toList());
        }
        return staffLevelService.list()
                .stream()
                .filter(Objects::nonNull)
                .map(staffLevel -> staffLevelConverter.toStaffLevelVo(staffLevel).setModifier(getUserVoById(staffLevel.getModifier())))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean saveStaffLevel(StaffLevelDTO staffLevelDTO) {
        /* 验证员工等级是否重复 */
        staffLevelService.checkoutStaffLevelRepeat(staffLevelDTO);
        if (Objects.nonNull(staffLevelDTO)) {
            return staffLevelService.save(
                    staffLevelConverter.toStaffLevel(staffLevelDTO).setCreator(UserUtils.getUserDetails().getUserId())
                            .setCreateTime(LocalDateTime.now()));
        }
        return false;
    }

    @Override
    public StaffLevelVo getStaffLevelById(Long id) {
        if (Objects.nonNull(id)) {
            return staffLevelConverter.toStaffLevelVo(
                    staffLevelService.getOne(Wrappers.lambdaQuery(new StaffLevel()).eq(StaffLevel::getId, id))
            );
        }
        return null;
    }

    /**
     * 获取单个用户
     *
     * @param id
     * @return
     */
    private OauthUserVO getUserVoById(Long id) {
        List<OauthUserVO> oauthUserVOS = oauthUserService.selectUserByUserId(Collections.singletonList(id));
        if (CollectionUtils.isEmpty(oauthUserVOS)) {
            return null;
        }
        return oauthUserVOS.get(0);
    }

    @Autowired
    public StaffLevelController(StaffLevelService staffLevelService,
                                OauthUserService oauthUserService,
                                StaffLevelConverter staffLevelConverter) {
        this.staffLevelService = staffLevelService;
        this.oauthUserService = oauthUserService;
        this.staffLevelConverter = staffLevelConverter;
    }
}
