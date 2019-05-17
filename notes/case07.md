# 目标：使用dagger注入“单例”

通过我们之前对`@Provides`生成的工厂类、`@Inject`注解变量或方法生成的注入器以及`@Component`生成的桥接类的分析，我们发现dagger每次在注入依赖时，其实都会通过工厂类创建一个新的实例，所以在上一篇文章最后，我抛出了dagger中**单例**的问题，本篇也是围绕着此问题展开的

# Kotlin中的单例

众所周知，在kotlin中通过`object`可以非常简单的构建单例，例如：

```kotlin
object Singleton { // object类不能有构造函数，其也不能被外部构造
    const val NAME = "Cmd"
    val AGE = 24
    fun info() = "$NAME-$AGE"
}
```

我们看下对应的java代码长啥样：

```java
public final class Singleton {
   @NotNull
   public static final String NAME = "Cmd"; // 对于有const标记的变量，可见性为public，省去了get方法调用
   private static final int AGE = 24; // 没有const标记的变量，可见性为private，会生成get方法（如果是var还会有set方法）
   public static final Singleton INSTANCE;

   public final int getAGE() {
      return AGE;
   }

   @NotNull
   public final String info() {
      return "Cmd-" + AGE;
   }

   private Singleton() { // 注意构造函数的可见性为private
   }

   static {
      Singleton var0 = new Singleton(); // 在static块中初始化单例
      INSTANCE = var0;
      AGE = 24;
   }
}
```

代码不难懂，唯一要注意的是kotlin的`object`是饿汉式单例，在*内存占用*、*线程安全*、*序列化与反序列化*等方面表现并不是很好，所以如果对单例有更高的要求，建议不要直接使用`object`这个语法糖。至于其他单例实现方式不是本系列重点，就不详细说明了

# 需求：优化代码

我觉得学习一个框架，就应该先想清楚这个框架能用在什么地方，所以通常我都会编一些需求来模拟实际使用场景

回想我们现在的代码，`Activity`不仅有逻辑控制部分（根据时间选择`Computer`、使用`Handler`实现定时器等）还有显示视图部分（显示`Computer`信息、显示`timestamp`信息等），这岂不是有违**单一职责原则**？所以我决定将这两部分拆分，逻辑控制部分留在`Activity`中，而显示视图部分则交给新的类——`Monitor`

# 借助object的全局单例

`Monitor`类仅负责显示视图，为了节省内存我们不妨借助`object`封装成一个“工具类”：

```kotlin
object Monitor {
    fun show(textView: TextView, computer: Computer) { // 在textView中显示Computer信息
        val builder = StringBuilder()
        computer.execute(builder)
        textView.text = builder.toString()
    }

    fun startRefresh(textView: TextView, timestamp: Date) { // 在textView中添加timestamp信息
        val builder = StringBuilder(textView.text)
        builder.append(timestamp.toString()).append("\n")
        textView.text = builder.toString()
    }
    
    fun toastInfo(context: Context) { // 通过toast显示一些信息
        val builder = StringBuilder()
        builder.append("Monitor: ").append(this.hashCode())
        Toast.makeText(context, builder.toString(), Toast.LENGTH_SHORT).show()
    }
}
```

当然我们可以在`Activity`中直接使用这个单例，但因为本系列主题是dagger，更应该考虑怎样用dagger来注入这个单例

通过之前几篇文章的分析我们应该清楚：**dagger实际上最终调用到的是我们自己写的`@Provides`注解的方法来创建新的实例的**，所以我们创建`MonitorModule`并修改其他部分：

```kotlin
@Module
class MonitorModule {
    @Provides fun provideMonitor() = Monitor // @Provides每次都返回这个单例
}

@Component(modules = [ComputerModule::class, TimestampModule::class, MonitorModule::class]) // @Component.modules添加MonitorModule数据仓库
interface CaseActivityComponent {
    /* ... */
    fun getMonitor(): Monitor // 添加一个获取Monitor实例的方法
}

class CaseActivity : BaseActivity() {
    /* ... */
    private lateinit var component: CaseActivityComponent // 将Component桥接类保存下来，以便调用getMonitor获取Monitor实例

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case)

        component = DaggerCaseActivityComponent
            .builder()
            .computerModule(ComputerModule(6666, 8888))
            .build()
        component.inject(this)

        show()
        startRefresh()
        
        text_view.setOnClickListener { component.getMonitor().toastInfo(this) } // 每次点击TextView就显示Monitor相关信息
    }

    private fun show() {
        val computer = if (System.currentTimeMillis() % 2 == 0L) linux.get() else windows.get()
        component.getMonitor().show(text_view, computer) // 显示部分交由Monitor处理
    }

    private fun startRefresh() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val date = timestamp.get()
                component.getMonitor().startRefresh(text_view, date) // 显示部分交由Monitor处理
                if (!isDestroy) {
                    handler.postDelayed(this, 1000)
                }
            }
        }, 1000)
    }
    /* ... */
}
```

