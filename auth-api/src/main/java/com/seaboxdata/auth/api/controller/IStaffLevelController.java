package com.seaboxdata.auth.api.controller;


import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.StaffLevelDTO;
import com.seaboxdata.auth.api.vo.StaffLevelVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "StaffLevelController",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface IStaffLevelController {

    /**
     * 更新等级信息
     *
     * @param staffLevelDTO
     * @return
     */
    @PutMapping("/staffLevel/update")
    Boolean updateById(@RequestBody StaffLevelDTO staffLevelDTO);

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/staffLevel/{id}")
    Boolean deleteById(@PathVariable("id") Long id);

    /**
     * 获取等级列表
     *
     * @param searchKey
     * @return
     */
    @GetMapping("/staffLevel/list/{searchKey}")
    List<StaffLevelVo> getStaffLevelList(@PathVariable(name = "searchKey", required = false) String searchKey);

    /**
     * 保存单个等级信息
     *
     * @param staffLevelDTO
     * @return
     */
    @PostMapping("/staffLevel/save")
    Boolean saveStaffLevel(@RequestBody StaffLevelDTO staffLevelDTO);

    /**
     * 根据id获取员工等级信息
     *
     * @param id
     * @return
     */
    @GetMapping("/staffLevel/get/{id}")
    StaffLevelVo getStaffLevelById(@PathVariable("id") Long id);
}
