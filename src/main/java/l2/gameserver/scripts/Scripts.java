//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.scripts;

import l2.commons.compiler.Compiler;
import l2.commons.compiler.MemoryClassLoader;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.Quest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

@Slf4j
public class Scripts {
  private static final Scripts _instance = new Scripts();
  public static final Map<Integer, List<Scripts.ScriptClassAndMethod>> dialogAppends = new HashMap<>();
  public static final Map<String, Scripts.ScriptClassAndMethod> onAction = new HashMap<>();
  public static final Map<String, Scripts.ScriptClassAndMethod> onActionShift = new HashMap<>();
  private final Compiler compiler = new Compiler();
  private final Map<String, Class<?>> _classes = new TreeMap<>();

  public static Scripts getInstance() {
    return _instance;
  }

  private Scripts() {
    this.load();
    this.loadExt();
  }

  private void load() {
    log.info("Scripts: Loading...");
    List<Class<?>> classes = new ArrayList<>();
    boolean result = false;
    File f = new File("scripts.jar");
    if (f.exists()) {
      JarInputStream stream = null;
      MemoryClassLoader classLoader = new MemoryClassLoader();

      try {
        stream = new JarInputStream(new FileInputStream(f));
        JarEntry entry;

        while((entry = stream.getNextJarEntry()) != null) {
          if (!entry.getName().contains(ClassUtils.INNER_CLASS_SEPARATOR) && entry.getName().endsWith(".class")) {
            String name = entry.getName().replace(".class", "").replace("/", ".");
            Class<?> aClass = getClass().getClassLoader().loadClass(name);
//            Class<?> clazz = classLoader.loadClass(name);
            if (!Modifier.isAbstract(aClass.getModifiers())) {
              classes.add(aClass);
            }
          }
        }

        result = true;
      } catch (Exception var12) {
        log.error("Fail to load scripts.jar!", var12);
        classes.clear();
      } finally {
        IOUtils.closeQuietly(stream);
      }
    }

    if (!result) {
      result = this.load(classes, "");
    }

    if (!result) {
      log.error("Scripts: Failed loading scripts!");
      Runtime.getRuntime().exit(0);
    } else {
      log.info("Scripts: Loaded " + classes.size() + " classes.");

      for (Class<?> clazz : classes) {
        this._classes.put(clazz.getName(), clazz);
      }

    }
  }

  private void loadExt() {
    log.info("loadExt: Extensions: Loading...");
    List<Class<?>> classes = new ArrayList<>();
    boolean result = false;
    File[] extFiles = (new File(".")).listFiles(pathname -> pathname.getName().endsWith(".ext.jar"));
    int i = 0;
    if (extFiles != null) {
      i = extFiles.length;
    }else {
      log.error("loadExt: Extensions: error load!, extFiles empty");
    }

    for(int var6 = 0; var6 < i; ++var6) {
      File extFile = extFiles[var6];
      if (extFile.exists()) {
        JarInputStream stream = null;
        MemoryClassLoader classLoader = new MemoryClassLoader();

        try {
          stream = new JarInputStream(new FileInputStream(extFile));
          JarEntry entry;

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
          log.error("Failed to load \"" + extFile + "\"!", var16);
          classes.clear();
        } finally {
          IOUtils.closeQuietly(stream);
        }
      }
    }

    if (!result) {
      this.load(classes, "");
    }

    log.info("Extensions: Loaded " + classes.size() + " extension classes.");

    for(i = 0; i < classes.size(); ++i) {
      Class<?> clazz = classes.get(i);
      this._classes.put(clazz.getName(), clazz);
    }

  }

  /**
   * Вызывается при загрузке сервера. Инициализирует объекты и обработчики.
   */
  public void init() {
    for (Class<?> clazz : _classes.values()) {
      addHandlers(clazz);

      if (Config.DONTLOADQUEST)
        if (ClassUtils.isAssignable(clazz, Quest.class))
          continue;

      if (ClassUtils.isAssignable(clazz, ScriptFile.class))
        try {
          ((ScriptFile) clazz.newInstance()).onLoad();
        } catch (Exception e) {
          log.error("Scripts: Failed running " + clazz.getName() + ".onLoad()", e);
        }
    }
  }

