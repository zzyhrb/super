package com.newgrand.superviseanalysis.system.user.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SysUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名称
     */
    private String userRealName;

    /**
     * 登录密码
     */
    private String userPassword;

    /**
     * 用户头像
     */
    private String userImage;

    /**
     * 用户代码
     */
    private String userCode;

    /**
     * 注册时间
     */
    private Date userModified;

    /**
     * 是否删除{0:正常,-1:删除,1:停用}
     */
    private Integer isDelete = 0;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 组织名称
     */
    private String organName;
}
