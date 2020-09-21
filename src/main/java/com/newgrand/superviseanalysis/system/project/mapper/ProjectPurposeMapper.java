package com.newgrand.superviseanalysis.system.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectPurpose;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 田霖禹
 * @since 2020-04-20
 */
public interface ProjectPurposeMapper extends BaseMapper<PmProjectPurpose> {

    @Select("SELECT dict_name,dict_descript,create_time,[value] FROM pm_project_purpose LEFT JOIN sys_dict ON pm_project_purpose.dict_id = sys_dict.dict_id WHERE detail_id  = #{detailId} ORDER BY create_time")
    List<Map<String, Object>> getlistByDetailId(String detailId);
}
