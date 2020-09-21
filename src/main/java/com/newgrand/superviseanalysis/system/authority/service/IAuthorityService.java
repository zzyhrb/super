package com.newgrand.superviseanalysis.system.authority.service;

import com.newgrand.superviseanalysis.system.authority.entity.SysRoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.newgrand.superviseanalysis.system.menu.entity.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 王雷
 * @since 2020-04-02
 */
public interface IAuthorityService extends IService<SysRoleMenu> {
    List<SysMenu> listWithRoleMenu(@Param("roleId") Integer roleId);

    List<SysMenu> quickAccessList(Integer roleId);
}
