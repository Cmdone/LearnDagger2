# 目标

本篇文章的目标是介绍dagger中Bind家族相关的注解，有`@Binds`、`@BindsOptionalOf`、`@MultiBinds`、`@BindsInstance`

# @Binds

之前我们一直没有为`CPU`创建`CPUMoudle`，因为我们将`@Inject`注解到其构造函数上了，dagger就会自动生成`CPU_Factory`工厂类，当有其他对象需要依赖`CPU`时，直接调用此工厂类去创建而不用在`@Component.modules`中声明类似于`CPUModule`的依赖，但如果这种情况发生在具有**抽象（继承）**关系的对象上呢？比如我们知道`CPU`厂商有`Intel`和`AMD`两家，虽然我们可以将`@Inject`都注解到其各自的构造函数上：

```kotlin
open class CPU {
    private val id = Random.nextInt()

    open fun execute(builder: StringBuilder) { // CPU执行时返回自身序列号信息
        builder.append("CPU Id: ").append(id).append("\n")
    }
}

class Intel @Inject constructor() : CPU() {
    override fun execute(builder: StringBuilder) {
        builder.append("Intel's ")
        super.execute(builder)
    }
}

class AMD @Inject constructor() : CPU() {
    override fun execute(builder: StringBuilder) {
        builder.append("AMD's ")
        super.execute(builder)
    }
}
```

但我们在需要依赖注入的地方（例如`Computer`和`CaseActivityComponent`中）就不能使用其父类`CPU`作为需要依赖的声明了：

```kotlin
// 下面这两种声明在dagger编译时都会出错，因为dagger并不知道这里需要的是CPU的哪一个实现类
@set:Inject lateinit var cpu: CPU
fun getCPU(): CPU
```

解决方案我想大家心里都有数，一是将上述代码中的`CPU`改为`Intel`或`AMD`，但这样就**违反了依赖倒置原则**；二是通过`@Module`+`@Provides`的方法，也许还要用上`@Qualifier`指定类型别名，这种方案可以解决问题，但这种情况下dagger提供了另一种解决方案——使用`@Binds`注解：

```kotlin
// 这种情况避免不了创建CPUModule，注意下面是抽象类和抽象方法
@Module
abstract class IntelCPUModule {
    @Binds abstract fun bindIntelCPU(cpu: Intel): CPU // 注意这里的入参
}

@Module
abstract class AMDCPUModule {
    @Binds abstract fun bindIntelCPU(cpu: AMD): CPU
}

// 使用到的地方需要声明@Component.modules依赖上述Module
@Component(modules = [MemoryModule::class, DiskModule::class, DeviceModule::class, IntelCPUModule::class]) 
interface ComputerComponent { /* ... */ }

@Component(modules = [ComputerModule::class, TimestampModule::class, MonitorModule::class, AMDCPUModule::class])
interface CaseActivityComponent { /* ... */ }
```

`@Module`+`@Binds`方案比起`@Module`+`@Provides`方案实现较为简单，但其限制限制稍多，需要注意以下几点：

1. `@Binds`一定用于**注解抽象方法**，且此方法**有且仅有一个参数**，并且此**参数的类型一定是返回值类型的子类**

2. 由于`@Binds`注解在抽象方法上，`@Module`注解的类就也变成抽象类了，但此类中不**允许同时存在*抽象方法*和*非静态方法***，因此如果要混搭`@Provides`和`@Binds`，`@Provides`注解的方法一定放在伴生对象（`companion object`）中

    ```kotlin
    @Module
    abstract class AMDCPUModule {
        @Binds abstract fun bindIntelCPU(cpu: AMD): CPU
        
        @Module
        companion object {
            @Provides fun provideString() = "Cmd" // 此方法相当于java中的static方法，但这样就不能像之前一样通过构造函数传参动态改变数据值了
        }
    }
    ```

3. dagger对于使用`@Binds`注解的抽象方法并不会去实现它（不像被`@Provides`注解的方法最终会被dagger生成的工厂类调用到），所以我们可以将`@Binds`看做是标识符，dagger通过读取这些标识符来确定抽象类`CPU`的实现应该是哪个类

# Optional 与 @BindsOptionalOf

`Optional<T>`是JDK1.8引入的辅助类，其包裹的对象可能为空，如调用`Optional.isPresent()`方法判断其内部是否为空；而`@BindsOptionalOf`则是为了注入`Optional`类型对象存在的，如：

