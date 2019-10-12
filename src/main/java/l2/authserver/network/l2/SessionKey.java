package l2.authserver.network.l2;


import l2.commons.util.Rnd;

public class SessionKey {
    public final int playOkID1;
    public final int playOkID2;
    public final int loginOkID1;
    public final int loginOkID2;
    private final int hashCode;

    public SessionKey(int loginOK1, int loginOK2, int playOK1, int playOK2) {
        this.playOkID1 = playOK1;
        this.playOkID2 = playOK2;
        this.loginOkID1 = loginOK1;
        this.loginOkID2 = loginOK2;
        int hashCode = playOK1 * 17;
        hashCode += playOK2;
        hashCode *= 37;
        hashCode += loginOK1;
        hashCode *= 51;
        hashCode += loginOK2;
        this.hashCode = hashCode;
    }

    public boolean checkLoginPair(int loginOk1, int loginOk2) {
        return this.loginOkID1 == loginOk1 && this.loginOkID2 == loginOk2;
    }

    public static final SessionKey create() {
        return new SessionKey(Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o.getClass() != this.getClass()) {
            return false;
        } else {
            SessionKey skey = (SessionKey)o;
            return this.playOkID1 == skey.playOkID1 && this.playOkID2 == skey.playOkID2 && skey.checkLoginPair(this.loginOkID1, this.loginOkID2);
        }
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        return "[playOkID1: " + this.playOkID1 + " playOkID2: " + this.playOkID2 + " loginOkID1: " + this.loginOkID1 + " loginOkID2: " + this.loginOkID2 + "]";
    }
}
