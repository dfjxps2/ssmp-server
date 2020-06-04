package com.seaboxdata.auth.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seaboxdata.auth.api.dto.StaffLevelDTO;
import com.seaboxdata.auth.server.model.StaffLevel;

/**
 * (StaffLevel)表服务接口
 *
 * @author makejava
 * @since 2020-03-30 11:20:30
 */
public interface StaffLevelService extends IService<StaffLevel> {


    public Boolean deleteStaffLevelById(Long id);

    void checkoutStaffLevelRepeat(StaffLevelDTO staffLevelDTO);

}