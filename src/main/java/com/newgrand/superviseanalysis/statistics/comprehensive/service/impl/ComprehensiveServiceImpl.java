package com.newgrand.superviseanalysis.statistics.comprehensive.service.impl;

import com.newgrand.superviseanalysis.statistics.comprehensive.mapper.ComprehensiveMapper;
import com.newgrand.superviseanalysis.statistics.comprehensive.service.IComprehensiveService;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */
@Service
public class ComprehensiveServiceImpl implements IComprehensiveService {

    private ComprehensiveMapper comprehensiveMapper;

    @Autowired
    public ComprehensiveServiceImpl(ComprehensiveMapper comprehensiveMapper) {
        this.comprehensiveMapper = comprehensiveMapper;
    }

    @Override
    public List<Map<String, Object>> list(PmProjectTable pmProjectTable, Map<String,Object> map ) {
        return comprehensiveMapper.list(pmProjectTable, map);
    }
}
