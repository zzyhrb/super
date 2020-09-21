package com.newgrand.superviseanalysis.system.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.superviseanalysis.system.dict.entity.SysDict;
import com.newgrand.superviseanalysis.system.dict.vo.DictOrganUserVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 田霖禹
 * @since 2020-04-01
 */

public interface DictMapper extends BaseMapper<SysDict> {

    @Select({"<script>",
            " SELECT sd.dict_id,sd.dict_descript,sd.dict_code,sd.create_time,sd.is_delete,sd.dict_order,sd.organ_id,sd.dict_name,sd.user_id,sd.parent_id,so.organ_name,user_real_name FROM  sys_dict sd ",
            "LEFT JOIN sys_organ so ON so.organ_id = sd.organ_id ",
            "LEFT JOIN sys_user su ON su.user_id = sd.user_id ",
            "WHERE sd.is_delete=${dictOrganUserVo.isDelete} AND sd.parent_id=${dictOrganUserVo.parentId} ",
            "<when test='dictOrganUserVo.dictCode!=null and dictOrganUserVo.dictCode !=\"\"'> AND sd.dict_code = #{dictOrganUserVo.dictCode} </when>", // 字典编码
            "<when test='dictOrganUserVo.dictName!=null and dictOrganUserVo.dictName !=\"\"'> AND sd.dict_name LIKE '${dictOrganUserVo.dictName}%' </when>", // 字典名称
            "<when test='dictOrganUserVo.organId!=null and dictOrganUserVo.organId !=\"\"'> AND sd.organ_id LIKE '${dictOrganUserVo.organId}%' </when>", // 组织ID
            "<when test='dictOrganUserVo.userRealName!=null and dictOrganUserVo.userRealName !=\"\"'> AND su.user_real_name LIKE '${dictOrganUserVo.userRealName}%' </when>", // 创建人

            "ORDER BY sd.create_time DESC ",
            "</script>"
    })

    List<DictOrganUserVo> listWithOrganNameAndUserRealName(@Param("dictOrganUserVo") DictOrganUserVo dictOrganUserVo);
}
