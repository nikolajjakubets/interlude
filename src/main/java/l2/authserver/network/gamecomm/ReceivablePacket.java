package l2.authserver.network.gamecomm;


import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ReceivablePacket extends l2.commons.net.nio.ReceivablePacket<GameServer> {
    private static final Logger _log = LoggerFactory.getLogger(ReceivablePacket.class);
    protected GameServer _gs;
    protected ByteBuffer _buf;

    public ReceivablePacket() {
    }

    protected void setByteBuffer(ByteBuffer buf) {
        this._buf = buf;
    }

    protected ByteBuffer getByteBuffer() {
        return this._buf;
    }

    protected void setClient(GameServer gs) {
        this._gs = gs;
    }

    public GameServer getClient() {
        return this._gs;
    }

    public GameServer getGameServer() {
        return this.getClient();
    }

    public final boolean read() {
        try {
            this.readImpl();
        } catch (Exception var2) {
            _log.error("", var2);
        }

        return true;
    }

    public final void run() {
        try {
            this.runImpl();
        } catch (Exception var2) {
            _log.error("", var2);
        }

    }

    protected abstract void readImpl();

    protected abstract void runImpl();

    public void sendPacket(SendablePacket packet) {
        this.getGameServer().sendPacket(packet);
    }
}
