package com.newgrand.superviseanalysis.system.role.vo;

import lombok.Data;

/**
 * @author tly
 * @version 1.0.0
 * @ClassName RoleOrganUser.java
 * @Description TODO
 * @createTime 2020年04月03日 11:41:00
 */
@Data
public class RoleOrganUserVo {
    private Long userId;
    private String userRealName;
    private String organName;
    private String roleName;
}
