package com.newgrand.superviseanalysis.system.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.newgrand.superviseanalysis.system.user.entity.SysUserAccount;
import com.newgrand.superviseanalysis.system.user.mapper.UserAccountMapper;
import com.newgrand.superviseanalysis.system.user.service.IUserAccountService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 王雷
 * @since 2020-03-23
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, SysUserAccount> implements IUserAccountService {

}
