package com.newgrand.superviseanalysis.system.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTable;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectTableModel;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;

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
public interface IProjectTableService extends IService<PmProjectTable> {

    boolean create(String tableName);

    boolean save(String tableName,PmProjectTableModel pmProjectTableModel);

    boolean update(String tableName,PmProjectTableModel pmProjectTableModel);

    List<String> selectTableNamesByOrganId(String organId);

    Integer count(PmProjectTable pmProjectTable,String createTime);

    Integer del(PmProjectTable pmProjectTable, String applyFlag);

    List<Map<String, Object>> getApplyList(List<String> tableNames, SysUser user);

    void setIsThrow(String tableName, String applyFlag, int i);
}
