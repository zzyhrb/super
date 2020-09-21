package com.newgrand.superviseanalysis.system.project.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author 田霖禹
 * @since 2020-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PmProjectTableModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 明细主键
     */
    @TableId(value = "detail_id")
    private String detailId;

    /**
     * 名称
     */
    private String detailName;

    /**
     * 数值
     */
    private BigDecimal detailValue;

    /**
     * 树ID
     */
    private String treeId;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 组织ID
     */
    private String organId;

    /**
     * 用途（详细字段使用）
     */
    private String detailPurpose;

    /**
     * 创建人ID
     */
    private Integer userId;

    /**
     * 开始时间
     */
    private Date createTime;

    /**
     * 截止时间
     */
    private Date endTime;

    /**
     * {0:通过，1：未通过}
     */
    private Integer isThrough;

    /**
     * 一次上报的唯一标识
     */
    private String applyFlag;

    /**
     * 创建时间
     */
    private Date createTime2;


}
