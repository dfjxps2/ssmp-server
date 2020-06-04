package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.netflix.discovery.converters.Auto;
import com.seaboxdata.auth.api.dto.StaffLevelDTO;
import com.seaboxdata.auth.server.dao.OauthUserMapper;
import com.seaboxdata.auth.server.dao.StaffLevelMapper;
import com.seaboxdata.auth.server.model.OauthUser;
import com.seaboxdata.auth.server.model.StaffLevel;
import com.seaboxdata.auth.server.service.StaffLevelService;
import com.seaboxdata.commons.exception.ServiceException;
import com.seaboxdata.commons.mybatis.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * (StaffLevel)表服务实现类
 *
 * @author makejava
 * @since 2020-03-30 11:20:30
 */
@Service("staffLevelService")
public class StaffLevelServiceImpl extends ServiceImpl<StaffLevelMapper, StaffLevel> implements StaffLevelService {

    private OauthUserMapper oauthUserMapper;

    private StaffLevelMapper staffLevelMapper;

    @Override
    public Boolean deleteStaffLevelById(Long id) {
        if (Objects.nonNull(id)) {
            // 员工等级未被使用
            if (Objects.isNull(oauthUserMapper.selectOne(Wrappers.lambdaQuery(new OauthUser()).eq(OauthUser::getStaffLevelId, id)))) {
                return removeById(id);
            } else {
                throw new ServiceException("400", "员工等级已被占用，无法删除");
            }
        }
        return false;
    }

    @Autowired
    public StaffLevelServiceImpl(OauthUserMapper oauthUserMapper) {
        this.oauthUserMapper = oauthUserMapper;
    }

    @Override
    public void checkoutStaffLevelRepeat(StaffLevelDTO dto)
    {
        LambdaQueryWrapper<StaffLevel> wrapper = new QueryWrapper<StaffLevel>().lambda()
                .eq(StaffLevel::getLevelName,dto.getLevelName());
        StaffLevel row = this.getOne(wrapper,false);
//        StaffLevel row = MapperUtils.getOne(staffLevelMapper,wrapper,false);
        if(Objects.nonNull(row))
        {
            throw new ServiceException("400","该员工等级已经存在！");
        }
    }
}