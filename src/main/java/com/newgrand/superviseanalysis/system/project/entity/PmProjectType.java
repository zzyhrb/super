package com.newgrand.superviseanalysis.system.project.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 田霖禹
 * @since 2020-03-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PmProjectType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 组织类别ID
     */
    @TableId(value = "type_id")
    private Integer typeId;

    /**
     * 组织类别名称
     */
    private String typeName;

    /**
     * 是否删除{0:正常,-1:删除,1:停用}
     */
    private Integer isDelete = 0;


}
