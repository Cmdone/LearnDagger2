# 目标

我们知道，每台电脑中都有硬盘，并且可能不止一张，每个电脑也会连接很多外设，本文的目的就在于将这些东西用dagger注入到我们的`Computer`中

# @IntoSet & @ElementsIntoSet

每张硬盘有着不同的类型（机械硬盘和固态硬盘），还有者不同的容量，在`Computer`中如果将每张硬盘都单独作为一个成员变量，未免扩展性太低了，因此我们用`Set`容器保存这些硬盘：

```kotlin
class Disk(private val type: Type, private val capacity: Capacity) { // 新建一个硬盘类
    fun mount(builder: StringBuilder) { // 此方法代表硬盘挂载到电脑上
        builder.append(capacity.size).append("G ").append(type.name).append(" mounted").append("\n")
    }

    enum class Type { HARD, SSD } // 硬盘的两种类型：机械和固体

    enum class Capacity(val size: Int) { SMALL(256), NORMAL(512), HUGE(1024) } // 硬盘的几种容量规格
}

abstract class Computer(private val os: String, private val price: Int) {
    /* ... */
    @field:Inject lateinit var disks: Set<Disk> // Computer中所有的硬盘
    /* ... */
    fun execute(builder: StringBuilder) { // CPU执行时除了返回自身信息，还要将CPU和Memory的信息一并返回
        /* ... */
        disks.forEach { it.mount(builder) } // 挂载所有硬盘
    }
}
```

这里我们自然可以让`@Provides`注解的方法返回一个`Set<Disk>`容器类，但在dagger中还有其他方式也可以做到：

```kotlin
@Module
class DiskModule { // 新建一个硬盘数据仓库，用以给Computer提供硬盘
    @[Provides IntoSet] fun provideSmallHardDisk() = Disk(Disk.Type.HARD, Disk.Capacity.SMALL)
    @[Provides IntoSet] fun provideHugeHardDisk() = Disk(Disk.Type.HARD, Disk.Capacity.HUGE)

    @[Provides ElementsIntoSet]
    fun provideSSD() = setOf(
        Disk(Disk.Type.SSD, Disk.Capacity.SMALL),
        Disk(Disk.Type.SSD, Disk.Capacity.NORMAL)
    )
}

@Component(modules = [MemoryModule::class, DiskModule::class]) // 在Computer使用的Component桥接类中添加上硬盘数据仓库
interface ComputerComponent {
    fun inject(target: Computer)
}
```

没错，就像上面那样使用`@IntoSet`和`@ElementsIntoSet`注解，就可以实现`Set`容器的注入了

照旧我们来看看生成的源码，打开相关`Factory`工厂类我们发现和普通使用`@Provides`生成的工厂类并无区别，看来又是`@Component`生成的桥接类有变动了：

```java
public final class DaggerComputerComponent implements ComputerComponent {
  /* ... */
  private final DiskModule diskModule;
  /* ... */
  private Set<Disk> getSetOfDisk() {
    return SetBuilder.<Disk>newSetBuilder(3)
        .add(DiskModule_ProvideSmallHardDiskFactory.provideSmallHardDisk(diskModule))
        .add(DiskModule_ProvideHugeHardDiskFactory.provideHugeHardDisk(diskModule))
        .addAll(DiskModule_ProvideSSDFactory.provideSSD(diskModule))
        .build();
  }
  /* ... */
  private Computer injectComputer(Computer instance) {
	/* ... */
    Computer_MembersInjector.injectSetDisks(instance, getSetOfDisk());
    return instance;
  }
  /* ... */
}
```

多了一个`getSetOfDisk()`方法，这个方法将各个`@Provides`提供的数据整合在一起，通过`SetBuilder`的Builder模式构建新的`Set`容器，对于`@IntoSet`标记的单个元素调用其`add()`方法，而对于`@ElementsIntoSet`标记的容器则调用`addAll()`方法：

```java
public final class SetBuilder<T> {
  private static final String SET_CONTRIBUTIONS_CANNOT_BE_NULL =
      "Set contributions cannot be null";
  private final List<T> contributions;

  private SetBuilder(int estimatedSize) {
    contributions = new ArrayList<>(estimatedSize);
  }
    
  public static <T> SetBuilder<T> newSetBuilder(int estimatedSize) {
    return new SetBuilder<T>(estimatedSize);
  }

  public SetBuilder<T> add(T t) {
    contributions.add(checkNotNull(t, SET_CONTRIBUTIONS_CANNOT_BE_NULL));
    return this;
  }

  public SetBuilder<T> addAll(Collection<? extends T> collection) {
    for (T item : collection) {
      checkNotNull(item, SET_CONTRIBUTIONS_CANNOT_BE_NULL);
    }
    contributions.addAll(collection);
    return this;
  }

  public Set<T> build() {
    switch (contributions.size()) {
      case 0:
        return Collections.emptySet();
      case 1:
        return Collections.singleton(contributions.get(0));
      default:
        return Collections.unmodifiableSet(new HashSet<>(contributions));
    }
  }
}
```

`SetBuilder`类整体上看起来就非常简单了，先用`ArrayList`将所有元素存起来，`build()`的时候再生成新的`Set`容器，这里需要注意**生成的`Set`容器是不能修改的**，即调用`add()`、`set()`、`remove()`等都是会抛异常的

另外`Set`容器的注入还可以搭配`@Qualifier`（注意是对`Set`容器的别名，而不是其中元素的别名）、`Provider`、`Lazy`等（例如`Lazy<Set<T>>`），大家自行尝试吧

# @IntoMap & 各种“Key”

