package com.newgrand.superviseanalysis.decision.manage.service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-29
 */
public interface IManageDataService {
    List<Map<String, Object>> dynamicColumn(Map<String, Object> map);

    List<Map<String, Object>> dynamicColumnData(Map<String, Object> map);

    List<Map<String, Object>> dynamicColumnDetailData(Map<String, Object> map);
}