```kotlin
@Module
abstract class OptionalCPUModule {
    @BindsOptionalOf abstract fun optionalCPU(): CPU // 使用@BindsOptionalOf注解在抽象方法上，用于表明可以提供Optional<CPU>这种类型的数据
}

@Module
class CPUModule { // 创建此数据仓库的目的是为了提供CPU实例以达到依赖导致原则，@BindsOptionalOf与上面的@Binds不能混用，很迷
    @Provides fun provideCPU(): CPU = Intel()
}

@Component(
    modules = [MemoryModule::class, DiskModule::class, DeviceModule::class,
        OptionalCPUModule::class, CPUModule::class] // 如果去掉CPUModule的依赖，则Optional中包裹的是空实例
)
interface ComputerComponent {
    fun inject(target: Computer)
}

abstract class Computer(private val os: String, private val price: Int) {
    @set:Inject
    lateinit var cpu: Optional<CPU> // 注意这里是用Optional包裹的
    /* ... */
    
    fun execute(builder: StringBuilder) { // CPU执行时除了返回自身信息，还要将CPU和Memory的信息一并返回
        /* ... */
        if (cpu.isPresent) cpu.get().execute(builder) else builder.append("None CPU exist!\n") // 通过isPresent判断实例是否存在
        /* ... */
    }
    /* ... */
}
```

老实说，我实在想不出什么好理由不用kotlin中的可空类型（比如`CPU?`）而去使用Java中的`Optional`对象，也许`@BindsOptionalOf`在Java中判空非常有用吧，所以这里也就简单介绍下，不再分析生成的源码了（PS：`@BindsOptionalOf`和`@Binds`一样，它们注解的抽象方法都不会有实现，仅是给与dagger一个标识符而已）

# @MultiBinds

在上一篇文章中介绍了怎么用`@IntoSet`、`@IntoMap`等注解实现对`Set`或`Map`容器的注入，但有没有想过如果我们需要注入一个大小为0（`size() == 0`）的`Set`或`Map`容器用dagger怎样实现？也许这听起来很奇怪，但dagger确实考虑到了这种场景：比如你开发了一个依赖dagger的SDK，将`@IntoSet`和`@IntoMap`作为使用SDK可选项提供给开发者并将`@Module`的编写权交给开发者，结果开发者并没有使用这两个注解，导致你代码中类似于`@set:Inject lateinit var ts: Set<T>`因为找不到依赖而报错。显然这是一个很特殊的场景，因为**在编码时不能确定容器内是否有元素被注入**，这时就需要用到dagger提供的`@MultiBinds`注解了

我们假设不知道`Computer`中是否有硬盘（`Disk`）挂载和是否有外设（`Device`）连接：

```kotlin
// 新建下面两个数据仓库
@Module
abstract class DeviceMapModule {
    @Multibinds abstract fun deviceMap(): Map<String, Device>
}

@Module
abstract class DiskSetModule {
    @Multibinds abstract fun diskSet(): Set<Disk>
}

// 修改ComputerComponent的@Component.modules依赖
@Component(
    modules = [MemoryModule::class,
        DiskSetModule::class, DiskModule::class, // 去掉对DiskModule的依赖也没问题
        DeviceMapModule::class, DeviceModule::class, // 去掉对DeviceModule的依赖也没问题
        IntelCPUModule::class]
)
interface ComputerComponent {
    fun inject(target: Computer)
}
```

当将`DiskModule::class`和`DeviceModule::class`两个依赖从`@Component.modules`中去掉后，虽然`Computer`中有以下代码：

```kotlin
@set:Inject lateinit var disks: Set<Disk>
@set:Inject lateinit var devices: Map<String, @JvmSuppressWildcards Device>
```

但也不会因为找不到依赖而编译不通过，因为dagger知道这里需要一个空的`Set`或`Map`容器：

```java
// DaggerComputerComponent.injectComputer()方法
Computer_MembersInjector.injectSetDisks(instance, Collections.<Disk>emptySet());
Computer_MembersInjector.injectSetDevices(instance, Collections.<String, Device>emptyMap());
```

简单来说，与前面的`@Binds`和`@BindsOptionalOf`一样，其注解的抽象方法并不会被调用到，仅仅起一个标识符的作用，告诉dagger可以有这么一个空容器的依赖，让dagger在找不到其他`@IntoSet`、`@IntoMap`注解时，自动创建一个空的`Set`或`Map`容器进行注入，以此**保证依赖的完整性**

# @Components.Builder

在继续说`@BindsInstance`前，我们又一次需要对`@Component`有更多的了解（偷偷剧透，除此之外，`@Component`还有新的内容没有介绍），我们知道`@Component`生成的桥接类在具体使用时都是通过Builder模式创建的，其实这个核心`Builder`类，居然是可以由用户自己定义的！打开`@Component`源码：

