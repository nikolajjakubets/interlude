//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.net.nio.impl;

import java.nio.ByteBuffer;

public abstract class MMOClient<T extends MMOConnection> {
    private T _connection;
    private boolean isAuthed;

    public MMOClient(T con) {
        this._connection = con;
    }

    protected void setConnection(T con) {
        this._connection = con;
    }

    public T getConnection() {
        return this._connection;
    }

    public boolean isAuthed() {
        return this.isAuthed;
    }

    public void setAuthed(boolean isAuthed) {
        this.isAuthed = isAuthed;
    }

    public void closeNow(boolean error) {
        if (this.isConnected()) {
            this._connection.closeNow();
        }

    }

    public void closeLater() {
        if (this.isConnected()) {
            this._connection.closeLater();
        }

    }

    public boolean isConnected() {
        return this._connection != null && !this._connection.isClosed();
    }

    public abstract boolean decrypt(ByteBuffer var1, int var2);

    public abstract boolean encrypt(ByteBuffer var1, int var2);

    protected void onDisconnection() {
    }

    protected void onForcedDisconnection() {
    }
}
