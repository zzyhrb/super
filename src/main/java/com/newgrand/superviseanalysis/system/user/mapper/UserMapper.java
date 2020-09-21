package com.newgrand.superviseanalysis.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import com.newgrand.superviseanalysis.system.user.entity.SysUserRole;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 王雷
 * @since 2020-03-23
 */
public interface UserMapper extends BaseMapper<SysUser> {
    @Select("SELECT u.user_id, u.user_real_name, u.user_image, u.user_code, (SELECT r.name FROM sys_role r WHERE u.role_id = r.id) name, (SELECT o.organ_name FROM sys_organ o WHERE u.organ_id = o.organ_id) organ_name FROM sys_user u WHERE u.is_delete = 0 or u.is_delete = 1")
    List<SysUserRole> listWithRoleName(SysUserRole sysUserRole);
}
