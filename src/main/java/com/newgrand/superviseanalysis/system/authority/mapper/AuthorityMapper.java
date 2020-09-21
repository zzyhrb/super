package com.newgrand.superviseanalysis.system.authority.mapper;

import com.newgrand.superviseanalysis.system.authority.entity.SysRoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.superviseanalysis.system.menu.entity.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 王雷
 * @since 2020-04-02
 */
public interface AuthorityMapper extends BaseMapper<SysRoleMenu> {
    @Select("SELECT m.id, m.parent_id, m.title, m.url, m.icon, m.sort, m.is_delete FROM sys_role_menu rm INNER JOIN sys_menu m ON rm.menu_id = m.id WHERE rm.is_delete = 0 AND m.is_delete = 0 AND rm.role_id = #{roleId} ORDER BY m.parent_id,m.sort")
    List<SysMenu> listWithRoleMenu(@Param("roleId") Integer roleId);

    @Select("SELECT title,url,icon FROM sys_role_menu rm INNER JOIN sys_menu m ON rm.menu_id = m.id WHERE rm.is_delete = 0 AND m.is_delete = 0 AND rm.role_id = ${roleId} AND url is not null AND url != '' AND title !='控制台'  ORDER BY m.parent_id,m.sort")
    List<SysMenu> quickAccessList(@Param("roleId") Integer roleId);
}
