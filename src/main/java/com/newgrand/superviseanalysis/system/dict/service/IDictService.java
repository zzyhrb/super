package com.newgrand.superviseanalysis.system.dict.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.newgrand.superviseanalysis.system.dict.entity.SysDict;
import com.newgrand.superviseanalysis.system.dict.vo.DictOrganUserVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-04-01
 */
public interface IDictService extends IService<SysDict> {

    List<DictOrganUserVo> listWithOrganNameAndUserRealName(DictOrganUserVo dictOrganUserVo);

}
