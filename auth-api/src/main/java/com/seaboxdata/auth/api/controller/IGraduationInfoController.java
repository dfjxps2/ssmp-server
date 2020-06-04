package com.seaboxdata.auth.api.controller;


import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.GraduationInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "GraduationInfoController",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface IGraduationInfoController {

    @PutMapping("/graduationInfo/update")
    Boolean updateById(@RequestBody GraduationInfoDTO graduationInfoDTO);

    @DeleteMapping("/graduationInfo/{id}")
    Boolean deleteById(@PathVariable("id") Long id);

    @PostMapping("/graduationInfo/save")
    Long save(@RequestBody GraduationInfoDTO graduationInfoDTO);
}
