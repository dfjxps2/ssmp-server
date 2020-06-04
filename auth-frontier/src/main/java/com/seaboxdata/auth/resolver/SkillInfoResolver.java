package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.seaboxdata.auth.api.controller.ISkillInfoController;
import com.seaboxdata.auth.api.dto.SkillInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ：long
 * @date ：Created in 2020/4/1 下午12:28
 * @description：
 */

@Component
public class SkillInfoResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private final ISkillInfoController skillInfoController;

    /**
     * 保存
     *
     * @param skillInfoDTO
     * @return
     */
    public Long saveSkillInfo(SkillInfoDTO skillInfoDTO) {
        return skillInfoController.save(skillInfoDTO);
    }

    /**
     * 更新
     *
     * @param skillInfoDTO
     * @return
     */
    public Boolean updateSkillInfo(SkillInfoDTO skillInfoDTO) {
        return skillInfoController.updateById(skillInfoDTO);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public Boolean deleteSkillInfo(Long id) {
        return skillInfoController.deleteById(id);
    }

    @Autowired
    public SkillInfoResolver(ISkillInfoController skillInfoController) {
        this.skillInfoController = skillInfoController;
    }
}
