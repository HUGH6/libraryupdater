package core.version;

/**
 * 该类用于控制加载不同版本软件库的类
 * 由于在迁移某一个调用点到新版本api时，我们需要通过编译和运行单测的方式检验代码，此时需要程序依赖新版本软件库
 * 而其余未变更部分为了不破坏程序的正确性，需要继续使用旧版本的api，因此程序中需要同时使用两个版本的同一软件库
 * 考虑使用自定义类加载器的方式来实现不同版本类的加载
 */
public class LibraryVersionController {
//    URLClassLoader loader1 = new URLClassLoader(new URL[] {new File("httpclient-v1.jar").toURL()}, Thread.currentThread().getContextClassLoader());
//    URLClassLoader loader2 = new URLClassLoader(new URL[] {new File("httpclient-v2.jar").toURL()}, Thread.currentThread().getContextClassLoader());
//
//    Class<?> c1 = loader1.loadClass("com.abc.Hello");
//
//    Class<?> c2 = loader2.loadClass("com.abc.Hello");
//
//    BaseInterface i1 = (BaseInterface) c1.newInstance();
//
//    BaseInterface i2 = (BaseInterface) c2.newInstance();
//    Class<?> c1 = null;
//
//
//    public static void main(String[] args) {
//        URLClassLoader loader1 = new URLClassLoader(new URL[] {new File("httpclient-v1.jar").toURL()}, Thread.currentThread().getContextClassLoader());
//        Class<?> c1 = loader1.loadClass("com.abc.Hello");
//
//        c1.get
//    }
}
