package com.newgrand.superviseanalysis.statistics.comprehensive.service;

import com.newgrand.superviseanalysis.system.project.entity.PmProjectTable;

import java.util.List;
import java.util.Map;

/**
 * @author tly
 * @version 1.0.0
 * @ClassName ComprehensiveService.java
 * @Description TODO
 * @createTime 2020年04月24日 10:16:00
 */
public interface IComprehensiveService {
    List<Map<String, Object>> list(PmProjectTable pmProjectTable, Map<String,Object> map );
}
