package com.newgrand.superviseanalysis.system.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author 田霖禹
 * @since 2020-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PmProjectPurpose implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 值
     */
    private BigDecimal value;

    /**
     * 对应详情ID
     */
    private String detailId;

    /**
     * 对应字典ID
     */
    private Integer dictId;

    /**
     * 对应详情ID
     */
    private String treeId;

    /**
     * 上报标识
     */
    private String applyFlag;

}