  public boolean reload() {
    log.info("Scripts: Reloading...");
    return this.reload("");
  }

  public boolean reload(String target) {
    List<Class<?>> classes = new ArrayList<>();
    if (!this.load(classes, target)) {
      log.error("Scripts: Failed reloading script(s): " + target + "!");
      return false;
    } else {
      log.info("Scripts: Reloaded " + classes.size() + " classes.");

      for (Class<?> clazz : classes) {
        Class<?> prevClazz = this._classes.put(clazz.getName(), clazz);
        if (prevClazz != null) {
          if (ClassUtils.isAssignable(prevClazz, ScriptFile.class)) {
            try {
              ((ScriptFile) prevClazz.newInstance()).onReload();
            } catch (Exception var7) {
              log.error("Scripts: Failed running " + prevClazz.getName() + ".onReload()", var7);
            }
          }

          this.removeHandlers(prevClazz);
        }

        if (!Config.DONTLOADQUEST || !ClassUtils.isAssignable(clazz, Quest.class)) {
          if (ClassUtils.isAssignable(clazz, ScriptFile.class)) {
            try {
              ((ScriptFile) clazz.newInstance()).onLoad();
            } catch (Exception var8) {
              log.error("Scripts: Failed running " + clazz.getName() + ".onLoad()", var8);
            }
          }

          this.addHandlers(clazz);
        }
      }

      return true;
    }
  }

  public void shutdown() {

    for (Class<?> clazz : this._classes.values()) {
      if (!ClassUtils.isAssignable(clazz, Quest.class) && ClassUtils.isAssignable(clazz, ScriptFile.class)) {
        try {
          ((ScriptFile) clazz.newInstance()).onShutdown();
        } catch (Exception var4) {
          log.error("Scripts: Failed running " + clazz.getName() + ".onShutdown()", var4);
        }
      }
    }

  }

