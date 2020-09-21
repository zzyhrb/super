package com.newgrand.superviseanalysis.system.user.service;

import com.newgrand.superviseanalysis.system.user.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.newgrand.superviseanalysis.system.user.entity.SysUserRole;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 王雷
 * @since 2020-03-23
 */
public interface IUserService extends IService<SysUser> {
    List<SysUserRole> listWithRoleName(SysUserRole sysUserRole);
}