*（这里我们不是通过`@Inject`注解让dagger自动注入，主要是方面讲解单例相关内容）*

运行起来，我们反复点击文本，发现每次toast内容确实都一样，说明kotlin的`object`结合dagger确实是可以实现单例。但这样的单例是一个**全局单例**，什么意思呢，假如我们有很多个`Activity`都需要使用到`Monitor`，那么这些`Activity`拿到的`Monitor`都是同一个实例，如果说我只需要在某个`Activity`或**某个范围内实现局部单例**，例如将`Monitor`改为如下写法：

```kotlin
class Monitor(private val context: Context, private val textView: TextView) { } // 注意不再是object，且构造函数需要传入特定参数，说明Monitor仅作用于这些参数的范围中
```

当然我们又可以改写`@Provides`注解的方法中的逻辑（例如用`HashMap`记录类似于`<<Context, TextView>, Monitor>`这样的键值对，或者在`@Modules`注解的类中添加成员变量等），但这说明你还不知道dagger中的`@Scope`注解

# 使用@Scope注解

为了配合`Monitor`的改动，我们各部分也需要修改一下：

```kotlin
//object Monitor {
class Monitor(private val context: Context, private val textView: TextView) { // object改为class，将context和textView以成员变量形式保存下来
    //  fun show(textView: TextView, computer: Computer) {
    fun show(computer: Computer) { /* ... */ }

    //  fun startRefresh(textView: TextView, timestamp: Date) {
    fun startRefresh(timestamp: Date) { /* ... */ }

    //  fun toastInfo(context: Context) {
    fun toastInfo() { // 这些方法入参都能用成员变量替换掉，这里我们多toast出一些信息
        val builder = StringBuilder()
        builder.append("Monitor: ").append(this.hashCode()).append("\n")
        builder.append("Context: ").append(context.hashCode()).append("\n")
        builder.append("TextView: ").append(textView.hashCode())
        Toast.makeText(context, builder.toString(), Toast.LENGTH_SHORT).show()
    }
}

@Module
class MonitorModule(private val context: Context, private val textView: TextView) { // @Module和@Provides也做相应修改
    @Provides fun provideMonitor() = Monitor(context, textView)
}

class CaseActivity : BaseActivity() { // Activity中修改相应方法
    /* ... */
    private lateinit var component: CaseActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        /* ... */
        component = DaggerCaseActivityComponent
            .builder()
            .computerModule(ComputerModule(6666, 8888))
            .monitorModule(MonitorModule(this, text_view)) // 因为@Module没有无参构造函数了，需要在构建Component时手动传入实例
            .build()
        component.inject(this)
		/* ... */
//        text_view.setOnClickListener { component.getMonitor().toastInfo(this) }
        text_view.setOnClickListener { component.getMonitor().toastInfo() }
    }

    private fun show() {
        val computer = if (System.currentTimeMillis() % 2 == 0L) linux.get() else windows.get()
//        component.getMonitor().show(text_view, computer)
        component.getMonitor().show(computer)
    }

    private fun startRefresh() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val date = timestamp.get()
//                component.getMonitor().startRefresh(text_view, date)
                component.getMonitor().startRefresh(date)
                if (!isDestroy) {
                    handler.postDelayed(this, 1000)
                }
            }
        }, 1000)
    }
    /* ... */
}
```

改完了就运行起来吧，反复点击文本，发现虽然`context`和`textView`每次都是一样的，但是`monitor`却每次都不一样，显然每次调用`getMonitor()`方法都创建了一个新的`Monitor`实例，毕竟我们`@Provides`方法中就是去创建新的实例。那这样岂不是很浪费内存？难道就不能实现**局部单例**了吗？这里就得`@Scope`登场了，我们先看看怎么用：

