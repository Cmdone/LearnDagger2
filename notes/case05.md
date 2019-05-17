# dagger与抽象

继承、封装和多态是面向对象的三大特征，这三大特征无一不是围绕着**抽象**这个词展开的，但在前几篇的例子中，我们的依赖都是具体的类型，比如`Computer`、`CPU`、`Memory`，**严重违反了依赖倒置原则**：

> 模块间的依赖通过抽象发生，实现类之间不发生直接的依赖关系，其依赖关系是通过接口或抽象类产生的

总之，我们接下来需要加上抽象的思想，看看dagger怎样处理

# 新增需求

现在我们将`Computer`改为一个抽象类，并且派生出两个子类：`WindowsComputer`和`LinuxComputer`，而`Activity`中需要依赖这两种`Computer`，并显示它们的信息，改动如下：

```kotlin
// 将Computer改为抽象类
abstract class Computer(private val os: String, private val price: Int) {
    @set:Inject lateinit var cpu: CPU
    @set:Inject lateinit var memory: Memory

    init {
        // 通常在非final类的构造函数中应避免传递this、调用非final方法等，因为父类构造函数先于子类执行，此时调用一些被子类重写的方法可能产生错误。不过这里是对父类中的依赖进行注入，所以Suppress了
        @Suppress("LeakingThis") 
        DaggerComputerComponent
            .builder()
            .memoryModule(MemoryModule(8192))
            .build()
            .inject(this)
    }

    fun execute(builder: StringBuilder) { // CPU执行时除了返回自身信息，还要将CPU和Memory的信息一并返回
        builder.append("Computer OS: ").append(os).append("\n")
        builder.append("Computer Price: ").append(price).append("\n")
        cpu.execute(builder)
        memory.execute(builder)
    }
}

// 写好两个派生类
class WindowsComputer(price: Int) : Computer("Windows", price)
class LinuxComputer(price: Int) : Computer("Linux", price)

// 改动@Module指定的数据仓库，使其能够提供两种类型的Computer（注意下面我将默认返回类型显示写出来了）
@Module
class ComputerModule(private val windowsPrice: Int, private val linuxPrice: Int) {
    @Provides fun provideWindowsComputer(): WindowsComputer = WindowsComputer(windowsPrice)
    @Provides fun provideLinuxComputer(): LinuxComputer = LinuxComputer(linuxPrice)
}
// 改动Activity，添加依赖并增加显示内容
class CaseActivity : BaseActivity() {
    // 注意这里两个依赖都是抽象类Computer而不是WindowsComputer和LinuxComputer
    @field:Inject lateinit var windows: Computer
    @field:Inject lateinit var linux: Computer
    @set:Inject lateinit var timestamp: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case)
        /* 这里先将DaggerCaseActivityComponent的调用去掉 */
        show()
    }

    private fun show() { // 显示两种Computor的信息
        val builder = StringBuilder(timestamp.toString()).append("\n")
        windows.execute(builder)
        linux.execute(builder)
        text_view.text = builder.toString()
    }
}
```

嗯，目前没有其他地方需要改了，启动编译！

```plain
error: [Dagger/MissingBinding] com.example.learndagger2.case05.computer.Computer cannot be provided without an @Provides-annotated method. This type supports members injection but cannot be implicitly provided.
```

`MissingBinding`错误，怎么回事？原来我们`Activity`中依赖的类型是`Computer`，而数据仓库中可以通过的类型只有`WindowsComputer`和`LinuxComputer`，是匹配不上的。没错，**dagger匹配依赖项是通过具体类型匹配的**，子类不能匹配到父类，就是这么严格。那么是不是我们将两个`@Provides`方法返回值类型改成`Computer`就可以了？恭喜，新的错误出现了：

```plain
error: [Dagger/DuplicateBindings] com.example.learndagger2.case05.computer.Computer is bound multiple times
```

由于我们数据仓库中有两个方法都提供能`Computer`，即使我们`Activity`中也有两个`Computer`依赖，但dagger不知道该用哪个数据仓库中的哪个方法以及哪个方法提供的`Computer`要注入到`Activity`哪个依赖中去，总之，**dagger不关心变量与方法名，仅关系它们的类型**

那么是不是就没有办法了呢？其实我们可以将`Activity`的两个`Computer`依赖具体化为`WindowsComputer`和`LinuxComputer`，但明显又违反了依赖倒置原则。接下来就看看dagger框架提供的解决方案——`@Qualifier`

# @Qualifier

`@Qualifier`是`javax.inject`提供的注解，这是一个用在自定义注解上的注解，它可以代替依赖类型，从而解决上面提供相同类型的依赖的问题，如下：

```kotlin
// 修改ComputerModule，使用@Qualifier区分提供数据的类型
@Module
class ComputerModule(private val windowsPrice: Int, private val linuxPrice: Int) {
    // 自定义Qualifier注解
    @Qualifier annotation class WindowsComputerQualifier
	// 使用自定义注解替代依赖类型
    @[Provides WindowsComputerQualifier] fun provideWindowsComputer(): Computer = WindowsComputer(windowsPrice)

    @Qualifier annotation class LinuxComputerQualifier
    @[Provides LinuxComputerQualifier] fun provideLinuxComputer(): Computer = LinuxComputer(linuxPrice)
}

// 修改Activity，使用@Qualifier区分依赖的类型
class CaseActivity : BaseActivity() {
    @field:[Inject ComputerModule.WindowsComputerQualifier] // 使用对应注解
    lateinit var windows: Computer
    @field:[Inject ComputerModule.LinuxComputerQualifier]
    lateinit var linux: Computer
    @set:Inject
    lateinit var timestamp: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case)

        DaggerCaseActivityComponent // 构建桥接类进行注入
            .builder()
            .computerModule(ComputerModule(6666, 8888))
            .build()
            .inject(this)
        
        show()
    }
    /* ... */
}
```

