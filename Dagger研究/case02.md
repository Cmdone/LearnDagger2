# Kotlin中的注解

在开始讲解注解之前，先看下kotlin中的注解有何不同

我们知道以下kotlin代码：

```kotlin
class Test(var temp: Int)
```

转换为Java代码后是：

```java
public final class Test {
   private int temp;

   public final int getTemp() {
      return this.temp;
   }

   public final void setTemp(int var1) {
      this.temp = var1;
   }

   public Test(int temp) {
      this.temp = temp;
   }
}
```

那么问题来了：

- 如何给构造函数添加注解？
- 如果代码为`class Test(@Ann var temp: Int)`，那么`@Ann`这个注解是作用于哪里？（构造函数中的参数、变量、get或是set方法等）
- 如何在kotlin中指定注解的目标？

## 构造函数添加注解

这个问题其实不难，研究过kotlin语法的应该都知道kotlin有`constructor`关键字，上述写法是省略掉此关键字之后的主构造函数的写法，如果要使用注解，则需要显示写出`constructor`关键字，如下：

```kotlin
class Test @Ann constructor(var temp: Int)
```

## 注解的默认目标

修改一下代码如下：

```kotlin
class Test constructor(@Ann var temp: Int) {
    @Ann var foo = "Cmd"
}
```

猜猜这两个`@Ann`注解分别在什么地方？

```java
public final class Test {
   @NotNull
   private String foo;
   private int temp;

   /** @deprecated */
   // $FF: synthetic method
   @Ann
   public static void foo$annotations() {
   }
    
   /* 省略掉get/set方法 */

   public Test(@Ann int temp) {
      this.temp = temp;
      this.foo = "Cmd";
   }
}
```

kotlin中注解首先根据`@Target`选择目标，如果有多个指定目标都符合时，有如下优先级：

1. `param`：即方法中的参数
2. `property`：一个对java不可见的属性（如上`foo$annotations`）
3. `filed`：变量

所以在上述代码在构造函数中`@Ann`的目标时构造函数的入参，而`foo`前的`@Ann`目标则是属性

## 指定注解目标

对于自定义的注解，也许我们可以指定`@Target`限制注解目标范围，但对于三方提供的注解（例如`javax.inject`中的`@Inject`），怎样指定注解的目标呢？kotlin给出了一系列关键字来辅助：

```kotlin
class Test constructor(
    @set:Ann var i1: Int, // 注解在set方法上
    @param:Ann val i2: Int // 注解在方法参数上
) {
    @setparam:Ann var foo = "Cmd" // 注解在set方法的参数上
    @get:Ann val bar = "Mei" // 注解在get方法上
    @field:Ann val zoo = "Zoo" // 注解在后端变量上
}
```

常用的关键字就上面那些，猜猜上述代码转为Java代码后是怎样的？

```java
/* 已省略掉一些不关心的地方 */
public final class Test {
   @Ann
   private final String zoo;

   public final void setFoo(@Ann @NotNull String var1) {
      Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
      this.foo = var1;
   }

   @Ann
   public final String getBar() {
      return this.bar;
   }

   @Ann
   public final void setI1(int var1) {
      this.i1 = var1;
   }

   public Test(int i1, @Ann int i2) {
      this.i1 = i1;
      this.i2 = i2;
      this.foo = "Cmd";
      this.bar = "Mei";
      this.zoo = "Zoo";
   }
}
```

## 补充

最后再补充一个同一目标多个注解的写法：

```kotlin
class Test constructor(@set:[Ann Ann2] var temp: Int)
```

