package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.seaboxdata.auth.api.controller.IStaffLevelController;
import com.seaboxdata.auth.api.dto.StaffLevelDTO;
import com.seaboxdata.auth.api.vo.StaffLevelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ：long
 * @date ：Created in 2020/4/1 下午12:29
 * @description：
 */

@Component
public class StaffLevelResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private IStaffLevelController staffLevelController;


    /**
     * 列表查询
     *
     * @param searchKey
     * @return
     */
    public List<StaffLevelVo> getStaffLevelList(String searchKey) {
        return staffLevelController.getStaffLevelList(searchKey);
    }

    /**
     * 保存
     *
     * @param staffLevelDTO
     * @return
     */
    public Boolean saveStaffLevel(StaffLevelDTO staffLevelDTO) {
        return staffLevelController.saveStaffLevel(staffLevelDTO);
    }

    /**
     * 更新
     *
     * @param staffLevelDTO
     * @return
     */
    public Boolean updateStaffLevel(StaffLevelDTO staffLevelDTO) {
        return staffLevelController.updateById(staffLevelDTO);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public Boolean deleteStaffLevel(Long id) {
        return staffLevelController.deleteById(id);
    }


    @Autowired
    public StaffLevelResolver(IStaffLevelController staffLevelController) {
        this.staffLevelController = staffLevelController;
    }
}
