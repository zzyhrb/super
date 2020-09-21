package com.newgrand.superviseanalysis.system.project.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectOrganUserVO {
    /**
     * 项目id
     */
    private Integer projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目描述
     */
    private String projectDescript;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 组织名称
     */
    private String organName;

    /**
     * 用户名称
     */
    private String userRealName;

    /**
     * 组织类别名称
     */
    private String typeName;

    /**
     * 组织id
     */
    private String organId;

    /**
     * 是否删除{0:正常,-1:删除,1:停用}
     */
    private Integer isDelete;

}
