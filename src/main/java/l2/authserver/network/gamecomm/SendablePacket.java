package l2.authserver.network.gamecomm;

import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SendablePacket extends l2.commons.net.nio.SendablePacket<GameServer> {
    private static final Logger _log = LoggerFactory.getLogger(SendablePacket.class);
    protected GameServer _gs;
    protected ByteBuffer _buf;

    public SendablePacket() {
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

    public boolean write() {
        try {
            this.writeImpl();
        } catch (Exception var2) {
            _log.error("", var2);
        }

        return true;
    }

    protected abstract void writeImpl();
}
