package com.openle.source.expression;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

/**
 * 公用类
 */
public class Utils {

    public static void main(String[] args) throws Throwable {
        sql.initialize();
        Integer ii = 100;
        PredicateSerializable<?> lambda = (Utils u) -> u.print() == "bbb" && u.id() == 345;
        System.out.println(LambdaParser.parseWhere(lambda));

//        lambda = (Utils u) -> u.getAge() > 18 && u.getClass().getName().toString().equals("abc");
//        System.out.println(LambdaParser.parseWhere(lambda));
        //System.out.println(Lambda2Sql.toSql(lambda));
        //   xxx();
//        MethodHandles.Lookup caller = MethodHandles.lookup();
//        MethodType methodType = MethodType.methodType(Object.class);
//        MethodType actualMethodType = MethodType.methodType(String.class);
//        MethodType invokedType = MethodType.methodType(Supplier.class);
//        CallSite site = LambdaMetafactory.metafactory(caller,
//                "get",
//                invokedType,
//                methodType,
//                caller.findStatic(Utils.class, "print", actualMethodType),
//                methodType);
//        MethodHandle factory = site.getTarget();
//        Supplier<String> r = (Supplier<String>) factory.invoke();
//        System.out.println(r.get());
    }

    public String print() {
        return "hello world";
    }

    public int id() {
        return 123;
    }

    public Integer getAge() {
        return 123;
    }

    public String getVersion() {
        return "hello world";
    }

    private static void xxx() throws NoSuchMethodException, IllegalAccessException, LambdaConversionException, Throwable {

        MethodHandles.Lookup caller = MethodHandles.lookup();
        MethodType getter = MethodType.methodType(String.class);
        MethodHandle target = caller.findVirtual(Utils.class, "getVersion", getter);
        MethodType func = target.type();
        CallSite site = LambdaMetafactory.metafactory(caller,
                "apply",
                MethodType.methodType(Function.class),
                func.generic(), target, func);

        MethodHandle factory = site.getTarget();
        Function r = (Function) factory.invoke();
        System.out.println(r.apply(new Utils()));
    }

    public static String camelToUnderline(String param) {
        Pattern p = Pattern.compile("[A-Z]");
        if (param == null || param.equals("")) {
            return "";
        }
        StringBuilder builder = new StringBuilder(param);
        Matcher mc = p.matcher(param);
        int i = 0;
        while (mc.find()) {
            builder.replace(mc.start() + i, mc.end() + i, "_" + mc.group().toLowerCase());
            i++;
        }

        if ('_' == builder.charAt(0)) {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    public static String getTableName(Class c) {

        String tableName = c.getSimpleName();

        for (Annotation a : c.getAnnotations()) {
            //System.out.println(a.annotationType().getName());
            if (a.annotationType().getName().equals("javax.persistence.Table")) {
                try {
                    Method m = a.annotationType().getMethod("name", new Class[]{});
                    Object obj = m.invoke(a, new Object[]{});
                    System.out.println("getTableName Entity " + c.getName() + "|TableName - " + obj);
                    //System.out.println("Table Name = " + obj);
                    tableName = obj.toString();
                } catch (java.lang.ReflectiveOperationException ex) {
                    Logger.getLogger(From.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return tableName;
    }

    @SuppressWarnings("unchecked")
    protected <T> String getSelectName(Class c, final Function<T, ?> getter) {
        Objects.requireNonNull(c);
        final Method[] method = new Method[1];
        //System.out.println(getter);  
        String name = null;
        try {

            getter.apply((T) Mockito.mock(c, Mockito.withSettings().invocationListeners(methodInvocationReport -> {
                method[0] = ((InvocationOnMock) methodInvocationReport.getInvocation()).getMethod();
            })));
            name = method[0].getName();

        } catch (NoSuchMethodError ex) {
            // 实体类字段不存在则通过异常NoSuchMethodError来获取。
            String msg = ex.getMessage();
            msg = LambdaHelper.restoreSymbol(msg);
            //System.out.println(msg);
            name = msg.substring(msg.lastIndexOf(".") + 1, msg.indexOf("()"));
            //System.out.println(name);
        } catch (ClassCastException ex) {
            // 未传入实体Class则通过异常ClassCastException来获取。
            String msg = ex.getMessage();
            msg = msg.substring(msg.lastIndexOf("cast to") + 8);
            Class clazz = null;
            try {
                clazz = Class.forName(msg);
            } catch (ClassNotFoundException ex1) {
                throw new RuntimeException(ex1);
            }
            System.out.println("msg - " + clazz);
            name = getSelectName(clazz, getter);
        }

        //System.out.println(" - " + name);
        return name;
    }

    //该方法已写入base模块，此处为冗余
    /**
     * 强制删除文件/文件夹(含不为空的文件夹)<br>
     *
     * @param dir
     * @throws IOException
     * @see Files#deleteIfExists(Path)
     * @see Files#walkFileTree(Path, java.nio.file.FileVisitor)
     */
    public static void deleteIfExistsWithNotEmpty(Path dir) throws IOException {
        try {
            Files.deleteIfExists(dir);
        } catch (DirectoryNotEmptyException e) {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return super.postVisitDirectory(dir, exc);
                }
            });
        }
    }
}
