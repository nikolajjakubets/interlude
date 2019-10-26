//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.scripts;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import l2.commons.compiler.Compiler;
import l2.commons.compiler.MemoryClassLoader;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.Quest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scripts {
  private static final Logger _log = LoggerFactory.getLogger(Scripts.class);
  private static final Scripts _instance = new Scripts();
  public static final Map<Integer, List<Scripts.ScriptClassAndMethod>> dialogAppends = new HashMap();
  public static final Map<String, Scripts.ScriptClassAndMethod> onAction = new HashMap();
  public static final Map<String, Scripts.ScriptClassAndMethod> onActionShift = new HashMap();
  private final Compiler compiler = new Compiler();
  private final Map<String, Class<?>> _classes = new TreeMap();

  public static final Scripts getInstance() {
    return _instance;
  }

  private Scripts() {
    this.load();
    this.loadExt();
  }

  private void load() {
    _log.info("Scripts: Loading...");
    List<Class<?>> classes = new ArrayList();
    boolean result = false;
    File f = new File("scripts.jar");
    if (f.exists()) {
      JarInputStream stream = null;
      MemoryClassLoader classLoader = new MemoryClassLoader();

      try {
        stream = new JarInputStream(new FileInputStream(f));
        JarEntry entry = null;

        while((entry = stream.getNextJarEntry()) != null) {
          if (!entry.getName().contains(ClassUtils.INNER_CLASS_SEPARATOR) && entry.getName().endsWith(".class")) {
            String name = entry.getName().replace(".class", "").replace("/", ".");
            Class<?> clazz = classLoader.loadClass(name);
            if (!Modifier.isAbstract(clazz.getModifiers())) {
              classes.add(clazz);
            }
          }
        }

        result = true;
      } catch (Exception var12) {
        _log.error("Fail to load scripts.jar!", var12);
        classes.clear();
      } finally {
        IOUtils.closeQuietly(stream);
      }
    }

    if (!result) {
      result = this.load(classes, "");
    }

    if (!result) {
      _log.error("Scripts: Failed loading scripts!");
      Runtime.getRuntime().exit(0);
    } else {
      _log.info("Scripts: Loaded " + classes.size() + " classes.");

      for(int i = 0; i < classes.size(); ++i) {
        Class<?> clazz = (Class)classes.get(i);
        this._classes.put(clazz.getName(), clazz);
      }

    }
  }

  private void loadExt() {
    _log.info("Extensions: Loading...");
    List<Class<?>> classes = new ArrayList();
    boolean result = false;
    File[] extFiles = (new File(".")).listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        return pathname.getName().endsWith(".ext.jar");
      }
    });
    File[] var4 = extFiles;
    int i = extFiles.length;

    for(int var6 = 0; var6 < i; ++var6) {
      File extFile = var4[var6];
      if (extFile.exists()) {
        JarInputStream stream = null;
        MemoryClassLoader classLoader = new MemoryClassLoader();

        try {
          stream = new JarInputStream(new FileInputStream(extFile));
          JarEntry entry = null;

          while((entry = stream.getNextJarEntry()) != null) {
            if (!entry.getName().startsWith("java/") && !entry.getName().startsWith("l2/authserver") && !entry.getName().startsWith("l2/commons") && !entry.getName().startsWith("l2/gameserver") && !entry.getName().contains(ClassUtils.INNER_CLASS_SEPARATOR) && entry.getName().endsWith(".class")) {
              String name = entry.getName().replace(".class", "").replace("/", ".");
              Class<?> clazz = classLoader.loadClass(name);
              if (!Modifier.isAbstract(clazz.getModifiers())) {
                classes.add(clazz);
              }
            }
          }

          result = true;
        } catch (Exception var16) {
          _log.error("Failed to load \"" + extFile + "\"!", var16);
          classes.clear();
        } finally {
          IOUtils.closeQuietly(stream);
        }
      }
    }

    if (!result) {
      this.load(classes, "");
    }

    _log.info("Extensions: Loaded " + classes.size() + " extension classes.");

    for(i = 0; i < classes.size(); ++i) {
      Class<?> clazz = (Class)classes.get(i);
      this._classes.put(clazz.getName(), clazz);
    }

  }

  public void init() {
    Iterator var1 = this._classes.values().iterator();

    while(true) {
      Class clazz;
      do {
        if (!var1.hasNext()) {
          return;
        }

        clazz = (Class)var1.next();
        this.addHandlers(clazz);
      } while(Config.DONTLOADQUEST && ClassUtils.isAssignable(clazz, Quest.class));

      if (ClassUtils.isAssignable(clazz, ScriptFile.class)) {
        try {
          ((ScriptFile)clazz.newInstance()).onLoad();
        } catch (Exception var4) {
          _log.error("Scripts: Failed running " + clazz.getName() + ".onLoad()", var4);
        }
      }
    }
  }

  public boolean reload() {
    _log.info("Scripts: Reloading...");
    return this.reload("");
  }

  public boolean reload(String target) {
    List<Class<?>> classes = new ArrayList();
    if (!this.load(classes, target)) {
      _log.error("Scripts: Failed reloading script(s): " + target + "!");
      return false;
    } else {
      _log.info("Scripts: Reloaded " + classes.size() + " classes.");

      for(int i = 0; i < classes.size(); ++i) {
        Class<?> clazz = (Class)classes.get(i);
        Class<?> prevClazz = (Class)this._classes.put(clazz.getName(), clazz);
        if (prevClazz != null) {
          if (ClassUtils.isAssignable(prevClazz, ScriptFile.class)) {
            try {
              ((ScriptFile)prevClazz.newInstance()).onReload();
            } catch (Exception var7) {
              _log.error("Scripts: Failed running " + prevClazz.getName() + ".onReload()", var7);
            }
          }

          this.removeHandlers(prevClazz);
        }

        if (!Config.DONTLOADQUEST || !ClassUtils.isAssignable(clazz, Quest.class)) {
          if (ClassUtils.isAssignable(clazz, ScriptFile.class)) {
            try {
              ((ScriptFile)clazz.newInstance()).onLoad();
            } catch (Exception var8) {
              _log.error("Scripts: Failed running " + clazz.getName() + ".onLoad()", var8);
            }
          }

          this.addHandlers(clazz);
        }
      }

      return true;
    }
  }

  public void shutdown() {
    Iterator var1 = this._classes.values().iterator();

    while(var1.hasNext()) {
      Class<?> clazz = (Class)var1.next();
      if (!ClassUtils.isAssignable(clazz, Quest.class) && ClassUtils.isAssignable(clazz, ScriptFile.class)) {
        try {
          ((ScriptFile)clazz.newInstance()).onShutdown();
        } catch (Exception var4) {
          _log.error("Scripts: Failed running " + clazz.getName() + ".onShutdown()", var4);
        }
      }
    }

  }

  private boolean load(List<Class<?>> classes, String target) {
    Collection<File> scriptFiles = Collections.emptyList();
    File file = new File(Config.DATAPACK_ROOT, "data/scripts/" + target.replace(".", "/") + ".java");
    if (file.isFile()) {
      scriptFiles = new ArrayList(1);
      ((Collection)scriptFiles).add(file);
    } else {
      file = new File(Config.DATAPACK_ROOT, "data/scripts/" + target);
      if (file.isDirectory()) {
        scriptFiles = FileUtils.listFiles(file, FileFilterUtils.suffixFileFilter(".java"), FileFilterUtils.directoryFileFilter());
      }
    }

    if (((Collection)scriptFiles).isEmpty()) {
      return false;
    } else {
      boolean success;
      if (success = this.compiler.compile((Collection)scriptFiles)) {
        MemoryClassLoader classLoader = this.compiler.getClassLoader();
        String[] var8 = classLoader.getLoadedClasses();
        int var9 = var8.length;

        for(int var10 = 0; var10 < var9; ++var10) {
          String name = var8[var10];
          if (!name.contains(ClassUtils.INNER_CLASS_SEPARATOR)) {
            try {
              Class<?> clazz = classLoader.loadClass(name);
              if (!Modifier.isAbstract(clazz.getModifiers())) {
                classes.add(clazz);
              }
            } catch (ClassNotFoundException var13) {
              success = false;
              _log.error("Scripts: Can't load script class: " + name, var13);
            }
          }
        }

        classLoader.clear();
      }

      return success;
    }
  }

  private void addHandlers(Class<?> clazz) {
    try {
      Method[] var2 = clazz.getMethods();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
        Method method = var2[var4];
        if (method.getName().contains("DialogAppend_")) {
          Integer id = Integer.parseInt(method.getName().substring(13));
          List<Scripts.ScriptClassAndMethod> handlers = (List)dialogAppends.get(id);
          if (handlers == null) {
            handlers = new ArrayList();
            dialogAppends.put(id, handlers);
          }

          ((List)handlers).add(new Scripts.ScriptClassAndMethod(clazz.getName(), method.getName()));
        } else {
          String name;
          if (method.getName().contains("OnAction_")) {
            name = method.getName().substring(9);
            onAction.put(name, new Scripts.ScriptClassAndMethod(clazz.getName(), method.getName()));
          } else if (method.getName().contains("OnActionShift_")) {
            name = method.getName().substring(14);
            onActionShift.put(name, new Scripts.ScriptClassAndMethod(clazz.getName(), method.getName()));
          }
        }
      }
    } catch (Exception var8) {
      _log.error("", var8);
    }

  }

  private void removeHandlers(Class<?> script) {
    try {
      Iterator var2 = dialogAppends.values().iterator();

      while(var2.hasNext()) {
        List<Scripts.ScriptClassAndMethod> entry = (List)var2.next();
        List<Scripts.ScriptClassAndMethod> toRemove = new ArrayList();
        Iterator var5 = entry.iterator();

        Scripts.ScriptClassAndMethod sc;
        while(var5.hasNext()) {
          sc = (Scripts.ScriptClassAndMethod)var5.next();
          if (sc.className.equals(script.getName())) {
            toRemove.add(sc);
          }
        }

        var5 = toRemove.iterator();

        while(var5.hasNext()) {
          sc = (Scripts.ScriptClassAndMethod)var5.next();
          entry.remove(sc);
        }
      }

      List<String> toRemove = new ArrayList();
      Iterator var9 = onAction.entrySet().iterator();

      Entry entry;
      while(var9.hasNext()) {
        entry = (Entry)var9.next();
        if (((Scripts.ScriptClassAndMethod)entry.getValue()).className.equals(script.getName())) {
          toRemove.add(entry.getKey());
        }
      }

      var9 = toRemove.iterator();

      String key;
      while(var9.hasNext()) {
        key = (String)var9.next();
        onAction.remove(key);
      }

      toRemove = new ArrayList();
      var9 = onActionShift.entrySet().iterator();

      while(var9.hasNext()) {
        entry = (Entry)var9.next();
        if (((Scripts.ScriptClassAndMethod)entry.getValue()).className.equals(script.getName())) {
          toRemove.add(entry.getKey());
        }
      }

      var9 = toRemove.iterator();

      while(var9.hasNext()) {
        key = (String)var9.next();
        onActionShift.remove(key);
      }
    } catch (Exception var7) {
      _log.error("", var7);
    }

  }

  public Object callScripts(String className, String methodName) {
    return this.callScripts((Player)null, className, methodName, (Object[])null, (Map)null);
  }

  public Object callScripts(String className, String methodName, Object[] args) {
    return this.callScripts((Player)null, className, methodName, args, (Map)null);
  }

  public Object callScripts(String className, String methodName, Map<String, Object> variables) {
    return this.callScripts((Player)null, className, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, variables);
  }

  public Object callScripts(String className, String methodName, Object[] args, Map<String, Object> variables) {
    return this.callScripts((Player)null, className, methodName, args, variables);
  }

  public Object callScripts(Player caller, String className, String methodName) {
    return this.callScripts(caller, className, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, (Map)null);
  }

  public Object callScripts(Player caller, String className, String methodName, Object[] args) {
    return this.callScripts(caller, className, methodName, args, (Map)null);
  }

  public Object callScripts(Player caller, String className, String methodName, Map<String, Object> variables) {
    return this.callScripts(caller, className, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, variables);
  }

  public Object callScripts(Player caller, String className, String methodName, Object[] args, Map<String, Object> variables) {
    Class<?> clazz = (Class)this._classes.get(className);
    if (clazz == null) {
      _log.error("Script class " + className + " not found!");
      return null;
    } else {
      Object o;
      try {
        o = clazz.newInstance();
      } catch (Exception var13) {
        _log.error("Scripts: Failed creating instance of " + clazz.getName(), var13);
        return null;
      }

      Iterator field;
      if (variables != null && !variables.isEmpty()) {
        field = variables.entrySet().iterator();

        while(field.hasNext()) {
          Entry param = (Entry)field.next();

          try {
            FieldUtils.writeField(o, (String)param.getKey(), param.getValue());
          } catch (Exception var12) {
            _log.error("Scripts: Failed setting fields for " + clazz.getName(), var12);
          }
        }
      }

      if (caller != null) {
        try {
          field = null;
          Field field;
          if ((field = FieldUtils.getField(clazz, "self")) != null) {
            FieldUtils.writeField(field, o, caller.getRef());
          }
        } catch (Exception var11) {
          _log.error("Scripts: Failed setting field for " + clazz.getName(), var11);
        }
      }

      Object ret = null;

      try {
        Class<?>[] parameterTypes = new Class[args.length];

        for(int i = 0; i < args.length; ++i) {
          parameterTypes[i] = args[i] != null ? args[i].getClass() : null;
        }

        ret = MethodUtils.invokeMethod(o, methodName, args, parameterTypes);
      } catch (NoSuchMethodException var14) {
        _log.error("Scripts: No such method " + clazz.getName() + "." + methodName + "()!");
      } catch (InvocationTargetException var15) {
        _log.error("Scripts: Error while calling " + clazz.getName() + "." + methodName + "()", var15.getTargetException());
      } catch (Exception var16) {
        _log.error("Scripts: Failed calling " + clazz.getName() + "." + methodName + "()", var16);
      }

      return ret;
    }
  }

  public Map<String, Class<?>> getClasses() {
    return this._classes;
  }

  public static class ScriptClassAndMethod {
    public final String className;
    public final String methodName;

    public ScriptClassAndMethod(String className, String methodName) {
      this.className = className;
      this.methodName = methodName;
    }
  }
}
