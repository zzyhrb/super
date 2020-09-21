package com.newgrand.superviseanalysis.decision.manage.service.impl;

import com.newgrand.superviseanalysis.decision.manage.mapper.ManageDataMapper;
import com.newgrand.superviseanalysis.decision.manage.service.IManageDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author tly
 * @version 1.0.0
 * @ClassName ManageDataServiceImpl.java
 * @Description TODO
 * @createTime 2020年04月08日 10:56:00
 */
@Service
public class ManageDataServiceImpl implements IManageDataService {
    @Autowired
    private ManageDataMapper manageDataMapper;

    @Override
    public List<Map<String, Object>> dynamicColumn(Map<String, Object> map) {
        return manageDataMapper.dynamicColumn(map);
    }

    @Override
    public List<Map<String, Object>> dynamicColumnData(Map<String, Object> map) {
        changeMap(map);
        return manageDataMapper.dynamicColumnData(map);
    }

    private void changeMap(Map<String, Object> map) {
        if ("第一季度".equals(map.get("column"))) {
            map.put("season", "(1,2,3)");
        } else if ("第二季度".equals(map.get("column"))) {
            map.put("season", "(4,5,6)");
        } else if ("第三季度".equals(map.get("column"))) {
            map.put("season", "(7,8,9)");
        } else {
            map.put("season", "(10,11,12)");
        }
    }

    @Override
    public List<Map<String, Object>> dynamicColumnDetailData(Map<String, Object> map) {
        changeMap(map);
        return manageDataMapper.dynamicColumnDetailData(map);
    }
}
