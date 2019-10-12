package l2.commons.compiler;

import java.io.IOException;
import java.net.URI;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject.Kind;

public class MemoryJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private MemoryClassLoader cl;

    public MemoryJavaFileManager(StandardJavaFileManager sjfm, MemoryClassLoader xcl) {
        super(sjfm);
        this.cl = xcl;
    }

    public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
        MemoryByteCode mbc = new MemoryByteCode(className.replace('/', '.').replace('\\', '.'), URI.create("file:///" + className.replace('.', '/').replace('\\', '/') + kind.extension));
        this.cl.addClass(mbc);
        return mbc;
    }

    public ClassLoader getClassLoader(Location location) {
        return this.cl;
    }
}
