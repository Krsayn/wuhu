# [01.对原生jdbc程序中的问题总结](01.对原生jdbc程序中的问题总结.md)
- [1. jdbc编程步骤](01.对原生jdbc程序中的问题总结.md/#1-jdbc编程步骤)
- [2. 问题总结](01.对原生jdbc程序中的问题总结.md/#2-问题总结)
- [3. 参考代码](01.对原生jdbc程序中的问题总结.md/#3-参考代码)

# [02.mybatis概述](02.mybatis概述.md)
- [1. mybatis 介绍](02.mybatis概述.md/#1-mybatis-介绍)
- [2. 框架原理](02.mybatis概述.md/#2-框架原理)
- [3. mybatis框架执行过程](02.mybatis概述.md/#3-mybatis框架执行过程)
- [4. mybatis开发dao的方法](02.mybatis概述.md/#4-mybatis开发dao的方法)
- [5. 输入映射和输出映射](02.mybatis概述.md/#5-输入映射和输出映射)
- [6. 动态sql](02.mybatis概述.md/#6-动态sql)

# [03.入门程序](03.入门程序.md)
- [1. 目录结构](03.入门程序.md/#1-目录结构)
- [2. 数据库表的设计](03.入门程序.md/#2-数据库表的设计)
- [3. 配置文件](03.入门程序.md/#3-配置文件)
  - [3.1 pom.xml](03.入门程序.md/#31-pomxml)
  - [3.2 log4j.properties](03.入门程序.md/#32-log4jproperties)
  - [3.3 SqlMapConfig.xml](03.入门程序.md/#33-sqlmapconfigxml)
- [4. User.java](03.入门程序.md/#4-userjava)
- [5. User.xml](03.入门程序.md/#5-userxml)
- [6. MybatisFirst.java](03.入门程序.md/#6-mybatisfirstjava)
- [7. 总结](03.入门程序.md/#7-总结)

# [04.开发dao方法](04.开发dao方法.md)
- [1. SqlSession使用范围](04.开发dao方法.md/#1-sqlsession使用范围)
- [2. 项目结构](04.开发dao方法.md/#2-项目结构)
- [3. 原始dao方法](04.开发dao方法.md/#3-原始dao方法)
  - [3.1 UserDao.java](04.开发dao方法.md/#31-userdaojava)
  - [3.2 UserDaoImpl.java](04.开发dao方法.md/#32-userdaoimpljava)
  - [3.3 UserDaoImplTest.java](04.开发dao方法.md/#33-userdaoimpltestjava)
- [4. Mybatis的mapper接口（相当于dao接口）代理开发方法](04.开发dao方法.md/#4-mybatis的mapper接口相当于dao接口代理开发方法)
  - [4.1 UserMapper.java](04.开发dao方法.md/#41-usermapperjava)
  - [4.2 UserMapper.xml](04.开发dao方法.md/#42-usermapperxml)
  - [4.3 在测试之前需要在SqlMapConfig.xml中加载mapper.xml这个映射文件](04.开发dao方法.md/#43-在测试之前需要在sqlmapconfigxml中加载mapperxml这个映射文件)
  - [4.4 UserMapperTest.java](04.开发dao方法.md/#44-usermappertestjava)
- [5. 总结](04.开发dao方法.md/#5-总结)
  - [5.1 原始dao开发问题](04.开发dao方法.md/#51-原始dao开发问题)
  - [5.2 mapper开发](04.开发dao方法.md/#52-mapper开发)
- [6. 一些问题总结](04.开发dao方法.md/#6-一些问题总结)

# [05.配置文件](05.配置文件.md)
- [1. SqlMapConfig.xml中配置的内容和顺序](05.配置文件.md/#1-sqlmapconfigxml中配置的内容和顺序)
- [2. properties（属性）](05.配置文件.md/#2-properties属性)
- [3. settings（全局配置参数）](05.配置文件.md/#3-settings全局配置参数)
- [4. typeAliases（类型别名）(重点)](05.配置文件.md/#4-typealiases类型别名重点)
- [5. typeHandlers（类型处理器）](05.配置文件.md/#5-typehandlers类型处理器)
- [6. objectFactory（对象工厂）](05.配置文件.md/#6-objectfactory对象工厂)
- [7. plugins（插件）](05.配置文件.md/#7-plugins插件)
- [8. environments（环境集合属性对象）](05.配置文件.md/#8-environments环境集合属性对象)
- [9. mappers（映射器）](05.配置文件.md/#9-mappers映射器)

# [06.Mybatis的输入和输出映射](06.Mybatis的输入和输出映射.md)
- [1. Mybatis输入映射（掌握）](06.Mybatis的输入和输出映射.md/#1-mybatis输入映射掌握)
  - [1.1 目录结构](06.Mybatis的输入和输出映射.md/#11-目录结构)
  - [1.2 UserCustom.java](06.Mybatis的输入和输出映射.md/#12-usercustomjava)
  - [1.3 UserQueryVo.java](06.Mybatis的输入和输出映射.md/#13-userqueryvojava)
  - [1.4 UserMapper.java](06.Mybatis的输入和输出映射.md/#14-usermapperjava)
  - [1.5 UserMapper.xml中配置新的查询](06.Mybatis的输入和输出映射.md/#15-usermapperxml中配置新的查询)
  - [1.6 UserMapperTest.java中新增测试](06.Mybatis的输入和输出映射.md/#16-usermappertestjava中新增测试)
- [2. Mybatis输出映射（掌握）](06.Mybatis的输入和输出映射.md/#2-mybatis输出映射掌握)
  - [2.1 resultType](06.Mybatis的输入和输出映射.md/#21-resulttype)
    - [2.1.1 resultType的输出简单类型](06.Mybatis的输入和输出映射.md/#211-resulttype的输出简单类型)
    - [2.1.2 resultType的输出pojo对象和pojo列表](06.Mybatis的输入和输出映射.md/#212-resulttype的输出pojo对象和pojo列表)
  - [2.2 resultMap](06.Mybatis的输入和输出映射.md/#22-resultmap)
  - [2.3 总结](06.Mybatis的输入和输出映射.md/#23-总结)

# [07.动态sql](07.动态sql.md)
- [1. 什么是动态sql？](07.动态sql.md/#1-什么是动态sql)
- [2. if判断](07.动态sql.md/#2-if判断)
  - [2.1 UserMapper.xml](07.动态sql.md/#21-usermapperxml)
  - [2.2 测试结果](07.动态sql.md/#22-测试结果)
    - [1.注释掉`testFindUserList()`方法中的`userCustom.setUsername("张三");`](07.动态sql.md/#1注释掉testfinduserlist方法中的usercustomsetusername张三)
    - [2.`userQueryVo`设为null,则`userCustom`为null](07.动态sql.md/#2userqueryvo设为null则usercustom为null)
- [3. sql片段(重点)](07.动态sql.md/#3-sql片段重点)
  - [3.1 定义sql片段](07.动态sql.md/#31-定义sql片段)
  - [3.2 引用sql片段](07.动态sql.md/#32-引用sql片段)
- [4. foreach标签](07.动态sql.md/#4-foreach标签)
  - [4.1 在输入参数类型中添加`List<Integer> ids`传入多个id](07.动态sql.md/#41-在输入参数类型中添加listinteger-ids传入多个id)
  - [4.2 修改mapper.xml](07.动态sql.md/#42-修改mapperxml)
  - [4.3 测试代码](07.动态sql.md/#43-测试代码)

# [08.订单商品数据模型分析](08.订单商品数据模型分析.md)
- [2. 数据模型分析](08.订单商品数据模型分析.md/#2-数据模型分析)
  - [2.1 表与表之间的业务关系：](08.订单商品数据模型分析.md/#21-表与表之间的业务关系)
    - [1. usre和orders：](08.订单商品数据模型分析.md/#1-usre和orders)
    - [2. orders和orderdetail：](08.订单商品数据模型分析.md/#2-orders和orderdetail)
    - [3. orderdetail和itesm：](08.订单商品数据模型分析.md/#3-orderdetail和itesm)
    - [4. orders和items：](08.订单商品数据模型分析.md/#4-orders和items)
- [3. 订单商品数据模型建表sql](08.订单商品数据模型分析.md/#3-订单商品数据模型建表sql)

# [09.高级映射结果集](09.高级映射结果集.md)
- [1. 一对一](09.高级映射结果集.md/#1-一对一)
  - [1.1. resultType实现](09.高级映射结果集.md/#11-resulttype实现)
  - [1.2. resultMap实现](09.高级映射结果集.md/#12-resultmap实现)
  - [1.3. resultType和resultMap实现一对一查询小结](09.高级映射结果集.md/#13-resulttype和resultmap实现一对一查询小结)
- [2. 一对多](09.高级映射结果集.md/#2-一对多)
  - [2.1. 需求](09.高级映射结果集.md/#21-需求)
  - [2.2 要求](09.高级映射结果集.md/#22-要求)
  - [2.3. 解决思路](09.高级映射结果集.md/#23-解决思路)
  - [2.4. resultMap](09.高级映射结果集.md/#24-resultmap)
  - [2.5. OrderMapper.xml](09.高级映射结果集.md/#25-ordermapperxml)
  - [2.6. OrderMapper.java](09.高级映射结果集.md/#26-ordermapperjava)
  - [2.7. 测试](09.高级映射结果集.md/#27-测试)
  - [2.8. 小结](09.高级映射结果集.md/#28-小结)
- [3. 多对多](09.高级映射结果集.md/#3-多对多)
  - [3.1 需求](09.高级映射结果集.md/#31-需求)
  - [3.2 sql](09.高级映射结果集.md/#32-sql)
  - [3.3 映射思路](09.高级映射结果集.md/#33-映射思路)
  - [3.4 resultMap](09.高级映射结果集.md/#34-resultmap)
  - [3.5 OrderMapper.xml](09.高级映射结果集.md/#35-ordermapperxml)
  - [3.6 OrderMapper.java](09.高级映射结果集.md/#36-ordermapperjava)
  - [3.7 测试](09.高级映射结果集.md/#37-测试)
  - [3.8 多对多查询总结](09.高级映射结果集.md/#38-多对多查询总结)
- [4. 总结](09.高级映射结果集.md/#4-总结)
  - [4.1 resultType](09.高级映射结果集.md/#41-resulttype)
  - [4.2 resultMap](09.高级映射结果集.md/#42-resultmap)
    - [4.2.1 association(一对一)](09.高级映射结果集.md/#421-association一对一)
    - [4.2.2 collection(一对多)](09.高级映射结果集.md/#422-collection一对多)

# [10.延迟加载](10.延迟加载.md)
- [1. 使用association实现延迟加载](10.延迟加载.md/#1-使用association实现延迟加载)
- [2. 延迟加载思考](10.延迟加载.md/#2-延迟加载思考)

# [11.查询缓存](11.查询缓存.md)
- [1. 查询缓存](11.查询缓存.md/#1-查询缓存)
- [2. 一级缓存](11.查询缓存.md/#2-一级缓存)
	- [2.1 一级缓存工作原理](11.查询缓存.md/#21-一级缓存工作原理)
	- [2.2 一级缓存测试](11.查询缓存.md/#22-一级缓存测试)
	- [2.3 一级缓存应用](11.查询缓存.md/#23-一级缓存应用)
- [3. 二级缓存](11.查询缓存.md/#3-二级缓存)
	- [3.1 二级缓存原理](11.查询缓存.md/#31-二级缓存原理)
	- [3.2 开启二级缓存](11.查询缓存.md/#32-开启二级缓存)
	- [3.3 调用pojo类实现序列化接口](11.查询缓存.md/#33-调用pojo类实现序列化接口)
	- [3.4 测试方法](11.查询缓存.md/#34-测试方法)
	- [3.5 useCache配置](11.查询缓存.md/#35-usecache配置)
	- [3.6 刷新缓存（就是清空缓存）](11.查询缓存.md/#36-刷新缓存就是清空缓存)
	- [3.7 应用场景和局限性](11.查询缓存.md/#37-应用场景和局限性)

# [12.mybatis整合ehcache](12.mybatis整合ehcache.md)
- [1. 分布缓存](12.mybatis整合ehcache.md/#1-分布缓存)
- [2. 整合方法(掌握)](12.mybatis整合ehcache.md/#2-整合方法掌握)
  - [2.1 整合ehcache](12.mybatis整合ehcache.md/#21-整合ehcache)
  - [2.2 加入ehcache的配置文件](12.mybatis整合ehcache.md/#22-加入ehcache的配置文件)