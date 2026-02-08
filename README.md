# 接口实现

## 对用户表进行列表分页查询
**http://localhost:8080/users?pageNum=0&pageSize=3**
pageNum 表示当前请求的页码，pageSize 表示每页返回的记录条数；此处返回从第1页（JPA页码从0开始）开始，每页3条数据

## 根据id单条查询指定用户
**http://localhost:8080/users/4**
返回id=4的用户信息

## 对部门表进行列表分页查询
**http://localhost:8080/depts?pageNum=0&pageSize=3**
作用同上

## 根据id单条查询指定部门
**http://localhost:8080/depts/4**
同上

## 对用户年度分数表进行列表分页查询
**http://localhost:8080/userYearScores?pageNum=0&pageSize=3**
上

## 根据id单条查询指定用户年度分数
**http://localhost:8080/userYearScores/4**
上

