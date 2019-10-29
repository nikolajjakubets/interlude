package l2.authserver.network.l2.s2c;

import l2.authserver.network.l2.L2LoginClient;
import l2.commons.net.nio.impl.SendablePacket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class L2LoginServerPacket extends SendablePacket<L2LoginClient> {

    public L2LoginServerPacket() {
    }

    public final boolean write() {
        try {
            this.writeImpl();
            return true;
        } catch (Exception e) {
          log.error("write: eMessage={}, eClass={}", e.getMessage(), e.getClass());
          log.error("write: Client: " + this.getClient() + " - Failed writing: " + this.getClass().getSimpleName() + "!", e);
            return false;
        }
    }

    protected abstract void writeImpl();
}
