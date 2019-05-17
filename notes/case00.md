# 文章目的

- 本系列为个人学习笔记，更多在于个人理解以及对源码的剖析，可能对于想快速上手的人不那么适合
- 普遍认为`dagger`难以上手，但我认为一是因为[官网引导](https://google.github.io/dagger/)不够详细和普适（用怎样制造咖啡为例着实不熟悉，各种不完整代码片段看得人一脸懵逼），二是大部分文章只说了一些基础用法，一旦涉及到高级用法就潦草带过，难以理解
- 我试着从dagger的使用、设计模式、源码理解几个方面去分析，由浅入深
- 本系列主要以kotlin作为主要语言，dagger版本为`2.22.1`，会留意讲解kotlin和java在使用dagger2上的不同
- 本系列源码会同步至Github，大家可以参考着看

# 引入dagger

引入dagger在[官方说明](https://github.com/google/dagger)上已经很详细了，这里说下在kotlin中的不同：

```groovy
apply plugin: 'kotlin-kapt' // 使用kapt插件，是kotlin版本的注解处理器

denpendencies {
    implementation 'com.google.dagger:dagger:2.22.1'
    kapt 'com.google.dagger:dagger-compiler:2.22.1' // 使用kapt而不是annotationProcessor
}
```

## 注解处理器

顾名思义，注解处理器就是用来处理注解的东西，什么是注解这里不多解释，只是个人反对`注解本身是没有意义的`这种说法，我认为注解就是用于添加元数据的一种方式。Android中的注解处理器通常流程是：编译阶段遍历所有源文件查找目标注解、根据注解内容生成模板代码、在源代码中显示调用约定好的方法以使用那些生成的代码。简单理解注解处理器就是一个**帮你自动生成代码的工具**

除了dagger外，例如[Butter Knife](http://jakewharton.github.io/butterknife/)、[Room](https://developer.android.google.cn/training/data-storage/room/index.html)等也是利用注解处理器的框架，如果你使用过它们，相信对于dagger的代码生成原理也不会陌生；如果你没有接触过注解处理器，可以想象利用反射机制，对被特定的注解所标记的类或变量等，进行信息读取并生成相应代码；如果也没有接触过反射，那建议你先弄清楚**注解**、**元数据**、**注解处理器**这些概念

# javax与dagger

dagger遵循`JSR 330`标准，这个标准是什么并不重要，只要记得dagger本身依赖于`javax.inject`这个包就行了，类似于`@Inject`、`Provider`、`@Qualifier`这些都是`javax.inject`定义的内容

# 特别注意！！！

**由于本系列是一边学习理解一边编写的文章，在对于`@Module`的使用上稍有偏差，正常来说例如`ComputerModule`应该代表的是`Computer`所依赖的所有对象的数据仓库，这个仓库时专门为`Computer`建立的（即其内部应该是`provideCPU`、`provideMemory`一系列方法），而不是`Computer`对外提供数据依赖的仓库（而不是`providerComputer`这种方法）。由于发现此问题已经较晚且没精力再去改动代码和文章，还望读者谅解**