```java
// 去掉了注释部分
@Retention(RUNTIME)
@Target(TYPE)
@Documented
public @interface Component {
    
  Class<?>[] modules() default {};
    
  Class<?>[] dependencies() default {};
    
  @Retention(RUNTIME)
  @Target(TYPE)
  @Documented
  @interface Builder {}
    
  @Retention(RUNTIME)
  @Target(TYPE)
  @Documented
  @interface Factory {}
}
```

没错，`@Component`原来有这么多内容，目前我们仅关注其中的`Builder`，阅读其注解大致意思是：`@Component`注解的桥接类接口/抽象类中，可以有且最多有一个被`@Component.Builder`注解的接口/抽象类，这个接口需要对`modules`、`dependencies`、`@BindsInstance`提供`set`系列方法接口，以及一个无参的构造方法其返回一个`Component`桥接类实例；如果`@Component`中有`@Component.Builder`，**dagger则会自动根据这个接口/抽象类创建一个实现类**；这个`Builder`必须满足以下规则：

- 上面提到的`set`系列方法和一个无参构造方法
- `set`系列方法有且仅能有一个入参，并且返回`void`、或此`Builder`（满足链式调用）、或此`Builder`的基类
- 为每一个`dependency`和每一个**非抽象**的`module`提供`set`接口（除了dagger可以使用`@Module`的无参构造函数的那些`module`）
- 可能有一些`@BindsInstance`相关的`set`方法

总而言之，就是开发者可以自定义`Component`的Builder对象，这么做的目的也很简单，为了可以自定义一些相关Builder的逻辑

这里我们拿`ActivityComponent`开刀，修改如下：

```kotlin
@MonitorScope
@Component(modules = [ComputerModule::class, TimestampModule::class, MonitorModule::class, AMDCPUModule::class])
interface CaseActivityComponent {
    /* ... */
    @Component.Builder // 自定义Builder时，这个注解是不能缺少的
    interface Builder { // 接口名称不一定时Builder
        fun computerModule(module: ComputerModule): Builder // 方法名也不一定是xxxModule
        fun monitorModule(module: MonitorModule): Builder
        fun build(): CaseActivityComponent // 一定要有一个无参方法并且返回对应Component，方法名也不一定为build
    }
}
```

注意有些`@Module`可以被dagger自动构建（有无参构造的`Module`，比如上面`TimestampModule`和`AMDCPUModule`），则可以不用在`Builder`中添加对应方法，但相对应的，在`Activity`使用dagger生成的`Component`时，就不能指定这些`Module`了。我们看下生成的源码有什么改动：

```java
// 这里主要关注Builder相关内容，其他内容就省略掉了
public final class DaggerCaseActivityComponent implements CaseActivityComponent {
  /* ... */
  public static CaseActivityComponent.Builder builder() { // 这里返回的是我们自定义的接口
    return new Builder();
  }
  /* ... */
  private static final class Builder implements CaseActivityComponent.Builder { // Builder的实现继承了我们定义的接口
    private ComputerModule computerModule;
    private MonitorModule monitorModule;

    @Override
    public Builder computerModule(ComputerModule module) {
      this.computerModule = Preconditions.checkNotNull(module);
      return this;
    }

    @Override
    public Builder monitorModule(MonitorModule module) {
      this.monitorModule = Preconditions.checkNotNull(module);
      return this;
    }

    @Override
    public CaseActivityComponent build() {
      Preconditions.checkBuilderRequirement(computerModule, ComputerModule.class);
      Preconditions.checkBuilderRequirement(monitorModule, MonitorModule.class);
      return new DaggerCaseActivityComponent(computerModule, new TimestampModule(), monitorModule);
    }
  }
}
```

不难看出，对比自定义`Builder`之前生成的`Component`代码，我们没法通过`Builder`指定`TimestampModule()`了（没有`public Builder timestampModule(TimestampModule timestampModule)`方法了，因为我们没有在接口中声明这样的方法），至于`AMDCPUModule`，因为使用了`@Binds`注解，`component`桥接类将直接构建相应对象实例，就不再依赖于`Moudle`了

另外补充一下，`@Component.Factory`与`@Component.Builder`作用是一致的，前者是在dagger 2.22时才加入的，其中**必须有且仅有一个返回`Component`的抽象方法**，目前看下来区别就是**其中的方法没有了仅能传入一个入参的限制**，官方例子如下：

```java
@Component(modules = {BackendModule.class, FrontendModule.class})
interface MyComponent {
    MyWidget myWidget();

    @Component.Factory
    interface Factory {
        MyComponent newMyComponent(BackendModule bm, FrontendModule fm, @BindsInstance Foo foo); // 注意这个构建方法，直接将所有依赖都传入
    }
}
```

