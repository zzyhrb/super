package com.newgrand.superviseanalysis.system.organ.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.superviseanalysis.system.organ.entity.SysOrgan;
import com.newgrand.superviseanalysis.system.organ.vo.OrganTypeVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */

public interface OrganMapper extends BaseMapper<SysOrgan> {

    @Select("SELECT max(organ_id) FROM sys_organ WHERE parent_id = #{parentId}")
    String getMaxTreeId(@Param("parentId") String parentId);

    @Select("SELECT organ_name FROM sys_organ WHERE is_delete = 0 AND organ_id = #{sysOrgan.organId} or (is_delete = 0 AND parent_id like '${sysOrgan.organId}%')")
    List<String> listObjs(@Param("sysOrgan") SysOrgan sysOrgan);

    @Select({
            "SELECT organ_name,create_time,so.organ_order,so.organ_remark,type_name FROM sys_organ so LEFT JOIN sys_organ_type sot ON so.type_id = sot.type_id  AND sot.is_delete = so.is_delete  " ,
            " WHERE so.is_delete = 0 AND organ_id = #{organId}"
    })
    OrganTypeVO getOrganTypeById(String organId);
}
