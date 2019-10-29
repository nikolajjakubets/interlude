//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.scripts;

/**
 * @Author: Death
 * @Date: 23/6/2007
 * @Time: 9:22:07
 * <p/>
 * Просто интерфейс с методами которые обязательно должны использоваться в скриптах.
 */
public interface ScriptFile {
  /**
   * Вызывается при загрузке классов скриптов
   */
  void onLoad();

  /**
   * Вызывается при перезагрузке После перезагрузки onLoad() вызывается автоматически
   */
  void onReload();

  /**
   * Вызывается при выключении сервера
   */
  void onShutdown();
}
