package com.newgrand.superviseanalysis.system.authority.service.impl;

import com.newgrand.superviseanalysis.system.authority.entity.SysRoleMenu;
import com.newgrand.superviseanalysis.system.authority.mapper.AuthorityMapper;
import com.newgrand.superviseanalysis.system.authority.service.IAuthorityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.newgrand.superviseanalysis.system.menu.entity.SysMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 王雷
 * @since 2020-04-02
 */
@Service
public class AuthorityServiceImpl extends ServiceImpl<AuthorityMapper, SysRoleMenu> implements IAuthorityService {
    private AuthorityMapper authorityMapper;

    @Autowired
    public AuthorityServiceImpl(AuthorityMapper authorityMapper) {
        this.authorityMapper = authorityMapper;
    }

    @Override
    public List<SysMenu> listWithRoleMenu(Integer roleId) {
        return authorityMapper.listWithRoleMenu(roleId);
    }

    @Override
    public List<SysMenu> quickAccessList(Integer roleId) {
        return authorityMapper.quickAccessList(roleId);
    }
}