更多关于kotlin中使用注解的东西还请查阅[官网](https://www.kotlincn.net/docs/reference/annotations.html)

# @Inject注解

`@Inject`是`javax.inject`中定义的注解，其可以作用于构造函数、变量和方法上，我们通过编译触发dagger的注解处理器，来看看它做了什么（`kapt`生成的代码通常在`build/generated/source/kapt`下）

## 注解在构造函数上

我们分别在`CPU`和`Memory`的构造函数上添加注解，分别看看对于无参和有参构造函数，`@Inject`会生成什么

```kotlin
class CPU @Inject constructor() { /* ... */ }
class Memory @Inject constructor(private val size: Int) { /* ... */ }
```

编译后，生成了`CPU_Factory`和`Memory_Factory`两个新类，这里先看下`CPU_Factory`：

```java
public final class CPU_Factory implements Factory<CPU> {
  private static final CPU_Factory INSTANCE = new CPU_Factory(); // 饿汉单例

  @Override
  public CPU get() { // 生产一个CPU对象
    return new CPU();
  }

  public static CPU_Factory create() { // 目前此方法不是我们关注的重点
    return INSTANCE;
  }

  public static CPU newInstance() {
    return new CPU();
  }
}
```

正如其名`Factory`，这是一个工厂类，所生产的对象是`CPU`，提供了两种生产方法——非静态的`get()`和静态的`newInstance()`；而此工厂又是一个饿汉单例模式，不过我们这里仅关注**工厂模式**。接下来看下其实现的`Factory`这个接口：

```java
public interface Factory<T> extends Provider<T> {} // 这是dagger中定义的接口
public interface Provider<T> { // 这是javax中定义的接口
    T get();
}
```

`Provider<T>`的是专门为注入器设计的接口，通过`get`方法返回一个可被注入的对象实例，对象类型为`T`；而`Factory<T>`则是一个未被范围标识（unscoped）的`Provider`

什么是未被范围标识这里并不是重点，总之`Factory`就是工厂类的接口，而dagger**对于有`@Inject`注解的构造函数的类，会根据被注解的构造函数生成一个实现了`Factory`接口的工厂类**

顺便一提，同一个类中若有多个构造函数，则`@Inject`仅能注解其中一个，不然工厂类就不知道用哪一个构造函数来生产对象了。我们知道在kotlin中如果构造函数有参数默认值，会被编译成多个重载构造函数，所以**`@Inject`不能用于有参数默认值的构造函数上**，否则编译后`@Inject`就会作用于多个构造函数了

再看看对有参构造函数注解`@Inject`生成的`Memory_Factory`类：

```java
public final class Memory_Factory implements Factory<Memory> {
  private final Provider<Integer> sizeProvider; // 注意这里也是一个Provider

  public Memory_Factory(Provider<Integer> sizeProvider) {
    this.sizeProvider = sizeProvider;
  }

  @Override
  public Memory get() {
    return new Memory(sizeProvider.get());
  }

  public static Memory_Factory create(Provider<Integer> sizeProvider) {
    return new Memory_Factory(sizeProvider);
  }

  public static Memory newInstance(int size) {
    return new Memory(size);
  }
}
```

因为构造`Memory`需要依赖一个`Integer`对象，所以这个工厂不再是单例，并且有了一个`Provider<Integer>`作为成员变量提供构造`Memory`时所需的参数，总之大同小异，其本质也就是生产`Memory`的工厂类

同理我们可以给`Computer`的构造函数也加上`@Inject`，并且可以改写一下我们的`Injector`类了，如下：

```kotlin
object CaseActivityInjector {
    fun inject(target: CaseActivity) {
        target.computer = Computer_Factory.newInstance("Windows", 6666).apply {
            cpu = CPU_Factory.newInstance()
            memory = Memory_Factory.newInstance(8192)
        }
    }
}
```

## 注解在变量和方法上

为了方便对比注解在变量和方法上的区别，此刻我们有新的需求，要求显示`Computer`信息前，还要显示当前页面的生成时间，我们在`Activity`中添加`@Inject`注解，如下：

```kotlin
class CaseActivity : AppCompatActivity() {
    @field:Inject
    lateinit var computer: Computer
    @set:Inject
    lateinit var timestamp: Date
    /* ... */
}
```

编译后可以发现dagger又生成了新的类——`CaseActivity_MembersInjector`：

```java
public final class CaseActivity_MembersInjector implements MembersInjector<CaseActivity> {
  // Provider上面说到过，是可以提供对象实例的接口
  private final Provider<Computer> computerProvider;
  private final Provider<Date> p0Provider;

  public CaseActivity_MembersInjector(
      Provider<Computer> computerProvider, Provider<Date> p0Provider) {
    this.computerProvider = computerProvider;
    this.p0Provider = p0Provider;
  }

  public static MembersInjector<CaseActivity> create(
      Provider<Computer> computerProvider, Provider<Date> p0Provider) {
    return new CaseActivity_MembersInjector(computerProvider, p0Provider);
  }

  @Override
  public void injectMembers(CaseActivity instance) {
    injectComputer(instance, computerProvider.get());
    injectSetTimestamp(instance, p0Provider.get());
  }

  public static void injectComputer(CaseActivity instance, Computer computer) {
    instance.computer = computer;
  }

  public static void injectSetTimestamp(CaseActivity instance, Date p0) {
    instance.setTimestamp(p0);
  }
}
```

很明显，这个类的作用和我们自己写的`Injector`一样，是一个**注入器类**，将目标类（Activity）的依赖（computer、timestamp）注入到目类中；提供了两种注入方法——非静态的`injectMembers()`和静态的`injectComputer() & injecterTimestamp()`。

这里注意一下`@Inject`注解在变量和方法上的区别：前者的注入方式是直接对被`@Inject`注解的成员变量赋值；而后者的注入方式则是调用被`@Inject`注解的方法，即实际的注入逻辑还是交给目标类（这里说明一下，**被`@Inject`注释的方法可以没有参数也可以有多个参数**）

接下来看下上面`CaseActivity_MenbersInjector`实现的接口`MenebersInjector`：

```java
public interface MembersInjector<T> { // dagger中定义的接口
      void injectMembers(T instance);
}
```

这个接口就是用作**将依赖注入到目标类的变量和方法中**的，目标类的类型为`T`，调用`injectMembers()`方法以执行依赖注入

再次根据`@Inject`生成的工厂和注入器来改写我们的代码，不过这里注意不能再使用`Injector`这个名字了，因为注入器已经被dagger生成了，我们现在需要做的是**使用注入器将工厂生产的对象注入到目标类中**，那么就不妨叫做`Bridge`吧（为什么这么叫之后会说明）：

```kotlin
object CaseActivityBridge {
    fun inject(target: CaseActivity) {
        // 创建依赖
        val computer = Computer_Factory.newInstance("Windows", 6666).apply {
            cpu = CPU_Factory.newInstance()
            memory = Memory_Factory.newInstance(8192)
        }
        val timestamp = Date(System.currentTimeMillis())
        // 使用注入器注入
        CaseActivity_MembersInjector.injectComputer(target, computer)
        CaseActivity_MembersInjector.injectSetTimestamp(target, timestamp)
    }
}

/* CaseActivity的onCreate方法 */
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_case)

    CaseActivityBridge.inject(this)

    show()
}
```

# 桥接模式

桥接模式是将多个维度的抽象与实现连接起来，这里和标准的桥接模式我觉得就只差一层抽象而已，这里更多关注的是**将`Activity`这个维度和注入器或工厂生产的对象实例这个维度连接起来**，起到一个“桥梁”的作用。所以我将上面那个类称为`Bridge`，这个桥梁的具体桥接方式是通过注入器将对象实例注入到目标类`Activity`中（另一个角度来看，桥接其实是一种实现组合结构的方式）

# 总结

- `@Inject`注解在构造函数上时，会根据被注解的构造函数生成该类的工厂类
- `@Inject`注解在变量和方法上时，表示这些变量和方法需要被注入，会生成相应的注入器

如果被依赖对象只能通过`@Inject`注解构造函数生成工厂类的话，未免局限性太大了，比如被依赖对象是三方库中的对象，难道要修改库中源代码再编译吗？那系统对象呢？dagger当然也想到了这点