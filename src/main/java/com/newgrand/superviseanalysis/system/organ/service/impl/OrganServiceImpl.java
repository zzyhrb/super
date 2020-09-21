package com.newgrand.superviseanalysis.system.organ.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.newgrand.superviseanalysis.system.organ.entity.SysOrgan;
import com.newgrand.superviseanalysis.system.organ.mapper.OrganMapper;
import com.newgrand.superviseanalysis.system.organ.service.IOrganService;
import com.newgrand.superviseanalysis.system.organ.vo.OrganTypeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */
@Service
public class OrganServiceImpl extends ServiceImpl<OrganMapper, SysOrgan> implements IOrganService {
    @Autowired
    private OrganMapper organMapper;
    @Override
    public String getTreeId(String parentId) {
        String maxTreeId = organMapper.getMaxTreeId(parentId);
        if("".equals(maxTreeId) || null == maxTreeId){
            return parentId+"01";
        }else{
            maxTreeId = String.valueOf(Integer.parseInt(maxTreeId)+1);
            return maxTreeId.length() % 2 ==0?maxTreeId:"0"+maxTreeId;
        }

    }

    @Override
    public List<String> listObjs(SysOrgan sysOrgan) {
        return organMapper.listObjs(sysOrgan);
    }

    @Override
    public OrganTypeVO getOrganTypeById(String organId) {
        return organMapper.getOrganTypeById(organId);
    }
}