```kotlin
@Scope
annotation class MonitorScope // 使用@Scope自定义一个注解，表示一个【作用范围】

@Module
class MonitorModule(private val context: Context, private val textView: TextView) {
    @[Provides MonitorScope] fun provideMonitor() = Monitor(context, textView) // 在@Provides注解的方法上追加我们自定义的@Scope注解，表明此方法用于某个范围内
}

@MonitorScope // 在@Component注解的接口上也追加上自定义的@Scope注解，表明此桥接类使用某个范围
@Component(modules = [ComputerModule::class, TimestampModule::class, MonitorModule::class])
interface CaseActivityComponent { /* ... */ }
```

没错，只是这两处小修改，再次运行后还是反复点击文本，神奇的事情发生了，每次`Monitor`都是一样的！只有我们重新进入这个`Activity`时，才会有新的`Monitor`实例，我们**通过`@Scope`实现了局部单例**

这么神奇的事情怎么能不看下源码呢？我们知道`@Provides`注解的方法实际上会生成`Factory`工厂类，但我们打开生成的`MonitorModule_ProvideMonitorFactory`工厂类发现其实现并没有改变。于是我们的目标锁定在`@Component`生成的桥接类上了：

```java
public final class DaggerCaseActivityComponent implements CaseActivityComponent {
  /* ... */
  private Provider<Monitor> provideMonitorProvider;
  /* ... */
  private void initialize(
      final ComputerModule computerModuleParam,
      final TimestampModule timestampModuleParam,
      final MonitorModule monitorModuleParam) {
    /* ... */
    this.provideMonitorProvider =
        DoubleCheck.provider(MonitorModule_ProvideMonitorFactory.create(monitorModuleParam)); // 注意这一行
  }
  /* ... */
  @Override
  public Monitor getMonitor() {
    return provideMonitorProvider.get();
  }
  // 注意对比上下这两个方法
  @Override
  public Lazy<CPU> getLazyCPU() {
    return DoubleCheck.lazy(CPU_Factory.create());
  }
  /* ... */
}
```

生成的`DaggerCaseActivityComponent`中，`Provider<Monitor>`并不是直接保存`Monitor`的工厂类了，而是**通过`DoubleCheck.provider()`又进行了一次封装**！为什么是“又”呢，想想上一篇文章中我们介绍了`Lazy`，介绍了其在生成的`Component`中的`DoubleCheck.lazy()`封装，还有`DoubleCheck.get()`的双检测实现，但回头看下是不是找不到`DoubleCheck.provider()`这个方法？其实是因为我没贴出来……所以这里补上其源码：

```java
/* DoubleCheck的其他部分就不贴出来了，上一篇已经分析过了 */
public static <P extends Provider<T>, T> Provider<T> provider(P delegate) {
    checkNotNull(delegate);
    if (delegate instanceof DoubleCheck) {
        return delegate;
    }
    return new DoubleCheck<T>(delegate);
}
```

其实和`DoubleCheck.lazy()`差不了多少，都是`DoubleCheck`对`Provider`的一次封装，记得上篇文章我们说过**`DoubleCheck.lazy()`绝对不是一个单例**，似乎打了自己的脸；但这里对比下`getMonitor()`和`getLazyCPU()`两个方法，相信你很快能明白为什么`DoubleCheck.provider()`就是单例而`DoubleCheck.lazy()`却不是，因为**是否是单例并不是又`DoubleCheck`决定的，而是使用它们的`Component`桥接类决定的**

再来看下`@Scope`的注释（渣翻译一下，就不贴出来了），该注解是`javax.inject`定义的，默认情况下，注入器是没有范围（Scope）限制的， 即每次都会创建一个实例并注入，之后注入器就再也不关注此实例了；但如果有范围（Scope）限制，注入器就有可能需要记住这个实例，并用于下一次注入中；另外**注入器应该考虑到线程安全问题**，以保证同个注入器在多个线程中注入时不会生成新的实例，这也就是为什么用`DoubleCheck`去封装的原因

# 全局单例

通过上面分析我们了解到使用`@Scope`自定义注解只能实现局部单例，那么怎样实现全局单例呢？比如我希望所有`Activity`中都用同一个实例，或者某几个`Activity`中都用同一个实例，dagger又怎样做到呢？回想一下，其实`@Scope`的局部指的是`Component`桥接类这个范围内，所以**要想共享这个局部单例，自然就要使用同一个`Component`**，那么这个`Component`自然是要放在所有`Activity`（或者所有使用者）都能访问到的地方，比如`Application`（这也是后续会介绍到的`dagger.android`所做的优化）：

