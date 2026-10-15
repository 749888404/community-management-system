package com.community.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.PageResult;
import com.community.common.Result;
import com.community.entity.User;
import com.community.entity.excel.UserExcel;
import com.community.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /** 分页查询 */
    @GetMapping("/page")
    public Result<PageResult<User>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(username != null && !username.isEmpty(), User::getUsername, username)
               .like(realName != null && !realName.isEmpty(), User::getRealName, realName)
               .eq(status != null, User::getStatus, status)
               .orderByAsc(User::getId);

        Page<User> result = userService.page(new Page<>(page, size), wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @PostMapping
    public Result<?> save(@RequestBody User user) {
        // 新增时若无密码，默认 123456（MD5）
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("e10adc3949ba59abbe56e057f20f883e");
        }
        userService.save(user);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody User user) {
        userService.updateById(user);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> remove(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("删除成功");
    }

    /** 批量删除 */
    @PostMapping("/batch-delete")
    public Result<?> batchRemove(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的用户");
        }
        userService.removeByIds(ids);
        return Result.success("删除成功，共 " + ids.size() + " 条");
    }

    /** 导出 Excel（按查询条件导出全部） */
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String username,
                       @RequestParam(required = false) String realName,
                       @RequestParam(required = false) Integer status) throws IOException {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(username != null && !username.isEmpty(), User::getUsername, username)
               .like(realName != null && !realName.isEmpty(), User::getRealName, realName)
               .eq(status != null, User::getStatus, status)
               .orderByAsc(User::getId);

        List<User> list = userService.list(wrapper);
        List<UserExcel> data = list.stream().map(u -> {
            UserExcel e = new UserExcel();
            BeanUtils.copyProperties(u, e);
            return e;
        }).collect(Collectors.toList());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("用户管理", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), UserExcel.class).sheet("用户管理").doWrite(data);
    }

    /** 导入 Excel（按 ID 更新，无 ID 则新增） */
    @PostMapping("/import")
    public Result<?> importData(@RequestParam("file") MultipartFile file) throws IOException {
        List<UserExcel> data = EasyExcel.read(file.getInputStream()).head(UserExcel.class).sheet().doReadSync();
        int count = 0;
        for (UserExcel e : data) {
            User u = new User();
            BeanUtils.copyProperties(e, u);
            if (e.getId() != null && userService.getById(e.getId()) != null) {
                userService.updateById(u);
            } else {
                u.setId(null);
                if (u.getPassword() == null || u.getPassword().isEmpty()) {
                    u.setPassword("e10adc3949ba59abbe56e057f20f883e");
                }
                userService.save(u);
            }
            count++;
        }
        return Result.success("导入成功，共处理 " + count + " 条");
    }
}
