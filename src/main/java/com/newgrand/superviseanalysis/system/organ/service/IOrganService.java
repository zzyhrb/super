package com.newgrand.superviseanalysis.system.organ.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.newgrand.superviseanalysis.system.organ.entity.SysOrgan;
import com.newgrand.superviseanalysis.system.organ.vo.OrganTypeVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-24
 */
public interface IOrganService extends IService<SysOrgan> {

    String getTreeId(String parentId);

    List<String> listObjs(SysOrgan sysOrgan);

    OrganTypeVO getOrganTypeById(String organId);
}