```kotlin
@Scope
annotation class ApplicationScope // 定义好范围（Scope）注解

@Module
class ApplicationModule(private val appName: String) { // 提供应用名的数据仓库
    @Qualifier
    annotation class ApplicationName

    @[Provides ApplicationName ApplicationScope] fun provideAppName() = appName // 使用上自定义的@Scope注解
}

@ApplicationScope // 桥接类使用自定义@Scope注解
@Component(modules = [ApplicationModule::class]) // 桥接类中使用上述数据仓库
interface ApplicationComponent {
    @ApplicationModule.ApplicationName fun getAppName(): String // 提供获取应用名的接口
}

class LearnDaggerApplication : Application() {
    // 下面写法是kotlin中一种隐藏真正实现的小技巧
    private lateinit var _component: ApplicationComponent
    val component: ApplicationComponent
        get() = _component

    override fun onCreate() {
        super.onCreate()

        val name = resources.getString(R.string.app_name)
        _component = DaggerApplicationComponent // 将桥接类构建好并存放在Application中
            .builder()
            .applicationModule(ApplicationModule(name))
            .build()
    }
}

// 比如在某个Activity中需要使用到应用名
class CaseActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        /* ... */
        val name = (applicationContext as LearnDaggerApplication).component.getAppName()
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
    }
}
```

如上述代码，其实是**将`Component`放在一个生命周期很长的对象中**（`Application`），以至于这个对象生命周期与应用生命周期一样长，从而实现一种“全局单例”；而其他对象如果需要使用这个“全局单例”，就需要通过那个生命周期很长的对象去获取到`Component`再拿到数据

总而言之，我们需要时刻记住，通过dagger框架的`@Scope`实现的单例仅仅只是针对于某个`Component`的“局部单例”，如果真的想要全局唯一单例，我更推荐使用`object`或其他传统写法

# @Singleton与@Reusable

`@Scope`在`javax.inject`中有一个派生注解`@Singleton`，在dagger中也有一个派生注解`@Reusable`

## @Singleton

千万要分清这个“单例”指的是桥接类（`Component`）中的局部单例，可以把其看做是`javax.inject`中对`@Scope`的默认实现，源码如下：

```java
@Scope
@Documented
@Retention(RUNTIME)
public @interface Singleton {}
```

这个注解的使用与作用与我们自定义`@Scope`注解一模一样

## @Reusable

`@Reusable`比较特别，其不用像`@Scope`一样，在`@Component`注解的接口上也加上`@Reusable`的注解，源码如下：

```java
@Documented
@Beta
@Retention(RUNTIME)
@Scope
public @interface Reusable {}
```

注意`@Beta`注解表示其还处于实验阶段，我个人看法就是省去了我们手动将`@Scope`与`@Component`连接起来的显示声明，大家可以自己试试，这里就不多说了

# 总结

通过`@Scope`，我们可以限制某个数据在某个`Component`中被创建的次数，从而实现“局部单例”。另外**自定义的`@Scope`注解也可用于`@Inject`注解的构造函数上**，大家不妨去试一下

至此我们已经将`javax.inject`中的内容全部介绍完了，下表是一个对其内容的小总结：

| `javax.inject`中的内容  | 小总结                                                       |
| ----------------------- | ------------------------------------------------------------ |
| `@Inject`               | 1. 注解在构造函数上，以生成此类的工厂类（`Factory`），此工厂类用以生产需要注入的依赖对象（注意一个类中仅能`@Inject`注解一个构造函数）<br />2. 注解在变量或方法上，以表明该变量或方法内的所有参数都是需要被注入的，从而生成该类的注入器（`MembersInjector`） |
| `@Qualifier` & `@Named` | `@Qualifier`用来定义相同类型的别名，以解决注入类型相同的问题，通常此注解用在自定义注解上，`@Named`是`javax.inject`对其的一个样例实现 |
| `@Scope` & `@Singleton` | 被`@Scope`注解的数据提供方法（`@Provides`或`@Inject`）在相同`@Scope`注解的`@Component`桥接类中是一个局部单例，仅会调用一次工厂类生成对象实例，`@Singleton`是`javax.inject`对其的一个样例实现 |
| `Provider`              | 用以定义“能够提供数据的对象”的接口，其实现类很多，比如`Factory`、`DoubleCheck`等 |

接下来本系列会介绍dagger中另一个特性——*MultiBinding*，看看在dagger中怎样把依赖注入进`map`和`set`容器中