我们知道`Map`相较于`Set`，除了将目标对象存入容器，还得为其指定一个Key值以区分它们。现在有鼠标、键盘、音响等外设需要连接到`Computer`上：

```kotlin
interface Device { // 外设接口
    fun connect(builder: StringBuilder) // 每种外设都需要连接到Computer
}

class Mouse : Device { // 鼠标
    override fun connect(builder: StringBuilder) {
        builder.append(" move").append("\n")
    }
}

class Keyboard : Device { // 键盘
    override fun connect(builder: StringBuilder) {
        builder.append(" press").append("\n")
    }
}

class Sound : Device { // 音响
    override fun connect(builder: StringBuilder) {
        builder.append(" play").append("\n")
    }
}

abstract class Computer(private val os: String, private val price: Int) {
    /* ... */
    @set:Inject
    lateinit var devices: Map<String, @JvmSuppressWildcards Device> // 使用Map存放连上电脑的外设，另外@JvmSuppressWildcards下面有说明
    /* ... */
    fun execute(builder: StringBuilder) { // CPU执行时除了返回自身信息，还要将CPU和Memory的信息一并返回
        /* ... */
        devices.forEach { (name, device) -> // 连接各外设
            builder.append(name).append(": ")
            device.connect(builder)
        }
    }
}
```

特别注意因为`Device`是一个抽象类，并且kotlin中对`map`的定义为`Map<K, out V>`，因此在编译时dagger将其看做`Map<K, ? extends V>`，但根本不会有`? extends V`这种类型，所以需要添加`@JvmJvmSuppressWildcards`这个注解，让其在编译时无视掉这种泛型变换。具体可以看下[stackoverflow上的文章](https://stackoverflow.com/questions/43141740/dagger-2-multibindings-with-kotlin/43149382#43149382)

另外还要注意这里的`Map`容器是以`String`作为Key值的，因此需要用到dagger提供的`@StringKey`和`@IntoMap`注解：

```kotlin
@Module
class DeviceModule { // 外设数据仓库，注意除了需要使用@IntoMap，还需要用@StringKey指定数据在map容器中对应的key值
    @[Provides IntoMap StringKey("Mouse")]
    fun provideMouse(): Device = Mouse()

    @[Provides IntoMap StringKey("Keyboard")]
    fun provideKeyboard(): Device = Keyboard()

    @[Provides IntoMap StringKey("Sound")]
    fun provideSound(): Device = Sound()
}

@Component(modules = [MemoryModule::class, DiskModule::class, DeviceModule::class]) // 在Computer使用的Component桥接类中添加Device的数据仓库
interface ComputerComponent {
    fun inject(target: Computer)
}
```

我想你已经猜到生成的`Component`长啥样了吧：

```java
public final class DaggerComputerComponent implements ComputerComponent {
  /* ... */
  private final DeviceModule deviceModule;
  /* ... */
  private Map<String, Device> getMapOfStringAndDevice() {
    return MapBuilder.<String, Device>newMapBuilder(3)
        .put("Mouse", DeviceModule_ProvideMouseFactory.provideMouse(deviceModule))
        .put("Keyboard", DeviceModule_ProvideKeyboardFactory.provideKeyboard(deviceModule))
        .put("Sound", DeviceModule_ProvideSoundFactory.provideSound(deviceModule))
        .build();
  }
  /* ... */
  private Computer injectComputer(Computer instance) {
    /* ... */
    Computer_MembersInjector.injectSetDevices(instance, getMapOfStringAndDevice());
    return instance;
  }
}
```

套路简直和`@IntoSet`一模一样，直接看`MapBuilder`吧

```java
public final class MapBuilder<K, V> {
  private final Map<K, V> contributions;

  private MapBuilder(int size) {
    contributions = newLinkedHashMapWithExpectedSize(size);
  }
    
  public static <K, V> MapBuilder<K, V> newMapBuilder(int size) {
    return new MapBuilder<>(size);
  }

  public MapBuilder<K, V> put(K key, V value) {
    contributions.put(key, value);
    return this;
  }

  public MapBuilder<K, V> putAll(Map<K, V> map) {
    contributions.putAll(map);
    return this;
  }

  public Map<K, V> build() {
    switch (contributions.size()) {
      case 0:
        return Collections.emptyMap();
      default:
        return Collections.unmodifiableMap(contributions);
    }
  }
}
```

`MapBuilder`整体上也是非常简单的，内部先用`LinkedHashMap`保存数据，`build()`的时候生成新的`map`容器，同样也要注意**生成的`map`容器是不能修改的**

这里除了通过`@StringKey`指定`map`的Key为`String`类型以外，还有`@ClassKey`、`@IntKey`、`@LongKey`这些注解辅助`map`的注入

# 有待完善

有时候我们`Map`的Key值并非是`String`、`Class`、`Int`、`Long`中的其中一种，可能是几种类型的复合、含有数据甚至是一个自定义的类，dagger给出了`@MapKey`+`@AutoAnnotation`的方案来解决复合类型的Key值，但需要额外引入google的`AutoValue`框架，而且并不能解决自定义类作为Key值的注入。这里就不再过多尝试，如果真的有这种需求场景，我们也可以通过`@Provides`直接返回对应`Map`（这种方法还适用于编译时期不能确定Key值，需要运行时动态注入的情况）

# 总结

当我们需要将依赖注入到`Set`或`Map`容器时，可以考虑使用`@IntoSet`、`@ElementsIntoSet`、`@IntoMap`这些注解，但一些复杂情况还是建议通过`@Provides`返回`Set`或`Map`容器