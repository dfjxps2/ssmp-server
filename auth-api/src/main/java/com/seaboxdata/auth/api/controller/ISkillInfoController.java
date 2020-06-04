package com.seaboxdata.auth.api.controller;


import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.SkillInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "SkillInfoController",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface ISkillInfoController {

    @PutMapping("/skillInfo/update")
    Boolean updateById(@RequestBody SkillInfoDTO skillInfoDTO);

    @DeleteMapping("/skillInfo/{id}")
    Boolean deleteById(@PathVariable("id") Long id);

    @PostMapping("/skillInfo/save")
    Long save(@RequestBody SkillInfoDTO skillInfoDTO);
}
