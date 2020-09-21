package com.newgrand.superviseanalysis.system.dict.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.newgrand.superviseanalysis.system.dict.entity.SysDict;
import com.newgrand.superviseanalysis.system.dict.mapper.DictMapper;
import com.newgrand.superviseanalysis.system.dict.service.IDictService;
import com.newgrand.superviseanalysis.system.dict.vo.DictOrganUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-04-01
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, SysDict> implements IDictService {
    private DictMapper dictMapper;

    @Autowired
    public DictServiceImpl(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    @Override
    public List<DictOrganUserVo> listWithOrganNameAndUserRealName(DictOrganUserVo dictOrganUserVo) {
        return dictMapper.listWithOrganNameAndUserRealName(dictOrganUserVo);
    }
}
