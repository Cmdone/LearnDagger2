# 依赖与注入

都说dagger2是一个依赖注入的框架，那么什么是依赖，什么又是注入呢？下面以显示一台电脑的信息来举例：

- 每台电脑（Computer）有其操作系统（os）和定价（price）
- 电脑内有处理器（CPU），处理器有着自己的序列号（ID）
- 电脑内有内存（Memory），内存有着自己的容量（size）
- 电脑执行（execute）时，会分别执行处理器和内存，它们在执行时都将自己信息以文本方式返回

综上，我们将对应的这几个类都先写好：

```kotlin
class CPU {
    private val id = Random.nextInt()

    fun execute(builder: StringBuilder) { // CPU执行时返回自身序列号信息
        builder.append("CPU Id: ").append(id).append("\n")
    }
}

class Memory(private val size: Int) {
    fun execute(builder: StringBuilder) { // Memory执行时返回自身容量信息
        builder.append("Memory Size: ").append(size).append("MB\n")
    }
}

class Computer(private val os: String, private val price: Int) {
    lateinit var cpu: CPU
    lateinit var memory: Memory

    fun execute(builder: StringBuilder) { // CPU执行时除了返回自身信息，还要将CPU和Memory的信息一并返回
        builder.append("Computer OS: ").append(os).append("\n")
        builder.append("Computer Price: ").append(price).append("\n")
        cpu.execute(builder)
        memory.execute(builder)
    }
}
```

我们的Activity里面有这么一个电脑，并要将其信息显示出来：

```kotlin
class CaseActivity : AppCompatActivity() {
    lateinit var computer: Computer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case)

        computer = Computer("Windows", 6666)
        computer.cpu = CPU()
        computer.memory = Memory(8192)

        show()
    }

    private fun show() {
        val builder = StringBuilder()
        computer.execute(builder)
        text_view.text = builder.toString()
    }
}
```

这里执行是没问题的，但可以发现`Computer`、`CPU`、`Memory`这些对象都是由`Activity`创建的，但`Case01Activity`其实真正需要的只是`Computer`，为了得到这个`Computer`，`Case01Activity`不仅要为`Computer`准备好参数，甚至还要帮其创建`CPU`和`Memory`！也就是说`Activity`不仅仅**依赖**于`Computer`，甚至连`Computer`的**依赖**（`CPU`和`Memory`）也变成`Activity`的**依赖**，这样的耦合程度是相当严重的

上面那段话应该让你明白了什么是依赖，那么注入又是什么意思呢？让我们修改一下上面的例子：

```kotlin
object CaseActivityInjector { // 新创建一个类，目前可以看做是一个工具类
    fun inject(target: CaseActivity) { // 在此方法内创建Computer并注入到目标对象中
        target.computer = Computer("Windows", 6666).apply {
            cpu = CPU()
            memory = Memory(8192)
        }
    }
}

// 修改Activity的onCreate方法如下：
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_case)
    
    CaseActivityInjector.inject(this)

    show()
}
```

如果只是从代码表面来看，无非就是关键代码从三行变成了一行，但为此我们却新建了一个工具类，有些自欺欺人的感觉；但如果我们从更高层次的思想去看待这个改动：

- `Activity`确实只依赖于`Computer`了，而不会依赖于`CPU`和`Memory`，实现了**解耦**
- 原先创建`Computer`、`CPU`和`Memory`的任务交给了`Injector`，实现了**控制反转**
- `Computer`是通过`Injector`的`inject`方法**注入**到`Activity`中的，而不是由`Activity`自行创建的

看到这，你或需对**依赖**和**注入**都有了自己的认知，那么想想这样做的好处是什么？假设我有几十上百个`Activity`都需要显示`Computer`的信息，那么一旦我`Computer`结构改变了（比如添加了硬盘信息），在改动前我只能一个一个`Activity`去添加、修改代码，而改动后，我仅需修改`inject`中的实现。由此看来这个`Injector`是依赖注入中的核心部分，而dagger就是用来帮助我们生成这种**注入器（或者叫控制反转容器）**的框架，当然dagger生成的代码肯定不会像上面一样简单

# 总结

我眼中的依赖注入则是通过第三方容器（注入器、控制反转容器）将目标对象的**依赖**（成员变量）通过赋值的方式**注入**到目标对象中，使得目标对象不用考虑其依赖从哪里来、怎样构建、依赖的依赖等问题，而可以直接使用此依赖，即**仅关注代码中真正起作用的对象**