  private boolean load(List<Class<?>> classes, String target) {
    Collection<File> scriptFiles = Collections.emptyList();
    File file = new File(Config.DATAPACK_ROOT, "data/scripts/" + target.replace(".", "/") + ".java");
    if (file.isFile()) {
      scriptFiles = new ArrayList<>(1);
      scriptFiles.add(file);
    } else {
      file = new File(Config.DATAPACK_ROOT, "data/scripts/" + target);
      if (file.isDirectory()) {
        scriptFiles = FileUtils.listFiles(file, FileFilterUtils.suffixFileFilter(".java"), FileFilterUtils.directoryFileFilter());
      }
    }

    if (scriptFiles.isEmpty()) {
      return false;
    } else {
      boolean success;
      if (success = this.compiler.compile(scriptFiles)) {
        MemoryClassLoader classLoader = this.compiler.getClassLoader();
        String[] var8 = classLoader.getLoadedClasses();

        for (String name : var8) {
          if (!name.contains(ClassUtils.INNER_CLASS_SEPARATOR)) {
            try {
              Class<?> clazz = classLoader.loadClass(name);
              if (!Modifier.isAbstract(clazz.getModifiers())) {
                classes.add(clazz);
              }
            } catch (ClassNotFoundException var13) {
              success = false;
              log.error("Scripts: Can't load script class: " + name, var13);
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

      for (Method method : var2) {
        if (method.getName().contains("DialogAppend_")) {
          Integer id = Integer.parseInt(method.getName().substring(13));
          List<ScriptClassAndMethod> handlers = dialogAppends.computeIfAbsent(id, k -> new ArrayList<>());

          handlers.add(new ScriptClassAndMethod(clazz.getName(), method.getName()));
        } else {
          String name;
          if (method.getName().contains("OnAction_")) {
            name = method.getName().substring(9);
            onAction.put(name, new ScriptClassAndMethod(clazz.getName(), method.getName()));
          } else if (method.getName().contains("OnActionShift_")) {
            name = method.getName().substring(14);
            onActionShift.put(name, new ScriptClassAndMethod(clazz.getName(), method.getName()));
          }
        }
      }
    } catch (Exception var8) {
      log.error("", var8);
    }

  }

  private void removeHandlers(Class<?> script) {
    try {

      for (List<ScriptClassAndMethod> entry : dialogAppends.values()) {
//        List<ScriptClassAndMethod> entry = (List) scriptClassAndMethods;
        List<ScriptClassAndMethod> toRemove = new ArrayList<>();
        Iterator var5 = entry.iterator();

        ScriptClassAndMethod sc;
        while (var5.hasNext()) {
          sc = (ScriptClassAndMethod) var5.next();
          if (sc.className.equals(script.getName())) {
            toRemove.add(sc);
          }
        }

        var5 = toRemove.iterator();

        while (var5.hasNext()) {
          sc = (ScriptClassAndMethod) var5.next();
          entry.remove(sc);
        }
      }

      List<String> toRemove = new ArrayList<>();
      Iterator<Entry<String, ScriptClassAndMethod>> var9 = onAction.entrySet().iterator();
      Entry<String, ScriptClassAndMethod> entry;
      while(var9.hasNext()) {
        entry = var9.next();
        if (entry.getValue().className.equals(script.getName())) {
          toRemove.add(entry.getKey());
        }
      }

      Iterator<String> stringIterator = toRemove.iterator();

      String key;
      while(stringIterator.hasNext()) {
        key = stringIterator.next();
        onAction.remove(key);
      }

      toRemove = new ArrayList<>();

      for (Entry<String, ScriptClassAndMethod> stringScriptClassAndMethodEntry : onActionShift.entrySet()) {
        entry = stringScriptClassAndMethodEntry;
        if (entry.getValue().className.equals(script.getName())) {
          toRemove.add(entry.getKey());
        }
      }

      for (String nameToRemove : toRemove) {
        onActionShift.remove(nameToRemove);
      }
    } catch (Exception e) {
      log.error("removeHandlers: eMessage={}, eClause={}", e.getMessage(), e.getClass());
    }

  }

  public Object callScripts(String className, String methodName) {
    return this.callScripts(null, className, methodName, null, null);
  }

  public Object callScripts(String className, String methodName, Object[] args) {
    return this.callScripts(null, className, methodName, args, null);
  }

  public Object callScripts(String className, String methodName, Map<String, Object> variables) {
    return this.callScripts(null, className, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, variables);
  }

  public Object callScripts(String className, String methodName, Object[] args, Map<String, Object> variables) {
    return this.callScripts(null, className, methodName, args, variables);
  }

  public Object callScripts(Player caller, String className, String methodName) {
    return this.callScripts(caller, className, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, null);
  }

  public Object callScripts(Player caller, String className, String methodName, Object[] args) {
    return this.callScripts(caller, className, methodName, args, null);
  }

  public Object callScripts(Player caller, String className, String methodName, Map<String, Object> variables) {
    return this.callScripts(caller, className, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, variables);
  }

  public Object callScripts(Player caller, String className, String methodName, Object[] args, Map<String, Object> variables) {
    Class<?> clazz = this._classes.get(className);
    if (clazz == null) {
      log.error("Script class " + className + " not found!");
      return null;
    } else {
      Object o;
      try {
        o = clazz.newInstance();
      } catch (Exception var13) {
        log.error("Scripts: Failed creating instance of " + clazz.getName(), var13);
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
            log.error("Scripts: Failed setting fields for " + clazz.getName(), var12);
          }
        }
      }

      if (caller != null) {
        try {
//          field = null;
          Field fieldSelf;
          if ((fieldSelf = FieldUtils.getField(clazz, "self")) != null) {
            FieldUtils.writeField(fieldSelf, o, caller.getRef());
          }
        } catch (Exception var11) {
          log.error("Scripts: Failed setting field for " + clazz.getName(), var11);
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
        log.error("Scripts: No such method " + clazz.getName() + "." + methodName + "()!");
      } catch (InvocationTargetException var15) {
        log.error("Scripts: Error while calling " + clazz.getName() + "." + methodName + "()", var15.getTargetException());
      } catch (Exception var16) {
        log.error("Scripts: Failed calling " + clazz.getName() + "." + methodName + "()", var16);
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
