package com.newgrand.superviseanalysis.system.baseBackup.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newgrand.superviseanalysis.system.baseBackup.entity.SysBaseBackup;
import com.newgrand.superviseanalysis.system.baseBackup.service.IBaseBackupService;
import com.newgrand.superviseanalysis.utils.ResponseCode;
import com.newgrand.superviseanalysis.utils.Result;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 田霖禹
 * @since 2020-05-26
 */
@Controller
@RequestMapping("/baseBackup")
public class BaseBackupController {
    private IBaseBackupService baseBackupService;
    private SqlSession sqlSession;
    private Environment env;

    @Autowired
    public BaseBackupController(IBaseBackupService baseBackupService, SqlSession sqlSession, Environment env) {
        this.baseBackupService = baseBackupService;
        this.sqlSession = sqlSession;
        this.env = env;
    }

    @RequestMapping("/page")
    public String Page(HttpServletRequest request) {
        return "baseBackup/list";
    }

    @ResponseBody
    @RequestMapping("/list")
    public Result list() {
        List<SysBaseBackup> list = baseBackupService.list(new QueryWrapper<SysBaseBackup>().orderByDesc("create_time"));
        return Result.success(list);
    }

    @ResponseBody
    @PostMapping("/backup")
    public Result backup(HttpServletRequest request) throws SQLException {

        File folder = new File(env.getProperty("backup.upload-path"));
        String downloadPath = env.getProperty("backup.download-path");

        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }

        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        String name = myFmt.format(new Date()); //数据库名
        try {
            String path = folder.getPath() + "\\" + name + ".bak";// name文件名
            String bakSQL = "backup database SuperviseAnalysisDB to disk=? with init";// SQL语句
            PreparedStatement bak = sqlSession.getConfiguration().getEnvironment().getDataSource().getConnection()
                    .prepareStatement(bakSQL);
            bak.setString(1, path);// path必须是绝对路径
            bak.execute(); // 备份数据库
            bak.close();
            SysBaseBackup sysBaseBackup = new SysBaseBackup();
            sysBaseBackup.setCreateTime(new Date());
            sysBaseBackup.setFileName(name + ".bak");
            sysBaseBackup.setServerPath(path);
            sysBaseBackup.setPath(downloadPath + "/" + sysBaseBackup.getFileName());
            baseBackupService.saveOrUpdate(sysBaseBackup);
            return Result.success("备份成功");

        } catch (Exception e) {
            return Result.failure(ResponseCode.ERROR_999, "备份失败");
        }
    }

    @ResponseBody
    @RequestMapping("/del")
    public Result del(SysBaseBackup sysBaseBackup) {
        return Result.success(baseBackupService.removeById(sysBaseBackup.getId()));
    }
}
