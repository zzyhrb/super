package com.newgrand.superviseanalysis.system.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTree;
import com.newgrand.superviseanalysis.system.project.mapper.ProjectTreeMapper;
import com.newgrand.superviseanalysis.system.project.service.IProjectTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */
@Service
public class ProjectTreeServiceImpl extends ServiceImpl<ProjectTreeMapper, PmProjectTree> implements IProjectTreeService {
    @Autowired
    private ProjectTreeMapper projectTreeMapper;
    @Override
    public String getTreeId(String parentId) {
        String maxTreeId = projectTreeMapper.getMaxTreeId(parentId);
        if("".equals(maxTreeId) || null == maxTreeId){
            return parentId+"01";
        }else{
            maxTreeId = String.valueOf(BigInteger.valueOf(Long.parseLong(maxTreeId)).add(BigInteger.valueOf(1)));
            return maxTreeId.length() % 2 ==0?maxTreeId:"0"+maxTreeId;
        }

    }

    @Override
    public void copyProjectTree(PmProjectTree projectTree, String copyProjectId) {
        projectTreeMapper.copyProjectTree(projectTree, copyProjectId);
    }
}
