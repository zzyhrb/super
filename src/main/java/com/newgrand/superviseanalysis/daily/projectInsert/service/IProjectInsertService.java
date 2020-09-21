package com.newgrand.superviseanalysis.daily.projectInsert.service;

import java.util.List;
import java.util.Map;

/**
 * @author tly
 * @version 1.0.0
 * @ClassName ProjectInsertService.java
 * @Description TODO
 * @createTime 2020年04月18日 10:16:00
 */
public interface IProjectInsertService {
    List<Map<String, Object>> list(String tableName, Integer userId,Map<String,Object> map);
}
