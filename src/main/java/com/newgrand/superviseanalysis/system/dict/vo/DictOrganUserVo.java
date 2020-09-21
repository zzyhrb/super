package com.newgrand.superviseanalysis.system.dict.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author tly
 * @version 1.0.0
 * @ClassName DictOrganUserVo.java
 * @Description TODO
 * @createTime 2020年05月13日 11:15:00
 */
@Data
public class DictOrganUserVo {
    private static final long serialVersionUID = 1L;

    /**
     * 字典ID
     */
    private Long dictId;

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 排序
     */
    private Integer dictOrder;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人ID
     */
    private Integer userId;

    /**
     * 字典描述
     */
    private String dictDescript;

    /**
     * 组织ID
     */
    private String organId;

    /**
     * 是否删除{0:正常,-1:删除}
     */
    private Integer isDelete = 0;

    /**
     * 组织名称
     */
    private String organName;

    /**
     * 用户名称
     */
    private String userRealName;

}
