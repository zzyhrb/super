package com.newgrand.superviseanalysis.system.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTree;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */
public interface IProjectTreeService extends IService<PmProjectTree> {
    String getTreeId(String parentId);

    void copyProjectTree(PmProjectTree projectTree, String copyProjectId);
}
