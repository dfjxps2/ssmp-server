package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.seaboxdata.auth.api.controller.IGraduationInfoController;
import com.seaboxdata.auth.api.dto.GraduationInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ：long
 * @date ：Created in 2020/4/1 下午12:19
 * @description：
 */


@Component
public class GraduationInfoResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private final IGraduationInfoController graduationInfoController;

    /**
     * 更新毕业信息
     *
     * @param graduationInfoDTO
     * @return
     */
    public Boolean updateGraduationInfo(GraduationInfoDTO graduationInfoDTO) {
        return graduationInfoController.updateById(graduationInfoDTO);
    }

    /**
     * 删除一条毕业信息
     *
     * @param id
     * @return
     */
    public Boolean deleteGraduationInfo(Long id) {
        return graduationInfoController.deleteById(id);
    }

    /**
     * 保存
     *
     * @param graduationInfoDTO
     * @return
     */
    public Long saveGraduationInfo(GraduationInfoDTO graduationInfoDTO) {
        return graduationInfoController.save(graduationInfoDTO);
    }

    @Autowired
    public GraduationInfoResolver(IGraduationInfoController graduationInfoController) {
        this.graduationInfoController = graduationInfoController;
    }
}
