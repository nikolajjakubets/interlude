package l2.commons.compiler;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaCompiler.CompilationTask;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.eclipse.jdt.internal.compiler.tool.EclipseFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Compiler {
    private static final Logger _log = LoggerFactory.getLogger(Compiler.class);
    private static final JavaCompiler javac = new EclipseCompiler();
    private final DiagnosticListener<JavaFileObject> listener = new Compiler.DefaultDiagnosticListener();
    private final StandardJavaFileManager fileManager = new EclipseFileManager(Locale.getDefault(), Charset.defaultCharset());
    private final MemoryClassLoader memClassLoader = new MemoryClassLoader();
    private final MemoryJavaFileManager memFileManager;

    public Compiler() {
        this.memFileManager = new MemoryJavaFileManager(this.fileManager, this.memClassLoader);
    }

    public boolean compile(File... files) {
        List<String> options = new ArrayList<>();
        options.add("-Xlint:all");
        options.add("-warn:none");
        options.add("-g");
        Writer writer = new StringWriter();
        CompilationTask compile = javac.getTask(writer, this.memFileManager, this.listener, options, (Iterable)null, this.fileManager.getJavaFileObjects(files));
        return compile.call();
    }

    public boolean compile(Collection<File> files) {
        return this.compile((File[])files.toArray(new File[files.size()]));
    }

    public MemoryClassLoader getClassLoader() {
        return this.memClassLoader;
    }

    private class DefaultDiagnosticListener implements DiagnosticListener<JavaFileObject> {
        private DefaultDiagnosticListener() {
        }

        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
            Compiler._log.error(((JavaFileObject)diagnostic.getSource()).getName() + (diagnostic.getPosition() == -1L ? "" : ":" + diagnostic.getLineNumber() + "," + diagnostic.getColumnNumber()) + ": " + diagnostic.getMessage(Locale.getDefault()));
        }
    }
}
