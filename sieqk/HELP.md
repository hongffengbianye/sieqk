1、本地安装mysql数据库并导入建表脚本（项目根目录apply_t.sql）
2、在application.properties文件里修改本地mysql的连接串
3、或者访问https://my.chinasie.com/attcenter/apply/page/-null- 输入账号密码后F12查看网络请求负载里面的accountId和password
4、修改application.properties文件，config登陆的账号密码，tmTime是转TM的时间点，转TM后加班按双倍算（填的加班单还是一倍），不涉及的可以修改代码逻辑，ApplyController类的139行，2*Double改为Double
5、打开SieqpApplication.java文件，右键启动项目
6、访问地址http://localhost:60000/siekq/apply/synchronization，自动同步数据

下面是表里查数据的sql
SELECT SUM(otHours) FROM work_overtime_detail_t; -- 加班
SELECT SUM(leDays) FROM vacation_detail_t WHERE leType = '调休假'; -- 调休
#年假另外算，vacation_detail_t表里包含年假记录
只有一个请求地址，只做了同步数据的功能，代码是随便写着玩的，有兴趣的同学可以自己优化代码加功能