这里需要注意`CaseActivity`中的依赖还是得和`@Provides`注解的方法的返回值类型一致，即**dagger首先匹配返回类型，在匹配`@Qualifier`对此类型注解的别名**。这样一顿操作后，dagger就可以根据`@Qualifier`定义的别名对应上`Activity`中的依赖了，如下生成的`DaggerCaseActivityComponent`源码：

```java
  private CaseActivity injectCaseActivity(CaseActivity instance) {
    CaseActivity_MembersInjector.injectWindows( // 注入windows
        instance,
        ComputerModule_ProvideWindowsComputerFactory.provideWindowsComputer(computerModule));
    CaseActivity_MembersInjector.injectLinux( // 注入linux
        instance, ComputerModule_ProvideLinuxComputerFactory.provideLinuxComputer(computerModule));
    CaseActivity_MembersInjector.injectSetTimestamp(
        instance, TimestampModule_ProvideTimestampFactory.provideTimestamp(timestampModule));
    return instance;
  }
```

# @Qualifier的属性

`@Qualifier`注解的注解可以有自己的属性，比如我们知道内存（`Memory`）有金士顿（`Kingston`）和三星（`Samsung`）两家厂商，每家厂商都有生产8G（8192MB）和4G（4096MB）两种规格的内存。我们对`Memory`修改如下：

```kotlin
// Memory中添加厂商信息
class Memory(private val size: Int, private val vendor: String) {
    fun execute(builder: StringBuilder) { // Memory执行时返回自身容量信息
        builder.append("MEmory Vendor: ").append(vendor).append("\n")
        builder.append("Memory Size: ").append(size).append("MB\n")
    }
}

// MemoryModule中提供不同厂商和规格的Memory
@Module
class MemoryModule {
    @Provides fun provideKingston4G() = Memory(4096, "Kingston")
    @Provides fun provideKingston8G() = Memory(8192, "Kingston")
    @Provides fun provideSamsung4G() = Memory(4096, "Samsung")
    @Provides fun provideSamsung8G() = Memory(8192, "Samsung")
}
```

但我们知道4个`@Provides`注解的方法都返回同样的类型，dagger是无法处理的，所以我们要为每种`Memory`都定义一个`@Qualifier`注解的自定义注解吗？其实还有另一种方法，如下：

```kotlin
@Module
class MemoryModule {
    enum class Vendor { Kingston, Samsung }
    @Qualifier annotation class MemoryType(val size: Int = 4096, val vendor: Vendor = Vendor.Kingston) // 自定义@Qualifier注解，含有自定义的属性

    @[Provides MemoryType] // 默认大小+默认厂商
    fun provideKingston4G() = Memory(4096, "Kingston")

    @[Provides MemoryType(size = 8192)] // 8G大小+默认厂商
    fun provideKingston8G() = Memory(8192, "Kingston")

    @[Provides MemoryType(vendor = Vendor.Samsung)] // 默认大小+三星厂商
    fun provideSamsung4G() = Memory(4096, "Samsung")

    @[Provides MemoryType(size = 8192, vendor = Vendor.Samsung)] // 8G大小+三星产商
    fun provideSamsung8G() = Memory(8192, "Samsung")
}
```

我们通过`@Qualifier`的自定义属性再次细分，所以我们`Computer`可以改成这样：

```kotlin
abstract class Computer(private val os: String, private val price: Int) {
    /* ... */
    @set:Inject
    @setparam:MemoryModule.MemoryType(vendor = MemoryModule.Vendor.Samsung) // 注意这里用的是setparam
    lateinit var memory: Memory

    init {
        @Suppress("LeakingThis")
        DaggerComputerComponent.create().inject(this) // 因为MemoryModule有无参构造函数了，ComputerComponent所依赖的所有Module都可以由dagger自行创建，这里就可以用create()而不是builder()方法了
    }
}
```

如果需要改变注入到`Computer`中的`memory`类型的话，更改`MemoryType`注解的属性就可以了

# @Named

最后提一下`@Named`，这也是`javax.inject`中提供的注解，其本身也就是一个`@Qualifier`注解的注解，我们直接看源码应该就能明白怎么使用了

```java
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface Named {
    /** The name. */
    String value() default "";
}
```

比如上面我们自定义的`MemoryType`注解就可以换成`Named("Kingston-4G")`，也有一样的效果

# 总结

我们可以通过`@Qualifier`给返回类型一个别名，从而解决`DuplicateBindings`的问题，甚至可以添加自定义注解的属性，以此更细分数据仓库中提供的同类型数据。`javax.inject`也默认提供了`@Named`注解，但更推荐自定义`@Qualifier`注解，让其更具备语义，以及在管理上更加层次化

至此篇结束，我们已经能够使用dagger进行一些简单的开发了，但dagger提供的功能远不仅仅于此……