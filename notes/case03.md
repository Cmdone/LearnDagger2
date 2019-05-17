# 使用@Module和@Provides注解

还记得上一节中添加的需求吗？

> 要求显示`Computer`信息前，还要显示当前页面的生成时间

为此，我们对`Activity`添加了如下成员变量：

```kotlin
@set:Inject lateinit var timestamp: Date
```

它是一个`java.util.Date`对象，自然我们是没法将`@Inject`注解添加到其构造函数上的，因此我们也就没有`Date`的工厂类，所以之前我们在`Bridge`中是这样写的：

```kotlin
// 创建依赖
val timestamp = Date(System.currentTimeMillis())
// 使用注入器注入
CaseActivity_MembersInjector.injectSetTimestamp(target, timestamp)
```

那么有没有办法让dagger生成一个`Date`的工厂类方便我们调用呢？答案是通过`@Module`和`@Provides`注解，如下：

```kotlin
@Module // 此注解表明将根据该类中的信息生成一个工厂类，另外该类将作为一个数据仓库类（与MVC、MVP等中的M概念一样）
class TimestampModule { // 通常用@Module注解的类名都以Module结尾
    @Provides // 此注解表明该工厂类和数据仓库会根据被注解的方法生成和提供一个对象实例（回想下之前提到过的Provider接口）
    fun provideTimestamp() = Date(System.currentTimeMillis()) // 通常用@Provides注解的方法名都以provide开头
}
```

`@Module`和`@Provides`都是dagger中定义的注解，这里特别说明一下dagger对于`@Provides`注解的方法默认是不允许返回空的，如果一定要返回空需要用`javax.annotation.Nullable`或`android.support.annotation.Nullable`注解该方法；但kotlin因为天生具有判空语法，只要将方法返回值写清楚（可空或不可空）就可以了

编译一下，果然生成了新的类——`TimestampModule_ProvideTimestampFactory`，没错，看名字就是我们熟悉的`Factory`工厂类：

```java
public final class TimestampModule_ProvideTimestampFactory implements Factory<Date> {
  private final TimestampModule module; // 对我们定义的Module的引用

  public TimestampModule_ProvideTimestampFactory(TimestampModule module) {
    this.module = module;
  }

  @Override
  public Date get() {
    return provideTimestamp(module);
  }

  public static TimestampModule_ProvideTimestampFactory create(TimestampModule module) { // 目前此方法不是我们关注的重点
    return new TimestampModule_ProvideTimestampFactory(module);
  }

  public static Date provideTimestamp(TimestampModule instance) {
    return Preconditions.checkNotNull(
        instance.provideTimestamp(), "Cannot return null from a non-@Nullable @Provides method");
  }
}
```

不过有别于之前`@Inject`注解在构造函数上生成的工厂，可以看到无论是非静态的`get()`还是静态的`provideTimestamp()`工厂方法，其实都是对`TimestampModule`代理（也许我可以称之为代理工厂）。

有了这个类，我们又可以再次对`Bridge`类进行修改了

```kotlin
/* CaseActivityBridge.inject()方法 */
// 创建数据仓库
val timestampModule = TimestampModule()
// 创建timestamp依赖
val timestamp = TimestampModule_ProvideTimestampFactory.provideTimestamp(timestampModule)
// 使用注入器注入
CaseActivity_MembersInjector.injectSetTimestamp(target, timestamp)
```

# 含参的@Provides方法

之前讲解`@Inject`注解构造函数时，有区分有参和无参的构造函数，结果发现使用`@Inject`注解有参的构造函数生成的工厂类比无参构造函数多了：

- `Provider`类型的成员变量，其所提供的构造函数所需的参数
- 非静态的工厂方法`get()`中使用`Provider`中提供的数据作为构造函数的参数来产生新的对象实例
- 静态工厂方法`newInstance()`参数列表中中多出了构造函数所需要的参数

那么既然`@Module`+`@Provides`也是生成工厂类，对于有参的`@Provides`方法又会是怎样的呢？这里我们假定`Memory`是一个第三方类，需要我们用`@Module`+`@Provides`辅助其生成工厂类：

```kotlin
class Memory(private val size: Int) { // 去掉Memory构造函数上的@Inject注解
    fun execute(builder: StringBuilder) { // Memory执行时返回自身容量信息
        builder.append("Memory Size: ").append(size).append("MB\n")
    }
}

/* 编写MemoryModule类 */
@Module
class MemoryModule {
    @Provides
    fun provideMemory(size: Int) = Memory(size) // 注意这里@Provides注解的方法需要传参
}
```

编译一下，看下生成的`MemoryModule_ProvideMemoryFactory`类：

```java
public final class MemoryModule_ProvideMemoryFactory implements Factory<Memory> {
  private final MemoryModule module;

  private final Provider<Integer> sizeProvider; // 与@Inject注解有参构造函数生成的工厂类一样，多出了需要用于参数的Provider

  public MemoryModule_ProvideMemoryFactory(MemoryModule module, Provider<Integer> sizeProvider) {
    this.module = module;
    this.sizeProvider = sizeProvider;
  }

  @Override
  public Memory get() { // 使用Provider中提供的数据作为构造函数的参数来产生新的对象实例
    return provideMemory(module, sizeProvider.get());
  }

  public static MemoryModule_ProvideMemoryFactory create( // 目前此方法不是我们关注的重点
      MemoryModule module, Provider<Integer> sizeProvider) {
    return new MemoryModule_ProvideMemoryFactory(module, sizeProvider);
  }

  public static Memory provideMemory(MemoryModule instance, int size) { // 参数列表中中多出了构造函数所需要的参数
    return Preconditions.checkNotNull(
        instance.provideMemory(size), "Cannot return null from a non-@Nullable @Provides method");
  }
}
```

可以看到和`@Inject`一模一样，即工厂类添加`Provider`类型的成员变量，非静态工厂方法中用`Provider`提供的数据作为参数，非静态工厂方法中需要传入所需参数，最终还是对`MemoryModule`的代理

我们再次对`Bridge`做出改造：

```kotlin
// 创建Memory依赖，注意这里先创建了数据仓库Module，再通过工厂方法创建Memory实例
memory = MemoryModule_ProvideMemoryFactory.provideMemory(MemoryModule(), 8192)
```

# 总结

`@Module`+`@Provides`为生成的工厂类添加了更多的灵活性，对于依赖于三方库中、系统中等的类，也可以使用工厂方法来实现依赖注入

至此我们要么是手写`Injector`类，要么是手写`Bridge`类，其中注入的逻辑虽然抽象层次越来越高，但还是需要我们手写，dagger有没有办法代替我们写这些逻辑呢？答案就是将要介绍的`@Component`注解