# @BindsInstance

在dagger中，如果需要注入一个对象实例，我们要为其准备被`@Inject`注解的构造函数；如果我们无法修改这个对象的构造函数，我们还得为其准备`@Module`+`@Provides`以便dagger能够创建实例。那么有没有更简单直接的方式呢？

我们假设`Computer`有一个蓝牙模块：

```kotlin
class BlueTooth(private val version: String) { // 需要指明蓝牙的版本
    fun info(builder: StringBuilder) {
        builder.append("Bluetooth Version: ").append(version).append("\n")
    }
}

abstract class Computer(private val os: String, private val price: Int) {
    /* ... */
    @set:Inject lateinit var blueTooth: BlueTooth // 需要将BlueTooth注入到Computer中
    /* ... */
    fun execute(builder: StringBuilder) { // CPU执行时除了返回自身信息，还要将CPU和Memory的信息一并返回
        /* ... */
        blueTooth.info(builder) // 附带上蓝牙的信息
    }
}
```

如上我们不希望修改`BlueTooth`的构造函数，也不希望编写`@Module`+`@Provides`，那么此时我们就能用上`@BindsInstance`了，修改`ComputerComponent`如下：

```kotlin
interface ComputerComponent {
    fun inject(target: Computer)
    fun getBlueTooth(): BlueTooth // 与@Inject注解的变量和方法一样，表明此Component需要能够提供这样的数据

    @Component.Builder
    interface Builder {
        fun build(): ComputerComponent
        @BindsInstance fun blueTooth(blueTooth: BlueTooth): Builder // 在自定义Builder中添加能够提供BuleTooth依赖实例的方法
    }
}

abstract class Computer(private val os: String, private val price: Int) {
    /* ... */
    init {
        DaggerComputerComponent
            .builder()
            .blueTooth(BlueTooth("4.0")) // 修改Computer中构建方法，注意这里需要提供BlueTooth实例
            .build()
            .inject(this)
    }
    /* ... */
}
```

注意`@BindsInstance`这个注解只能用于`@Component.Builder`注解的接口/抽象类中，至于生成的`Component`代码，我想就不用再贴出来了吧（这里需要注意`@BindsInstance`绑定的对象实例`BlueTooth`算是一种局部单例，每次`getBlueTooth()`都是拿到构建`DaggerComputerComponent`中传入的`BlueTooth`类型对象）

另外，我们知道，**`@Inject`注解的构造函数和`@Provides`注解的方法，其中的参数也会被dagger加入到依赖树中**，所以我们也需要对其提供数据源，这里也可以用`@BindsInstance`来提供这些依赖的实例，比如：

```kotlin
// 新建BlueToothModule以提供蓝牙依赖实例
@Module
class BlueToothModule {
    @Qualifier annotation class BlueToothVersion // 自定义@Qualifier注解，给下面的String一个依赖别名，避免与其他String弄混
    @Provides fun provideBlueTooth(@BlueToothVersion version: String) = BlueTooth(version)
}

@Component(modules = [/*... */ BlueToothModule::class]) // 给ComputerComponent添加BlueToothModule的依赖
interface ComputerComponent {
    /* ... */
    @Component.Builder
    interface Builder {
    	/* ... */
        @BindsInstance fun blueToothVersion(@BlueToothModule.BlueToothVersion version: String): Builder // 提供蓝牙版本依赖的实例
    }
}

// 修改Computer中的构建逻辑
init {
    @Suppress("LeakingThis")
    DaggerComputerComponent
    .builder()
    //            .blueTooth(BlueTooth("4.0"))
    .blueToothVersion("2.3")
    .build()
    .inject(this)
}
```

注意这里有别于之前介绍`@Module`时需要使用者手动构建`Module`并传入（比如在`Activity`中的`.monitorModule(MonitorModule(this, text_view))`），因为**dagger能找到`@Provides`注解的方法中的参数依赖**，所以并不会在编译时抛出`MissingBinding`错误，所以`@BindsInstance`也算是对此错误的一种解决方案了

# 总结

至此，本篇总算把dagger中的Bind家族介绍完了，这些注解理解起来并不难，大多只是dagger用来完善依赖关系（`@BindsOptionalOf` & `@MultiBinds`）和提供依赖（`Binds` & `BindsInstance`）的特殊方法，也算是dagger中的进阶使用了。下一篇就是本系列的压轴了，将隆重介绍`Module`数据仓库和`Component`桥接类之间的依赖 & 继承关系，敬请期待吧

