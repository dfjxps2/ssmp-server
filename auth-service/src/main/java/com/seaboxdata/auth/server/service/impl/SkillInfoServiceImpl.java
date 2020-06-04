package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seaboxdata.auth.server.dao.SkillInfoMapper;
import com.seaboxdata.auth.server.model.SkillInfo;
import com.seaboxdata.auth.server.service.SkillInfoService;
import org.springframework.stereotype.Service;

/**
 * (SkillInfo)表服务实现类
 *
 * @author makejava
 * @since 2020-03-30 11:20:30
 */
@Service("skillInfoService")
public class SkillInfoServiceImpl extends ServiceImpl<SkillInfoMapper, SkillInfo> implements SkillInfoService {

}