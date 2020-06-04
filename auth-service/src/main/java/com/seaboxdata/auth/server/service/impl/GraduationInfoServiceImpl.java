package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seaboxdata.auth.server.dao.GraduationInfoMapper;
import com.seaboxdata.auth.server.model.GraduationInfo;
import com.seaboxdata.auth.server.service.GraduationInfoService;
import org.springframework.stereotype.Service;

/**
 * (GraduationInfo)表服务实现类
 *
 * @author makejava
 * @since 2020-03-30 11:16:48
 */
@Service("graduationInfoService")
public class GraduationInfoServiceImpl extends ServiceImpl<GraduationInfoMapper, GraduationInfo> implements GraduationInfoService {

}